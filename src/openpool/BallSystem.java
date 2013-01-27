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

	BallSystem(OpenPool op) {
		this.op = op;
		currentId = 0;
		balls = new ArrayList<Ball>();
		prevBalls = new ArrayList<Ball>();
		return;
	}
	
	void addBall(CvRect rect) {
		float width = rect.width();
		float height = rect.height();
		float x = rect.x() + width / 2;
		float y = rect.y() + height / 2;
		Ball ball = new Ball(
				currentId,
				op.depthToScreenX(x),
				op.depthToScreenY(y),
				op.depthToScreenWidth(width),
				op.depthToScreenHeight(height),
				ghostLife);
		Ball prev = null;
		double distance = distanceThreshold;
		for (Ball b : prevBalls) {
			double d = ball.distance(b);
			if (d < distance) {
				prev = b;
				distance = d;
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
		return prevBalls.toArray(new Ball[0]);
	}
}
