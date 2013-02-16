package openpool;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import openpool.config.BallDetectorConfigHandler;
import openpool.config.ConfigHandler;
import openpool.config.FieldConfigHandler;
import openpool.config.CameraPositionConfigHandler;
import openpool.config.PoolAreaConfigHandler;

import SimpleOpenNI.*;
import processing.core.*;

public class OpenPool {
	public PApplet pa;
	public Ball[] balls;
	public int nBalls;
	public Point[] PoolArea = {new Point(100,100),new Point (100+540,100+220)};

	private BallSystem ballSystem;

	private Point[] depthImageCorners = { new Point(100, 100), new Point(100 + 640, 100 + 240) };
	private BallDetector ballDetector;
	private ScheduledFuture<?> future;

	private SimpleOpenNI cam1, cam2;

	private ConfigHandler[] configHandlers;
	private int currentModeIndex = 0;

	/**
	 * True if it's in debug mode.
	 */
	private boolean isConfigMode;

	/**
	 * @see #message
	 */
	private int messageLife = 15;

	/**
	 * Counter for showing the message.
	 * 
	 * @see #message
	 */
	private int messageCounter = 0;
	
	/**
	 * Config handler message.
	 */
	private String message = "";

	/**
	 * Use two real cameras.
	 * @param pa Java Applet instance
	 */
	public OpenPool(PApplet pa) {
		this(pa, 2);
	}

	/**
	 * Use one or two real cameras.
	 * @param pa Java Applet instance
	 * @param numCamera Number of real cameras (1 or 2)
	 */
	public OpenPool(PApplet pa, int numCamera) {
		this(pa, numCamera, null, null);
	}

	/**
	 * Use one virtual camera.
	 * @param pa
	 * @param cam1FileName File name of OpenNI recording
	 */
	public OpenPool(PApplet pa, String cam1FileName) {
		this(pa, 1, cam1FileName, null);
	}

	/**
	 * Use two virtual cameras.
	 * @param pa
	 * @param cam1FileName File name of OpenNI recording for the first camera
	 * @param cam2FileName File name of OpenNI recording for the second camera
	 */
	public OpenPool(PApplet pa, String cam1FileName, String cam2FileName) {
		this(pa, 2, cam1FileName, cam2FileName);
	}

	/**
	 * Full constructor. The user is recommended to call other constructors for ease of use.
	 * @param pa
	 * @param numCamera Number of cameras
	 * @param cam1FileName File name of OpenNI recording for the first camera
	 * @param cam2FileName File name of OpenNI recording for the second camera
	 */
	public OpenPool(PApplet pa, int numCamera, String cam1FileName, String cam2FileName) {
		this.pa = pa;

		pa.hint(PApplet.ENABLE_OPENGL_4X_SMOOTH);
		
		drawSplashScreen();
		
		pa.registerMouseEvent(this);
		pa.registerKeyEvent(this);
		pa.registerDispose(this);
		pa.registerPre(this);
		
		// For Processing 2.0b
		//	Turn on 4X antialiasing
		//smooth(4);
		//	Register event handlers.
		//pa.registerMethod("mouseEvent", this);
		//pa.registerMethod("keyEvent", this);
		//pa.registerMethod("dispose", this);
		//pa.registerMethod("pre", this);

		// Initialize OpenNI drivers.
		initOpenNI(numCamera, cam1FileName, cam2FileName);

		// Start the ball detector.
		initBallDetector();
		
		// Initialize Config handlers.
		initConfigHandlers();
	}
	
	private void initConfigHandlers() {
		configHandlers = new ConfigHandler[] {
				new FieldConfigHandler(this),
				new BallDetectorConfigHandler(this, ballDetector),
				new CameraPositionConfigHandler(this, ballDetector),
				new PoolAreaConfigHandler(this)
		};
	}

	private void initOpenNI(int numCamera, String cam1FileName, String cam2FileName) {
		SimpleOpenNI.start(); 
		// print all the cams 
		StrVector strList = new StrVector();
		SimpleOpenNI.deviceNames(strList);
		for(int i=0;i<strList.size();i++)
			pa.println(i + ":" + strList.get(i));

		if (numCamera == 1) {
			cam1 = new SimpleOpenNI(pa);
			cam2 = null;
		} else if (numCamera == 2) {
			cam1 = new SimpleOpenNI(1, pa);
			cam2 = new SimpleOpenNI(0, pa);
		} else if (numCamera > 2) {
			throw new IllegalStateException(
					"Number of cameras are limited up to two.");
		}

		// Load recording files if needed.
		if (cam1 != null && cam1FileName != null && !cam1.openFileRecording(cam1FileName)) {
			throw new IllegalStateException(
					"Can't open the recording file for the first camera: " + cam1FileName);
		}
		if (cam2 != null && cam2FileName != null && !cam2.openFileRecording(cam2FileName)) {
			throw new IllegalStateException(
					"Can't open the recording file for the second camera: " + cam2FileName);
		}

		// Check if depth streams are available.
		if (cam1 != null && !cam1.enableDepth()) {
			throw new IllegalStateException(
					"Can't open the depth map of the first camera; check the camera connection.");
		}
		if (cam2 != null && !cam2.enableDepth()) {
			throw new IllegalStateException(
					"Can't open the depth map of the second camera; check the camera connection.");
		}
	}
	
