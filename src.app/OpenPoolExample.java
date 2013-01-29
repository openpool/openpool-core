import java.io.File;

import openpool.Ball;
import openpool.DummyPool;
import openpool.OpenPool;
import processing.core.PApplet;

public class OpenPoolExample extends PApplet {
	private static final long serialVersionUID = 1468683270191048480L;

	public static void main(String[] args) {
		new OpenPoolExample();
	}

	OpenPool op;
	boolean real = false;

	public void setup() {
		if (real) {
			op = new OpenPool(this);
		} else {
			String userDir = System.getProperty("user.dir");
			String binPath = File.separatorChar + "bin";
			if (userDir.endsWith(binPath)) {
				userDir = userDir.substring(0,  userDir.length() - binPath.length());
			}
			op = new DummyPool(this);
			op = new OpenPool(this,
					userDir + "\\recordings\\straight1.oni");
		}
		op.setConfigMode(true);
		frameRate(30);
	}
	
	public void draw() {
		op.updateBalls();
		for (Ball ball : op.balls) {
			ball.draw(this);
		}
	}
}
