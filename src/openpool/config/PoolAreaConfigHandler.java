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
		Point tl = op.getTopLeftCorner();
		Point br = op.getBottomRightCorner();
		int mx = op.pa.mouseX;
		int my = op.pa.mouseY;

		if (op.pa.mousePressed && selected >= 0) {
			op.PoolArea[selected].x = mx;
			op.PoolArea[selected].y = my;
		} else {
			selected = -1;
			int mSq = minimumDistanceSq;
			for (int i = 0; i < 2; i++) {
				int distanceSq = getDistanceSq(
						mx, my, op.PoolArea[i].x, op.PoolArea[i].y);
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
		
		//draw poolarea
		op.pa.stroke(255, 0, 255);
		op.pa.line(op.PoolArea[0].x, op.PoolArea[0].y, op.PoolArea[1].x, op.PoolArea[0].y);
		op.pa.line(op.PoolArea[1].x, op.PoolArea[0].y, op.PoolArea[1].x, op.PoolArea[1].y);
		op.pa.line(op.PoolArea[1].x, op.PoolArea[1].y, op.PoolArea[0].x, op.PoolArea[1].y);
		op.pa.line(op.PoolArea[0].x, op.PoolArea[1].y, op.PoolArea[0].x, op.PoolArea[0].y);
		// draw an arrow
		op.pa.line(op.PoolArea[0].x, op.PoolArea[0].y, op.PoolArea[0].x + 8, op.PoolArea[0].y + 4);
		op.pa.line(op.PoolArea[0].x, op.PoolArea[0].y, op.PoolArea[0].x + 4, op.PoolArea[0].y + 8);
		op.pa.line(op.PoolArea[0].x, op.PoolArea[0].y, op.PoolArea[0].x + 10, op.PoolArea[0].y + 10);

		// draw xy
		op.pa.text("X:", op.PoolArea[0].x + 20, op.PoolArea[0].y + 20);
		op.pa.text(op.PoolArea[0].x, op.PoolArea[0].x + 30, op.PoolArea[0].y + 20);
		op.pa.text("Y:", op.PoolArea[0].x + 20, op.PoolArea[0].y + 34);
		op.pa.text(op.PoolArea[0].y, op.PoolArea[0].x + 30, op.PoolArea[0].y + 34);

		// draw an arrow
		op.pa.line(op.PoolArea[1].x, op.PoolArea[1].y, op.PoolArea[1].x - 8, op.PoolArea[1].y - 4);
		op.pa.line(op.PoolArea[1].x, op.PoolArea[1].y, op.PoolArea[1].x - 4, op.PoolArea[1].y - 8);
		op.pa.line(op.PoolArea[1].x, op.PoolArea[1].y, op.PoolArea[1].x - 10, op.PoolArea[1].y - 10);

		// draw xy
		op.pa.text("X:", op.PoolArea[1].x - 50, op.PoolArea[1].y - 24);
		op.pa.text(op.PoolArea[1].x, op.PoolArea[1].x - 40, op.PoolArea[1].y - 24);
		op.pa.text("Y:", op.PoolArea[1].x - 50, op.PoolArea[1].y - 10);
		op.pa.text(op.PoolArea[1].y, op.PoolArea[1].x - 40, op.PoolArea[1].y - 10);
			
		op.pa.noStroke();
	}
	
	private int getDistanceSq(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

}
