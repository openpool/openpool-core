package openpool;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvRect;

public class BallSystem {
	private final int distanceThreshold = 20;
	private final int ghostLife = 5;
	private int currentId;
	private OpenPool op;
	private List<Ball> balls;
	private List<Ball> prevBalls;
	private List<Ball> dummyBalls;
	private boolean isDummy;

	BallSystem(OpenPool op) {
		this.op = op;
		currentId = 0;
		balls = new ArrayList<Ball>();
		prevBalls = new ArrayList<Ball>();
	}
	
	void addBall(CvRect rect) {
		float width = rect.width();
		float height = rect.height();
		float x = rect.x() + width / 2;
		float y = rect.y() + height / 2;
		Ball ball = new Ball(
				currentId,
				op.tableToScreenX(x),
				op.tableToScreenY(y),
				op.tableToScreenWidth(width),
				op.tableToScreenHeight(height),
				ghostLife);

		Ball prev = null;
		double distance = distanceThreshold;
		for (Ball b : prevBalls) {
			if (!b.hasSuccessor()) {
				double d = ball.distance(b);
				if (d < distance) {
					prev = b;
					distance = d;
				}
			}
		}
		ball.setPrev(prev);

		balls.add(ball);
		currentId = (int)((long)(currentId + 1) % Integer.MAX_VALUE);
	}
	
	void merge() {
		
	}

	void commit() {
		for (Ball b : prevBalls) {
			if (!b.hasSuccessor() && b.reviveAsGhost()) {
				balls.add(b);
			}
		}
		prevBalls.clear();
		List<Ball> tmp = balls;
		balls = prevBalls;
		prevBalls = tmp;
	}
	
	public Ball[] getBalls() {
		return (isDummy ? dummyBalls : prevBalls).toArray(new Ball[0]);
	}
	
	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
		if (isDummy && dummyBalls == null) {
			initDummyBalls();
		}
	}
	
	public boolean isDummy() {
		return isDummy;
	}

	private void initDummyBalls() {
		dummyBalls = new ArrayList<Ball>();
	    dummyBalls.add(new Ball(0, 200, 200, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(1, 400, 200, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(2, 600, 200, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(3, 800, 200, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(4, 200, 400, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(5, 400, 400, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(6, 600, 400, 10, 10, ghostLife));
	    dummyBalls.add(new Ball(7, 800, 400, 10, 10, ghostLife));
	}
}
