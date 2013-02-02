package openpool;

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
	
	public double movedLength(Ball b) {
		// A (x' - dx, y' - dy) : ball position (2 frames behind)
		// B (x', y') : ball position (1 frame behind)
		// C (x, y) : ball position (current)
		
		if (b.dx == 0 && b.dy == 0) {
			return movedLength(x - b.x, y - b.y);
		}
		
		// if (dot(b-a, c-a) < 0)
		float cax = x - (b.x - b.dx), cay = y - (b.y -b.dy);
		if (isBoundedMuch(b.dx, b.dy, cax, cay)) {
			// return abs(c-a)
			return movedLength(cax, cay);
		}

		// if (dot(a-b, c-b) < 0)
		float cbx = x - b.x, cby = y - b.y;
		if (!isBounded(b.dx, b.dy, cbx, cby)) {
			// return abs(c-b)
			return movedLength(cbx, cby);
		}
		
		// return abs(cross(b-a, c-a)) / abs(b-a)
		return crossLength(b.dx, b.dy, cax, cay) / movedLength(b.dx, b.dy);
	}

	private static boolean isBoundedMuch(float x1, float y1, float x2, float y2) {
		return isBounded(x1, y1, x2, y2);
	}

	private static boolean isBounded(float x1, float y1, float x2, float y2) {
		return dot(x1, y1, x2, y2) < 0;
	}

	private static double dot(float x1, float y1, float x2, float y2) {
		return x1*x2 + y1*y2;
	}
	
	private static double crossLength(float x1, float y1, float x2, float y2) {
		return Math.abs(x1*y2 - y1*x2);
	}
	
	private static double movedLength(float x, float y) {
		return Math.sqrt(x * x + y * y);
	}
	
	public void draw(OpenPool op) {
		op.pa.stroke(255);
		op.pa.fill(255);
		op.pa.ellipse(x, y, 2 * realr, 2 * realr);
		op.pa.line(x, y, x + 12, y - 15);
		op.pa.text(String.format("id: %d [%.2f, %.2f]", id, x, y), x + 15, y - 15);
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
		}
	}
	
	public boolean hasSuccessor() {
		return hasSuccessor;
	}
	
	public boolean reviveAsGhost() {
		if ((-- life) < 0) {
			return false;
		}
		x = x + dx;
		y = y + dy;
		return true;
	}
}
