package openpool.config;

import java.awt.Point;
import openpool.OpenPool;

public class PoolAreaConfigHandler extends ConfigHandlerAbstractImpl {
	private final int minimumDistanceSq = 20 * 20;
	private OpenPool op;
	
	/**
	 * True when pool area corner is grabbed with mouse pointer.
	 */
	int selected = -1;
	
	public PoolAreaConfigHandler(OpenPool op) {
		this.op = op;
	}
	
	@Override
	public String getTitle() {
		return "Specify the Pool area by dragging the corners.";
	}
	@Override
	public void draw() {
		int mx = op.pa.mouseX;
		int my = op.pa.mouseY;

		if (op.pa.mousePressed && selected >= 0) {
			op.getPoolCorner(selected).x = mx;
			op.getPoolCorner(selected).y = my;
		} else {
			selected = -1;
			int mSq = minimumDistanceSq;
			for (int i = 0; i < 2; i++) {
				int distanceSq = getDistanceSq(
						mx, my, op.getPoolCorner(i).x, op.getPoolCorner(i).y);
				if (distanceSq < mSq) {
					mSq = distanceSq;
					selected = i;
				}
			}
		}

		op.pa.fill(255, 0, 255);		
		if (selected >= 0) {
			op.pa.ellipse(op.pa.mouseX, op.pa.mouseY, 20, 20);
		}
		op.pa.noFill();

		// draw the image bounding box
		op.pa.stroke(255, 255, 0);
		drawRectangle(op.getCombinedImageTopLeft(), op.getCombinedImageBottomRight());

		//draw the pool area
		op.pa.stroke(255, 0, 255);
		drawRectangle(op.getPoolTopLeft(), op.getPoolBottomRight());

		op.pa.noStroke();
	}
	
	private void drawRectangle(Point tl, Point br) {

		// draw the rectangle
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
	}
	
	private int getDistanceSq(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

}
