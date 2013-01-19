import openpool.OpenPool;
import processing.core.PApplet;

public class OpenPoolExample extends PApplet {
	private static final long serialVersionUID = 1468683270191048480L;

	public static void main(String[] args) {
		new OpenPoolExample();
	}

	OpenPool op;
	
	public void setup() {
		op = new OpenPool(this);
	}
	
	public void draw() {
		op.draw();
	}
	
	public void mouseMoved() {
		op.mouseMoved();
	}
	
	public void keyPressed() {
		op.keyPressed();
	}
}
