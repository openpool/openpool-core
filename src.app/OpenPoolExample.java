/**
 This is an example sketch for OpenPool library.
 This sketch requires following libraries to run properly.

 <ul>
  <li>OpenPool: http://www.open-pool.com/.
  <li>SimpleOpenNI: http://code.google.com/p/simple-openni/wiki/Installation
 </ul>

 Copyright (c) takashyx 2012-2013 ( <a href="http://takashyx.com">takashyx.com</a> )
 Copyright (c) arc@dmz 2012-2013 ( <a href="http://junkato.jp">junkato.jp</a> )

 All rights reserved.
 This work is licensed under GPL v2.
*/
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

		size(840, 440);
		frameRate(30);

		if (real) {
			op = new OpenPool(this);
		} else {
			String userDir = System.getProperty("user.dir");
			String binPath = File.separatorChar + "bin";
			if (userDir.endsWith(binPath)) {
				userDir = userDir.substring(0,  userDir.length() - binPath.length());
			}
			// op = new DummyPool(this);
			op = new OpenPool(this, userDir + File.separator + "recordings" + File.separator + "straight1.oni");
		}

		op.loadConfig("config.txt");
		op.setConfigMode(true);
	}
	
	public void draw() {
		op.updateBalls();
		for (Ball ball : op.balls) {
			ball.draw(this);
		}
	}
}
