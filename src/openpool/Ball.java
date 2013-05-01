package openpool;

import processing.core.PApplet;

public class Ball {
    private final float realr = 5;
    public int id;
    public float x;
    public float y;
    public float dx;
    public float dy;
    public float width;
    public float height;
    public Ball prev;
    private boolean hasSuccessor;
    private int life;

    /**
     * Constructor.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Ball(int id, float x, float y, float width, float height, int life) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = 0;
        this.dy = 0;
        this.life = life;
        hasSuccessor = false;
    }

    public double distance(Ball b) {
        // A (x' - dx, y' - dy)
        // ball position (2 frames behind)
        // B (x', y')
        // ball position (1 frame behind)
        // C (x, y)
        // current ball position
        // D (x' + dx, y' + dy)
        // predicted ball position

        // If (can't get A, thus can't calculate D
        // || or dot(d-b, c-b) < 0 i.e. dot(b-a, c-b) < 0),
        float cbx = x - b.x, cby = y - b.y;
        if (b.dx == 0 && b.dy == 0 || dot(b.dx, b.dy, cbx, cby) < 0) {
            // Return the distance between B and C
            return vectorLength(cbx, cby);
        }

        // If (dot(c-d, d-b) > 0 i.e. dot(c-d, b-a) > 0)
        float cdx = x - (b.x + b.dx), cdy = y - (b.y + b.dy);
        if (dot(cdx, cdy, b.dx, b.dy) > 0) {
            // Return the distance between C and D
            return vectorLength(cdx, cdy);
        }

        // Return the distance between C and BD
        // i.e. abs(cross(b-d, c-d)) / abs(b-d)
        return crossLength(b.dx, b.dy, cdx, cdy) / vectorLength(b.dx, b.dy);
    }

    private static double dot(float x1, float y1, float x2, float y2) {
        return x1 * x2 + y1 * y2;
    }

    private static double crossLength(float x1, float y1, float x2, float y2) {
        return Math.abs(x1 * y2 - y1 * x2);
    }

    private static double vectorLength(float x, float y) {
        return Math.sqrt(x * x + y * y);
    }

    public void draw(PApplet pa) {
        pa.stroke(255);
        pa.fill(255);
        pa.ellipse(x, y, 2 * realr, 2 * realr);
        pa.line(x, y, x + 12, y - 15);
        pa.text(String.format("no.%d [%.2f, %.2f]", id, x, y), x + 15, y - 15);
    }

    public void setPrev(Ball prev) {
        this.prev = prev;
        if (prev == null) {
            this.dx = 0;
            this.dy = 0;
        } else {
            this.id = prev.id;
            this.dx = x - prev.x;
            this.dy = y - prev.y;
            prev.hasSuccessor = true;
            prev.prev = null; // to avoid infinite reference that causes
                                // ouf-of-memory.
        }
    }

    public boolean hasSuccessor() {
        return hasSuccessor;
    }

    public boolean isNew() {
        return !hasSuccessor;
    }

    public boolean reviveAsGhost() {
        if ((--life) < 0) {
            return false;
        }
        x = x + dx;
        y = y + dy;
        return true;
    }
}
