package openpool.config;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import openpool.BallDetector;
import openpool.OpenPool;
import processing.core.PGraphicsJava2D;
import processing.core.PImage;

public class BallDetectorConfigHandler extends ConfigHandlerAbstractImpl {
	private OpenPool op;
	private BallDetector ballDetector;

	private boolean mousePressed = false;
	private boolean mouseHovering = false;

	/**
	 * Visualization mode:
	 * <dl>
	 * <dt>0</dt><dd>None</dd>
	 * <dt>1</dt><dd>Show the entire diff image</dd>
	 * <dt>2</dt><dd>Show the upper half of the diff image</dd>
	 * </dl>
	 */
	private int rawMode = 0;

	/**
	 * PImage for masking the lower part of the image.
	 */
	private PImage mask;

	public BallDetectorConfigHandler(OpenPool op, BallDetector ballDetector) {
		this.op = op;
		this.ballDetector = ballDetector;
	}
	
	public String getTitle() {
		return "Set binarization threshold by dragging the slider. / Define background by pressing SPACE key. / Show raw diff image by pressing TAB key.";
	}

	@Override
	public void draw() {
		Point tl = op.getTableTopLeft();
		Point br = op.getTableBottomRight();

		op.pa.background(0);
		op.pa.stroke(255, 255, 0);
		
		if (rawMode > 0) {
			synchronized (ballDetector) {
				PImage diffImage = ballDetector.getDiffImage();
				
				// Mask the lower part of the image.
				if (rawMode == 2) {
					if (mask == null) {
						mask = new PImage(diffImage.width, diffImage.height, PImage.GRAY);
						// mask.loadPixels();
						for (int i = 0; i < mask.width * mask.height / 2; i ++) {
							mask.pixels[i] = 0xff;
						}
						mask.updatePixels();
					}
					diffImage.mask(mask);
					int y = (br.y + tl.y) / 2;
					op.pa.line(tl.x, y, br.x, y);
				}
				op.pa.image(diffImage, tl.x, tl.y, br.x - tl.x, br.y - tl.y);
			}
		}

		// Draw the image bounding box
		op.pa.line(tl.x, tl.y, br.x, tl.y);
		op.pa.line(br.x, tl.y, br.x, br.y);
		op.pa.line(br.x, br.y, tl.x, br.y);
		op.pa.line(tl.x, br.y, tl.x, tl.y);

		op.pa.fill(255, 100, 100);
		op.pa.text("Binarization threshold: " + ballDetector.getThreshold(), 10, op.getHeight() - 35);
		op.pa.fill(0);
		op.pa.stroke(255);
		op.pa.rect(10, op.getHeight() - 30, op.getWidth() - 20, 14);
		op.pa.fill(255);
		op.pa.noStroke();
		int y = op.pa.g instanceof PGraphicsJava2D ? 28 : 29; // PGraphics bug work around.
		op.pa.rect(12, op.getHeight() - y, (float) ballDetector.getThreshold() * (op.getWidth() - 24) / 255, 11);

		if (mouseHovering || mousePressed) {
			op.pa.fill(255, 255, 0);
			op.pa.ellipse(op.pa.mouseX, op.pa.mouseY, 20, 20);
		}
	}

	@Override
	public void mouseEvent(MouseEvent e) {
		mouseHovering = e.getX() >= 10 && e.getX() <= op.getWidth() - 10
				&& e.getY() >= op.getHeight() - 40
				&& e.getY() <= op.getHeight() - 5;
		switch (e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			if (!mouseHovering) break;
			mousePressed = true;
		case MouseEvent.MOUSE_DRAGGED:
			if (mousePressed) {
				double threshold = 255.0 * (e.getX() - 12) / (op.getWidth() - 24);
				if (threshold < 0 || threshold > 255) break;
				ballDetector.setThreshold(threshold);
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			mousePressed = false;
		}
	}

	@Override
	public void keyEvent(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_RELEASED) {
			return;
		}
		if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
			ballDetector.rememberBackground();
			op.setMessage("Recorded the base image for background substraction.");
		}
		if (e.getKeyCode() == java.awt.event.KeyEvent.VK_TAB) {
			rawMode = (rawMode + 1) % 3;
			switch (rawMode) {
			case 0:
				op.setMessage("Stopped showing raw diff image.");
				break;
			case 1:
				op.setMessage("Started to show raw diff image.");
				break;
			case 2:
				op.setMessage("Started to show the top half of raw diff image.");
				break;
			}
		}
	}
	
}
