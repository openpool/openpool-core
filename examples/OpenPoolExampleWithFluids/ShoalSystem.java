import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PVector;

class ShoalSystem {

	/**
	 * Distance threshold for collision avoidance.
	 * (When two shoals come closer than this threshold, force for collision avoidance is applied.)
	 */
	static float
			CA_DISTANCE_THRESHOLD = 100,
			CA_DISTANCE_THRESHOLD_SQ = PApplet.sq(CA_DISTANCE_THRESHOLD);

	static int CENTER_PULL_FACTOR = 300;

	static int DISTANCE_THRESHOLD = 30;

	private OpenPoolExampleWithFluids ope;

	/**
	 * Shoal objects.
	 */
	private ArrayList<Shoal> shoals;

	/**
	 * Ellipse objects to be avoided.
	 */
	private ArrayList<Obstacle> obstacles;

	/**
	 * Temporary vector for force calculation.
	 */
	private PVector f1 = new PVector(), f2 = new PVector();

	/**
	 * Default constructor.
	 */
	public ShoalSystem(OpenPoolExampleWithFluids ope) {
		this.ope = ope;
		shoals = new ArrayList<Shoal>();
		obstacles = new ArrayList<Obstacle>();
	}

	/**
	 * Create a new shoal with the specified parameters.
	 */
	public Shoal addShoal(float r, float g, float b, int x, int y, int numFishes, float speed) {
		Shoal shoal = new Shoal(ope);

		float angle = (float) (Math.PI * 2 / numFishes);
		for (int i = 0; i < numFishes; i++) {
			float dx = (float) Math.cos(angle * i);
			float dy = (float) Math.sin(angle * i);

			Fish fish = new Fish(
					ope,
					ope.width / 2 + x + dx * 50,
					ope.height / 2 + y + dy * 50,
					(float) (Math.random() - 0.5) * 2 * speed * dx,
					(float) (Math.random() - 0.5) * 2 * speed * dy,
					i,
					r, g, b,
					speed);

			shoal.add(fish);
		}

		shoals.add(shoal);
		return shoal;
	}

	public void addObstacle(float x, float y, int r) {
		Obstacle obj = new Obstacle(ope, x, y, r);
		obstacles.add(obj);
	}

	public void clearEllipseObjects() {
		obstacles.clear();
	}

	public void update() {
		Iterator<Shoal> itA = shoals.iterator();
		while (itA.hasNext()) {
			Shoal shoalA = itA.next();

			shoalA.clearVelocityVectors();

			f1.x = 0;
			f1.y = 0;
			Iterator<Shoal> itB = shoals.iterator();
			while (itB.hasNext()) {
				Shoal shoalB = itB.next();
				if (shoalA != shoalB) {
					avoidCollision(shoalA.x, shoalA.y, shoalB.x, shoalB.y,
							CA_DISTANCE_THRESHOLD_SQ, f2);
					f1.x += f2.x;
					f1.y += f2.y;
				}
			}
			shoalA.addShoalAvoidanceForce(f1);

			Iterator<Fish> itFish = shoalA.fishes.iterator();
			while (itFish.hasNext()) {
				Fish fish = itFish.next();

				Iterator<Obstacle> itObstacle = obstacles.iterator();
				while (itObstacle.hasNext()) {
					Obstacle obstacle = itObstacle.next();
					avoidCollision(fish.x, fish.y, obstacle.x, obstacle.y,
							PApplet.sq(obstacle.r), f1);
					fish.addObstacleAvoidanceForce(f1);
				}
			}
			shoalA.update();
		}
		return;
	}

	private void avoidCollision(float x, float y, float x2, float y2,
			float distanceSq, PVector v) {
		v.x = 0;
		v.y = 0;

		if (PApplet.sq(x - x2) + PApplet.sq(y - y2) < distanceSq) {
			if ((x - x2) != 0) {
				v.x = (x - x2) / PApplet.abs(x - x2);
			}
			if ((y - y2) != 0) {
				v.y = (y - y2) / PApplet.abs(y - y2);
			}
		}
	}

	public void draw() {
		for (Shoal shoal : shoals) {
			shoal.draw();
		}
		for (Obstacle obstacle : obstacles) {
			obstacle.draw();
			int MAXLOOP = 32;
			for (int i = 0; i < MAXLOOP; i++) {
				ope.addForceToFluid((obstacle.x + 30 * Math.cos(i * 2 * Math.PI / MAXLOOP)) * ope.invWidth,
						(obstacle.y + 30 * Math.sin(i * 2 * Math.PI / MAXLOOP)) * ope.invHeight,
						0.001 * Math.cos(i * 2* Math.PI / MAXLOOP),
						0.001 * Math.sin(i * 2 * Math.PI / MAXLOOP));
				ope.addForceToFluid((obstacle.x + 15 * Math.cos(i * 2 * Math.PI / MAXLOOP)) * ope.invWidth,
						(obstacle.y + 15 * Math.sin(i * 2 * Math.PI / MAXLOOP)) * ope.invHeight,
						0.002 * Math.cos(i * 2 * Math.PI / MAXLOOP),
						0.002 * Math.sin(i * 2 * Math.PI / MAXLOOP));
			}
		}
	}
}
