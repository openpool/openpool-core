import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PVector;

class Shoal {

	private static final int FISH_COEFF_FORCE = 2000;
	
	private OpenPoolExampleWithFluids ope;

	/**
	 * Center of this shoal.
	 */
	float x, y;
	
	/**
	 * Velocity of this shoal.
	 */
	private float vx, vy;

	ArrayList<Fish> fishes;

	public Shoal(OpenPoolExampleWithFluids ope) {
		this.ope = ope;
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;
		fishes = new ArrayList<Fish>();
	}

	public void add(Fish fish) {
		fishes.add(fish);
	}

	public void clearVelocityVectors() {
		Iterator<Fish> it = fishes.iterator();
		while (it.hasNext()) {
			it.next().clearVelocityVectors();
		}
	}

	void addShoalAvoidanceForce(PVector v) {
		// Rule 4: Do not collide with another shoal.
		for (Fish fish : fishes) {
			fish.v4.x = fish.v4.x + v.x;
			fish.v4.y = fish.v4.y + v.y;
		}
	}

	public void update() {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;

		Iterator<Fish> it = fishes.iterator();

		while (it.hasNext()) {
			Fish fish = it.next();

			x = x + fish.x;
			y = y + fish.y;

			applyShoalRules(fish);

			fish.move();

			vx = vx + fish.vx;
			vy = vy + fish.vy;

			ope.addForceToFluid(
					fish.x / ope.width, fish.y / ope.height,
					-fish.vx / FISH_COEFF_FORCE, -fish.vy / FISH_COEFF_FORCE);
		}

		x = x / fishes.size();
		y = y / fishes.size();

		vx = vx / fishes.size();
		vy = vy / fishes.size();
	}

	/**
	 * Calculate next velocity parameters.
	 */
	void applyShoalRules(Fish fish) {
		Iterator<Fish> itB = fishes.iterator();
		while (itB.hasNext()) {
			Fish fishB = itB.next();
			if (fish != fishB) {

				// Rule 1: Follow the other fishes.
				fish.v1.x = fish.v1.x + fishB.x;
				fish.v1.y = fish.v1.y + fishB.y;

				// Rule 2: Do not collide with another fish.
				if (PApplet.dist(fish.x, fish.y, fishB.x, fishB.y) < ShoalSystem.DISTANCE_THRESHOLD) {
					fish.v2.x -= (fishB.x - fish.x);
					fish.v2.y -= (fishB.y - fish.y);
				}

				// Rule 3: Go into the same direction as the other fishes.
				fish.v3.x += fishB.vx;
				fish.v3.y += fishB.vy;
			}
		}

		// Rule 1
		fish.v1.x = (fish.v1.x / (fishes.size() - 1));
		fish.v1.y = (fish.v1.y / (fishes.size() - 1));
		fish.v1.x = (fish.v1.x - fish.x) / ShoalSystem.CENTER_PULL_FACTOR;
		fish.v1.y = (fish.v1.y - fish.y) / ShoalSystem.CENTER_PULL_FACTOR;

		// Rule 3
		fish.v3.x /= (fishes.size() - 1);
		fish.v3.y /= (fishes.size() - 1);
		fish.v3.x = (fish.v3.x - fish.vx) / 2;
		fish.v3.y = (fish.v3.y - fish.vy) / 2;
	}

	void draw() {
		for (Fish fish : fishes) {
			fish.draw();
		}

		if (ope.isDebugMode) {
			ope.noFill();
			ope.stroke(1, 1, 1);
			ope.ellipse(x, y, ShoalSystem.CA_DISTANCE_THRESHOLD, ShoalSystem.CA_DISTANCE_THRESHOLD);
			ope.text("SHOAL", x + 50, y + 50);
			ope.text("x: ", x + 50, y + 50 + 15);
			ope.text(x, x + 50 + 15, y + 50 + 15);
			ope.text("y: ", x + 50, y + 50 + 30);
			ope.text(y, x + 50 + 15, y + 50 + 30);
		}
	}
}
