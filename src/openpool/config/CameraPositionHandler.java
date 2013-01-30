package openpool.config;

import java.awt.Point;

import openpool.BallDetector;
import openpool.OpenPool;

public class CameraPositionHandler extends ConfigHandlerAbstractImpl {
	private final int minimumDistanceSq = 20 * 20;
	private OpenPool op;
	private BallDetector ballDetector;

	/**
	 * True when camera image corner is grabbed with mouse pointer.
	 */
	int selected = -1;
	int camCount;

	public CameraPositionHandler(OpenPool op,BallDetector ballDetector) {
		this.op = op;
		this.ballDetector = ballDetector;
		this.camCount = ballDetector.getCamCount();
	}
	
	@Override
	public String getTitle() {
		return "Specify the camera image area by dragging the corners.";
	}

	@Override
	public void draw() {
		int depthwidth = ballDetector.getDepthWidth();
		int depthheight = ballDetector.getDepthHeight();
		
		Point tl = op.getTopLeftCorner();
		Point br = op.getBottomRightCorner();

		Point cam1tl = new Point();
		Point cam1br = new Point();
		Point cam2tl = new Point();
		Point cam2br = new Point();
		
		cam1tl.x = tl.x + ballDetector.getX1();
		cam1tl.y = tl.y + ballDetector.getY1();
		
		cam1br.x = cam1tl.x + ballDetector.getCam1Width();
		cam1br.y = cam1tl.y + ballDetector.getCam1Height();
		
		if(camCount >= 2)
		{
			cam2tl.x = tl.x + ballDetector.getCam1Width() + ballDetector.getX2();
			cam2tl.y = tl.y + ballDetector.getY2();
			
			cam2br.x = cam2tl.x + ballDetector.getCam2Width();
			cam2br.y = cam2tl.y + ballDetector.getCam2Height();
		}
		
		int mx = op.pa.mouseX;
		int my = op.pa.mouseY;

		if (op.pa.mousePressed && selected >= 0) {
			op.getCorner(selected).x = mx;
			op.getCorner(selected).y = my;
		} else {
			selected = -1;
			int mSq = minimumDistanceSq;
			for (int i = 0; i < 2; i++) {
				int distanceSq = getDistanceSq(
						mx, my, op.getCorner(i).x, op.getCorner(i).y);
				if (distanceSq < mSq) {
					mSq = distanceSq;
					selected = i;
				}
			}
			for (int i = 0; i < 2; i++) {
				int distanceSq = getDistanceSq(
						mx, my, op.getCorner(i).x, op.getCorner(i).y);
				if (distanceSq < mSq) {
					mSq = distanceSq;
					selected = i;
				}
			}
		}

		op.pa.fill(255, 255, 0);
		if (selected >= 0) {
			op.pa.ellipse(op.pa.mouseX, op.pa.mouseY, 20, 20);
		}

		// draw the image bounding box
		op.pa.stroke(255, 255, 0);
		op.pa.line(tl.x, tl.y, br.x, tl.y);
		op.pa.line(br.x, tl.y, br.x, br.y);
		op.pa.line(br.x, br.y, tl.x, br.y);
		op.pa.line(tl.x, br.y, tl.x, tl.y);

		// draw an arrow
		op.pa.line(tl.x, tl.y, tl.x + 8, tl.y + 4);
		op.pa.line(tl.x, tl.y, tl.x + 4, tl.y + 8);
		op.pa.line(tl.x, tl.y, tl.x + 10, tl.y + 10);

		// draw xy
		op.pa.text("X:", tl.x + 20, tl.y + 20);
		op.pa.text(tl.x, tl.x + 30, tl.y + 20);
		op.pa.text("Y:", tl.x + 20, tl.y + 34);
		op.pa.text(tl.y, tl.x + 30, tl.y + 34);

		// draw an arrow
		op.pa.line(br.x, br.y, br.x - 8, br.y - 4);
		op.pa.line(br.x, br.y, br.x - 4, br.y - 8);
		op.pa.line(br.x, br.y, br.x - 10, br.y - 10);

		// draw xy
		op.pa.text("X:", br.x - 50, br.y - 24);
		op.pa.text(br.x, br.x - 40, br.y - 24);
		op.pa.text("Y:", br.x - 50, br.y - 10);
		op.pa.text(br.y, br.x - 40, br.y - 10);
		
		
		//draw cam1 bounding
		op.pa.line(cam1tl.x, cam1tl.y, cam1br.x, cam1tl.y);
		op.pa.line(cam1br.x, cam1tl.y, cam1br.x, cam1br.y);
		op.pa.line(cam1br.x, cam1br.y, cam1tl.x, cam1br.y);
		op.pa.line(cam1tl.x, cam1br.y, cam1tl.x, cam1tl.y);
		
		if(camCount >= 2)
		{
			//draw cam12bounding
			op.pa.line(cam2tl.x, cam2tl.y, cam2br.x, cam2tl.y);
			op.pa.line(cam2br.x, cam2tl.y, cam2br.x, cam2br.y);
			op.pa.line(cam2br.x, cam2br.y, cam2tl.x, cam2br.y);
			op.pa.line(cam2tl.x, cam2br.y, cam2tl.x, cam2tl.y);
		}
		op.pa.noStroke();
	}
	
	private int getDistanceSq(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
}
