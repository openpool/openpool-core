package openpool;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import SimpleOpenNI.*;
import processing.core.*;

public class OpenPool {
	PApplet pa;
	Field field;
	BallSystem ballSystem;

	int pos[][] = { { 50, 50 }, { 640 + 50, 240 + 50 } };
	
	private BallDetector ballDetector;
	private ScheduledFuture<?> future;

	/**
	 * True if it's in debug mode.
	 */
	private boolean isDebugMode;

	/**
	 * True when camera image corner is grabbed with mouse pointer.
	 */
	int selected = -1;

	/**
	 * Background image for billiard pool.
	 */
	PImage img;

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

		pa.size(498*2, 282*2, PApplet.OPENGL);
		pa.hint(PApplet.ENABLE_OPENGL_4X_SMOOTH); // Turn on 4X antialiasing
		
		pa.registerMouseEvent(this);
		pa.registerKeyEvent(this);
		pa.registerDispose(this);

		SimpleOpenNI cam1, cam2;
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

		if (cam1FileName != null && !cam1.openFileRecording(cam1FileName)) {
			throw new IllegalStateException(
					"Can't open the recording file for the first camera: " + cam1FileName);
		}
		if (cam2 != null && cam2FileName != null && !cam2.openFileRecording(cam2FileName)) {
			throw new IllegalStateException(
					"Can't open the recording file for the second camera: " + cam2FileName);
		}
		
		if (!cam1.enableDepth()) {
			throw new IllegalStateException(
					"Can't open the depth map of the first camera; check the camera connection.");
		}
		if (cam2 != null && !cam2.enableDepth()) {
			throw new IllegalStateException(
					"Can't open the depth map of the second camera; check the camera connection.");
		}

		ballDetector = new BallDetector(cam1, cam2);
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		future = ses.scheduleAtFixedRate(ballDetector, 33, 33, TimeUnit.MILLISECONDS);
	}

	public boolean isDebugMode() {
		return isDebugMode;
	}

	public void setDebugMode(boolean isDebugMode) {
		this.isDebugMode = isDebugMode;
	}

	/**
	 * Call this from Processing code.
	 */
	public void draw() {
		if (!isDebugMode) {
			return;
		}

		/*
		 * for (Blob blob:blobs) { Point pt = new Point(); pt.x =
		 * (blob.centroid.x* (pos[1][0]-pos[0][0])) / depthImage.width +
		 * pos[0][0]; pt.y = (blob.centroid.y* (pos[1][1]-pos[0][1])) /
		 * depthImage.height + pos[0][1]; bgPoints.add(pt);
		 * 
		 * if (OpenPool.DEBUG) { pa.line(pt.x-50, pt.y, pt.x+50, pt.y);
		 * pa.line(pt.x, pt.y-50, pt.x, pt.y+50); text( str(blob.area), pt.x,
		 * pt.y-30); } }
		 */

		/*
		if (pa.mousePressed && selected >= 0) {
			pos[selected][0] = pa.mouseX;
			pos[selected][1] = pa.mouseY;
		} else {
			float minimumDistance = 20;
			selected = -1;
			for (int i = 0; i < 2; i++) {
				float d = PApplet.dist(pa.mouseX, pa.mouseY, pos[i][0], pos[i][1]);
				if (d < minimumDistance) {
					minimumDistance = d;
					selected = i;
				}
			}
		}
		if (selected >= 0) {
			pa.ellipse(pa.mouseX, pa.mouseY, 20, 20);
		}

		pa.stroke(255, 255, 0);
		pa.fill(255, 255, 0);
		// draw image boundingbox
		pa.line(pos[0][0], pos[0][1], pos[1][0], pos[0][1]);
		pa.line(pos[1][0], pos[0][1], pos[1][0], pos[1][1]);
		pa.line(pos[1][0], pos[1][1], pos[0][0], pos[1][1]);
		pa.line(pos[0][0], pos[1][1], pos[0][0], pos[0][1]);

		// draw an arrow

		pa.line(pos[0][0], pos[0][1], pos[0][0] + 8, pos[0][1] + 4);
		pa.line(pos[0][0], pos[0][1], pos[0][0] + 4, pos[0][1] + 8);
		pa.line(pos[0][0], pos[0][1], pos[0][0] + 10, pos[0][1] + 10);

		// draw xy
		pa.text("X:", pos[0][0] + 20, pos[0][1] + 20);
		pa.text(pos[0][0], pos[0][0] + 30, pos[0][1] + 20);
		pa.text("Y:", pos[0][0] + 20, pos[0][1] + 30);
		pa.text(pos[0][1], pos[0][0] + 30, pos[0][1] + 30);

		// draw an arrow
		pa.line(pos[1][0], pos[1][1], pos[1][0] - 8, pos[1][1] - 4);
		pa.line(pos[1][0], pos[1][1], pos[1][0] - 4, pos[1][1] - 8);
		pa.line(pos[1][0], pos[1][1], pos[1][0] - 10, pos[1][1] - 10);

		// draw xy
		pa.text("X:", pos[1][0] - 50, pos[1][1] - 10);
		pa.text(pos[1][0], pos[1][0] - 40, pos[1][1] - 10);
		pa.text("Y:", pos[1][0] - 50, pos[1][1] - 20);
		pa.text(pos[1][1], pos[1][0] - 40, pos[1][1] - 20);

		pa.noFill();
		pa.stroke(255, 255, 255);
		*/

		// draw depthimage
		synchronized (ballDetector) {
			pa.image(new PImage(ballDetector.currentBufferedImage), pos[0][0], pos[0][1],
				pos[1][0] - pos[0][0], pos[1][1] - pos[0][1]);
		}
	}

	public void mouseEvent(MouseEvent e) {
		if (!isDebugMode) {
			return;
		}
	}

	public void keyEvent(KeyEvent e) {
		if (!isDebugMode) {
			return;
		}
		switch (pa.key) {
		case 'b':
			ballDetector.rememberBackground();
			break;

		}
	}
	
	public void dispose() {
		future.cancel(false);
	}
}
