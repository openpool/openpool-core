package openpool;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import openpool.config.BallDetectorConfigHandler;
import openpool.config.ConfigHandler;
import openpool.config.FieldConfigHandler;

import SimpleOpenNI.*;
import processing.core.*;

public class OpenPool {
	public PApplet pa;
	public Ball[] balls;
	public int nBalls;

	private BallSystem ballSystem;

	private Point[] corners = { new Point(100, 100), new Point(100 + 640, 100 + 240) };
	
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
		this(pa, 2, null, null);
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

		// Set window size.
		pa.size(498*2, 282*2);
		pa.hint(PApplet.ENABLE_OPENGL_4X_SMOOTH); // Turn on 4X antialiasing
		
		// Register event handlers.
		pa.registerMouseEvent(this);
		pa.registerKeyEvent(this);
		pa.registerDispose(this);
		pa.registerPre(this);

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
				new BallDetectorConfigHandler(this, ballDetector)
		};
	}

	private void initOpenNI(int numCamera, String cam1FileName, String cam2FileName) {
		if (numCamera <= 0) {
			throw new IllegalStateException(
					"Use one or two cameras.");
		} else if (numCamera == 1) {
			cam1 = new SimpleOpenNI(pa);
			cam2 = null;
		} else if (numCamera == 2) {
			cam1 = new SimpleOpenNI(1, pa);
			cam2 = new SimpleOpenNI(0, pa);
		} else {
			throw new IllegalStateException(
					"Number of cameras are limited up to two.");
		}

		// Load recording files if needed.
		if (cam1FileName != null && !cam1.openFileRecording(cam1FileName)) {
			throw new IllegalStateException(
					"Can't open the recording file for the first camera: " + cam1FileName);
		}
		if (cam2 != null && cam2FileName != null && !cam2.openFileRecording(cam2FileName)) {
			throw new IllegalStateException(
					"Can't open the recording file for the second camera: " + cam2FileName);
		}

		// Check if depth streams are available.
		if (!cam1.enableDepth()) {
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
		ballDetector = new BallDetector(ballSystem, cam1, cam2);
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

		pa.fill(0, 0, 0);
		pa.rect(0, 0, getWidth(), getHeight());

		synchronized (ballDetector) {
			pa.image(new PImage(ballDetector.getImage()),
					corners[0].x, corners[0].y,
					corners[1].x - corners[0].x, corners[1].y - corners[0].y);
		}

		configHandlers[currentModeIndex].draw();
		
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
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_NUMPAD6:
				currentModeIndex = (currentModeIndex + 1) % configHandlers.length;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_NUMPAD4:
				currentModeIndex = (currentModeIndex + configHandlers.length - 1) % configHandlers.length;
				break;
			}
		}
		configHandlers[currentModeIndex].keyEvent(e);
	}
	
	public void dispose() {
		future.cancel(true);
		ballDetector.dispose();
	}

	// Getters and setters follow:

	public int getWidth() { return pa.getWidth(); }
	
	public int getHeight() { return pa.getHeight(); }
	
	public Point getCorner(int index) { return corners[index]; }
	
	public Point getTopLeftCorner() { return corners[0]; }
	
	public Point getBottomRightCorner() { return corners[1]; }
	
	public int getFieldWidth() { return corners[1].x - corners[0].x; }

	public int getFieldHeight() { return corners[1].y - corners[0].y; }
	
	public boolean isConfigMode() {
		return isConfigMode;
	}

	public void setConfigMode(boolean isConfigMode) {
		this.isConfigMode = isConfigMode;
	}

	// Main API follow:
	
	public void updateBalls() {
		synchronized (ballDetector) {
			this.balls = ballSystem.getBalls();
			this.nBalls = balls.length;
		}
	}
	
	// Utility methods follow:

	public void setMessage(String message) {
		this.message = message;
		messageCounter = 120;
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
}
