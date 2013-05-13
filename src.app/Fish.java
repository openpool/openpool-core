import java.awt.Point;

import processing.core.PApplet;
import processing.core.PVector;

class Fish {
    private OpenPoolExampleWithFluids ope;

    static float SIZE_FISH = 4;

    /**
     * Parameter for shoal gathering.
     */
    static float COEFF_TOWARD_SHOAL_CENTER = 1.0f;

    /**
     * Parameter for collision avoidance with other fishes in the shoal.
     */
    static float COEFF_AVOID_FISHES = 0.1f;

    /**
     * Parameter for going along with other fishes in the shoal.
     */
    static float COEFF_TOWARD_SHOAL_VELOCITY = 0.5f;

    /**
     * Parameter for avoiding other shoals.
     */
    static float COEFF_AVOID_SHOALS = 1;

    /**
     * Parameter for avoiding balls.
     */
    static float COEFF_AVOID_BALLS = 100;

    /**
     * Location of this fish
     */
    float x, y;

    /**
     * Speed of this fish
     */
    float vx, vy;

    PVector v1, v2, v3, v4, v5;

    float speed;
    float r, g, b;

    int id;

    /**
     * 
     */
    Fish(OpenPoolExampleWithFluids ope, float x, float y, float vx, float vy,
            int id, float r, float g, float b, float speed) {
        this.ope = ope;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.id = id;

        this.r = r;
        this.g = g;
        this.b = b;
        this.speed = speed;

        v1 = new PVector();
        v2 = new PVector();
        v3 = new PVector();
        v4 = new PVector();
        v5 = new PVector();
    }

    /**
     * Clear vectors
     */
    public void clearVelocityVectors() {
        v1.x = 0;
        v1.y = 0;
        v2.x = 0;
        v2.y = 0;
        v3.x = 0;
        v3.y = 0;
        v4.x = 0;
        v4.y = 0;
        v5.x = 0;
        v5.y = 0;
    }

    public void addObstacleAvoidanceForce(PVector v) {
        v5.x += v.x;
        v5.y += v.y;
    }

    public void move() {
        vx += COEFF_TOWARD_SHOAL_CENTER * v1.x + COEFF_AVOID_FISHES * v2.x
                + COEFF_TOWARD_SHOAL_VELOCITY * v3.x + COEFF_AVOID_SHOALS
                * v4.x + COEFF_AVOID_BALLS * v5.x;
        vy += COEFF_TOWARD_SHOAL_CENTER * v1.y + COEFF_AVOID_FISHES * v2.y
                + COEFF_TOWARD_SHOAL_VELOCITY * v3.y + COEFF_AVOID_SHOALS
                * v4.y + COEFF_AVOID_BALLS * v5.y;

        // Check if the speed is faster than the limit.
        float vVector = PApplet.sqrt(vx * vx + vy * vy);
        if (vVector > speed) {
            vx = (vx / vVector) * speed;
            vy = (vy / vVector) * speed;
        }

        x += vx;
        y += vy;

        Point tl = ope.op.getPoolTopLeft();
        Point br = ope.op.getPoolBottomRight();

        // Hit the left edge
        if (x - SIZE_FISH <= tl.x) {
            x = SIZE_FISH + tl.x;
            vx *= -1;
        }

        // Hit the right edge
        if (x + SIZE_FISH >= br.x) {
            x = br.x - SIZE_FISH;
            vx *= -1;
        }

        // Hit the upper edge
        if (y - SIZE_FISH <= tl.y) {
            y = SIZE_FISH + tl.y;
            vy *= -1;
        }

        // Hit the bottom edge
        if (y + SIZE_FISH >= br.y) {
            y = br.y - SIZE_FISH;
            vy *= -1;
        }
    }

    void draw() {
        float dx = 0;
        float dy = 0;
        float rtemp;

        ope.noStroke();
        ope.fill(r, g, b, 100);

        for (int i = 0; i < 5; i++) {
            dx = -vx * 5 * i / 10;
            dy = -vy * 5 * i / 10;
            rtemp = SIZE_FISH * (5 - i) / 10;
            ope.ellipse(x - dx, y - dy, rtemp * 2, rtemp * 2);
        }
        for (int i = 0; i < 10; i++) {
            ope.noStroke();
            ope.fill(r, g, b, 100);
            dx = -vx * 5 * i / 10;
            dy = -vy * 5 * i / 10;
            rtemp = SIZE_FISH * (10 - i) / 10;
            ope.ellipse(x + dx, y + dy, rtemp * 2, rtemp * 2);
        }
    }
}