	private void initBallDetector() {
		ballSystem = new BallSystem(this);
		ballDetector = new BallDetector(ballSystem, cam1, cam2, pa);
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		future = ses.scheduleAtFixedRate(ballDetector, 33, 33, TimeUnit.MILLISECONDS);
	}

	/**
	 * This method is called BEFORE draw() of the processing sketch.
	 */
	public void pre() {
		if (!isConfigMode) {
			return;
		}

		pa.colorMode(PApplet.RGB, 255);
		pa.background(0);

		synchronized (ballDetector) {
			pa.image(ballDetector.getImage(),
					depthImageCorners[0].x, depthImageCorners[0].y,
					depthImageCorners[1].x - depthImageCorners[0].x, depthImageCorners[1].y - depthImageCorners[0].y);
		}
		configHandlers[currentModeIndex].draw();

		pa.noStroke();
		pa.fill(255, 100, 100);
		pa.text(configHandlers[currentModeIndex].getTitle(), 10, 20);
		pa.text("Press left or right arrow to switch between config modes.", 10, 36);
		if (messageCounter -- > 0) {
			pa.text(message, 10, 52);
		}
	}

	public void mouseEvent(MouseEvent e) {
		if (!isConfigMode) {
			return;
		}
		configHandlers[currentModeIndex].mouseEvent(e);
	}

	public void keyEvent(KeyEvent e) {
		if (!isConfigMode) {
			return;
		}
		
		// Switch between config modes.
		if (e.getID() == KeyEvent.KEY_RELEASED) {
			switch(e.getKeyCode()) {
			case java.awt.event.KeyEvent.VK_RIGHT:
			case java.awt.event.KeyEvent.VK_NUMPAD6:
				currentModeIndex = (currentModeIndex + 1) % configHandlers.length;
				break;
			case java.awt.event.KeyEvent.VK_LEFT:
			case java.awt.event.KeyEvent.VK_NUMPAD4:
				currentModeIndex = (currentModeIndex + configHandlers.length - 1) % configHandlers.length;
				break;
			}
		}
		configHandlers[currentModeIndex].keyEvent(e);
	}
	
	public void dispose() {
		future.cancel(true);
		ballDetector.dispose();
		if (cam1 != null) {
			cam1.dispose();
		}
		if (cam2 != null) {
			cam2.dispose();
		}
	}

	// Getters and setters follow:

	public int getMessageLife() {
		return messageLife;
	}

	public void setMessageLife(int messageLife) {
		this.messageLife = messageLife;
	}

	public int getWidth() { return pa.getWidth(); }
	
	public int getHeight() { return pa.getHeight(); }
	
	// Main API follow:
	
	public void updateBalls() {
		synchronized (ballDetector) {
			this.balls = ballSystem.getBalls();
			this.nBalls = balls.length;
		}
	}

	public boolean isConfigMode() {
		return isConfigMode;
	}

	public void setConfigMode(boolean isConfigMode) {
		this.isConfigMode = isConfigMode;
	}
	
	public boolean isDummyMode() {
		return ballSystem.isDummy();
	}
	
	public void setDummyMode(boolean isDummy) {
		ballSystem.setDummy(isDummy);
	}

	public Point getDepthImageCorner(int index) { return depthImageCorners[index]; }
		
	public Point getTopLeftCorner() { return depthImageCorners[0]; }
	
	public Point getBottomRightCorner() { return depthImageCorners[1]; }
	
	public int getFieldWidth() { return depthImageCorners[1].x - depthImageCorners[0].x; }

	public int getFieldHeight() { return depthImageCorners[1].y - depthImageCorners[0].y; }
	
	// Utility methods follow:
	
	public void rememberBackground() {
		ballDetector.rememberBackground();
	}

	public void setMessage(String message) {
		this.message = message;
		messageCounter = messageLife;
	}

	public float depthToScreenX(float x) {
		return getTopLeftCorner().x + getFieldWidth() * x / ballDetector.getDepthWidth();
	}

	public float depthToScreenY(float y) {
		return getTopLeftCorner().y + getFieldHeight() * y / ballDetector.getDepthHeight();
	}

	public float depthToScreenWidth(float width) {
		return getFieldWidth() * width / ballDetector.getDepthWidth();
	}

	public float depthToScreenHeight(float height) {
		return getFieldHeight() * height / ballDetector.getDepthHeight();
	}

	private void drawSplashScreen(){
		
		// First, look for the logo file.
		BufferedImage logo = null;
		try {
			logo = ImageIO.read(new File(pa.dataPath("openpool.jpg")));
		} catch (IOException e1) {
			String userDir = System.getProperty("user.dir");
			String binPath = File.separatorChar + "bin";
			if (userDir.endsWith(binPath)) {
				userDir = userDir.substring(0, userDir.length() - binPath.length());
			}
			try {
				logo = ImageIO.read(new File(
						userDir + File.separatorChar + "data" + File.separatorChar + "openpool.jpg"));
			} catch (IOException e2) {
				// Do nothing, just give up and return.
				return;
			}
		}

		// Next, import the image into the Proccessing world.
		BufferedImage logoArgb = new BufferedImage(logo.getWidth(), logo.getHeight(), BufferedImage.TYPE_INT_ARGB);
		logoArgb.getGraphics().drawImage(logo, 0, 0, null);
		PImage image = new PImage(logoArgb);
		
		// Finally, show it.
		pa.background(255);
		pa.image(image, (pa.width - image.width)/2, (pa.height - image.height)/2);
	}
}
