package openpool.config;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import openpool.BallDetector;
import openpool.OpenPool;
import processing.core.PImage;

public class BallDetectorConfigHandler extends ConfigHandlerAbstractImpl {
	private OpenPool op;
	private BallDetector ballDetector;

	private boolean mousePressed = false;
	private boolean mouseHovering = false;
	private int rawMode = 0;

	public BallDetectorConfigHandler(OpenPool op, BallDetector ballDetector) {
		this.op = op;
		this.ballDetector = ballDetector;
	}
	
	public String getTitle() {
		return "Set binarization threshold by dragging the slider. / Define background by pressing ENTER key. / Show raw diff image by pressing SPACE key.";
	}

	@Override
	public void draw() {
		Point tl = op.getTopLeftCorner();
		Point br = op.getBottomRightCorner();

		op.pa.stroke(255, 255, 0);
		if (rawMode > 0) {
			synchronized (ballDetector) {
				BufferedImage diffImage = ballDetector.getDiffImage();
				int height = br.y - tl.y;
				if (rawMode == 2) {
					diffImage = diffImage.getSubimage(0, 0, diffImage.getWidth(), diffImage.getHeight() / 2);
					height /= 2;
				}
				op.pa.image(new PImage(diffImage), tl.x, tl.y, br.x - tl.x, height);
				if (rawMode == 2) {
					op.pa.line(tl.x, tl.y + height, br.x, tl.y + height);
				}
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
		op.pa.rect(12, op.getHeight() - 28, (float) ballDetector.getThreshold() * (op.getWidth() - 24) / 255, 11);

		if (mouseHovering || mousePressed) {
			op.pa.fill(255, 255, 0);
			op.pa.ellipse(op.pa.mouseX, op.pa.mouseY, 20, 20);
		}
	}

	@Override
	public void mouseEvent(MouseEvent e) {
		mouseHovering = e.getX() >= 10 && e.getX() <= op.getWidth() - 10
				&& e.getY() >= op.getHeight() - 30
				&& e.getY() <= op.getHeight() - 15;
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
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			ballDetector.rememberBackground();
			op.setMessage("Recorded the base image for background substraction.");
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			rawMode = (rawMode + 1) % 3;
			switch (rawMode) {
			case 0:
				op.setMessage("Finished showing raw diff image.");
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
