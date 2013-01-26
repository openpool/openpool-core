import java.io.File;

import openpool.OpenPool;
import processing.core.PApplet;

public class OpenPoolExample extends PApplet {
	private static final long serialVersionUID = 1468683270191048480L;

	public static void main(String[] args) {
		new OpenPoolExample();
	}

	OpenPool op;
	
	public void setup() {
		String userDir = System.getProperty("user.dir");
		String binPath = File.separatorChar + "bin";
		if (userDir.endsWith(binPath)) {
			userDir = userDir.substring(0,  userDir.length() - binPath.length());
		}
		op = new OpenPool(this,
				userDir + "\\data\\straight1.oni");
		op.setConfigMode(true);
	}
	
	public void draw() {
		
	}
}
