import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;

class Shoal {
	OpenPoolExampleWithFluids ope;
	ArrayList<Fish> fishes;
	float x, y;
	float vx, vy;
	int CENTER_PULL_FACTOR;
	int DIST_THRESHOLD;

	Shoal(OpenPoolExampleWithFluids ope) {
		this.ope = ope;

		fishes = new ArrayList<Fish>();
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;

		CENTER_PULL_FACTOR = 300;
		DIST_THRESHOLD = 30;
		return;
	}

	void add(Fish _fish) {
		fishes.add(_fish);
	}

	int size() {
		return fishes.size();
	}

	// calc param1
	Fish shoalrules(Fish fish_i) {
		Iterator<Fish> iter_j = fishes.iterator();
		while (iter_j.hasNext()) {
			Fish fish_j = (Fish) iter_j.next();
			if (fish_i != fish_j) {
				// rule1
				fish_i.v1.x = fish_i.v1.x + fish_j.x;
				fish_i.v1.y = fish_i.v1.y + fish_j.y;

				// rule2
				if (PApplet.dist(fish_i.x, fish_i.y, fish_j.x, fish_j.y) < DIST_THRESHOLD) {
					fish_i.v2.x -= (fish_j.x - fish_i.x);
					fish_i.v2.y -= (fish_j.y - fish_i.y);
				}

				// rule3
				fish_i.v3.x += fish_j.vx;
				fish_i.v3.y += fish_j.vy;
			}
		}

		// rule1
		fish_i.v1.x = (fish_i.v1.x / (fishes.size() - 1));
		fish_i.v1.y = (fish_i.v1.y / (fishes.size() - 1));

		fish_i.v1.x = (fish_i.v1.x - fish_i.x) / CENTER_PULL_FACTOR;
		fish_i.v1.y = (fish_i.v1.y - fish_i.y) / CENTER_PULL_FACTOR;

		// rule2 none

		// rule3
		fish_i.v3.x /= (fishes.size() - 1);
		fish_i.v3.y /= (fishes.size() - 1);

		fish_i.v3.x = (fish_i.v3.x - fish_i.vx) / 2;
		fish_i.v3.y = (fish_i.v3.y - fish_i.vy) / 2;

		return fish_i;
	}// end shoalrules

	void clearVector() {
		Iterator<Fish> iter = fishes.iterator();

		while (iter.hasNext()) {
			Fish fish = (Fish) iter.next();
			fish.clearVector();
		}
	}

	void update() {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;

		Iterator<Fish> iter = fishes.iterator();

		while (iter.hasNext()) {
			Fish fish = (Fish) iter.next();

			x = x + fish.x;
			y = y + fish.y;

			shoalrules(fish);
			// rule4

			fish.move();

			vx = vx + fish.vx;
			vy = vy + fish.vy;

			ope.addForceToFluid(fish.x / ope.width, fish.y / ope.height,
					-fish.vx / ope.FISHFORCE, -fish.vy / ope.FISHFORCE);
		}
		x = x / fishes.size();
		y = y / fishes.size();

		vx = vx / fishes.size();
		vy = vy / fishes.size();

		return;
	}

	void addForce(float _vx, float _vy) {
		Iterator<Fish> iter = fishes.iterator();
		while (iter.hasNext()) {
			Fish fish = iter.next();
			fish.v4.x = fish.v4.x + _vx;
			fish.v4.y = fish.v4.y + _vy;
			/*
			 * print("addforce vx:"); print(vx); print(" vy:"); println(vy);
			 */
		}
	}

	void draw() {
		Iterator<Fish> iter = fishes.iterator();
		while (iter.hasNext()) {
			Fish fish = (Fish) iter.next();
			fish.draw();
		}

		if (ope.DEBUG) {
			ope.noFill();
			ope.stroke(1, 1, 1);
			ope.ellipse(x, y, ope.SHOALCOLLISION, ope.SHOALCOLLISION);
			ope.text("SHOAL", x + 50, y + 50);
			ope.text("x: ", x + 50, y + 50 + 15);
			ope.text(x, x + 50 + 15, y + 50 + 15);
			ope.text("y: ", x + 50, y + 50 + 30);
			ope.text(y, x + 50 + 15, y + 50 + 30);
		}
		return;
	}
}
