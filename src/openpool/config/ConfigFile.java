package openpool.config;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import openpool.BallDetector;
import openpool.OpenPool;

public class ConfigFile {
	private OpenPool op;
	private BallDetector ballDetector;
	private String filePath;
	FileWriter fw;
	public ConfigFile(OpenPool op, BallDetector bd, String filePath) {
		this.op = op;
		this.ballDetector = bd;
		this.filePath = filePath;
	}

	public void load() {
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(filePath);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			System.err.print("Config file not found: ");
			System.err.println(filePath);
			return;
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] words = line.split("=", 2);
				if (words.length < 2) continue;
				deserialize(words[0].trim(), words[1].trim());
			}
			br.close();
		} catch (IOException e) {
			System.err.print("Error while reading the config file: ");
			System.err.println(filePath);
		}
	}
	private void deserialize(String key, String value) {

		// BallDetector.threshold
		if ("detector.threshold".equals(key)) {
			try {
				Double threshold = Double.parseDouble(value);
				ballDetector.setThreshold(threshold);
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}
		//cam1_xoffset
		if("detector.cam1_xoffset".equals(key)) {
			try {
				Point pt = new Point(0,0);
				int cam1_xoffset = Integer.parseInt(value);
				ballDetector.setX1(cam1_xoffset);
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}
		//cam1_yoffset
		if("detector.cam1_yoffset".equals(key)) {
			try {
				Point pt = new Point(0,0);
				int cam1_yoffset = Integer.parseInt(value);
				ballDetector.setY1(cam1_yoffset);
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}
		//cam2_xoffset
		if("detector.cam2_xoffset".equals(key)) {
			try {
				Point pt = new Point(0,0);
				int cam2_xoffset = Integer.parseInt(value);
				ballDetector.setX2(cam2_xoffset);
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}
		//cam2_yoffset
		if("detector.cam2_yoffset".equals(key)) {
			try {
				Point pt = new Point(0,0);
				int cam2_yoffset = Integer.parseInt(value);
				ballDetector.setY2(cam2_yoffset);
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}
		// OpenPool.tableCorners
		if ("combinedImage.corners".equals(key)) {
			String[] coordinates = value.split(",");
			if (coordinates.length != 4) {
				return;
			}
			try {
				for (int i = 0; i < 2; i ++) {
					Point p = op.getCombinedImageCorner(i);
					int x = Integer.parseInt(coordinates[i * 2 + 0].trim());
					int y = Integer.parseInt(coordinates[i * 2 + 1].trim());
					p.setLocation(x, y);
				}
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}

		// OpenPool.poolCorners
		if ("pool.corners".equals(key)) {
			String[] coordinates = value.split(",");
			if (coordinates.length != 4) {
				return;
			}
			try {
				for (int i = 0; i < 2; i ++) {
					Point p = op.getPoolCorner(i);
					int x = Integer.parseInt(coordinates[i * 2 + 0].trim());
					int y = Integer.parseInt(coordinates[i * 2 + 1].trim());
					p.setLocation(x, y);
				}
			} catch (NumberFormatException nfe) {
				// Do nothing.
			}
			return;
		}

		// TODO Read other settings.
	}
	
	public void save() {
		try {
			FileWriter fw = new FileWriter(filePath);
			BufferedWriter bw = new BufferedWriter(fw);
			serialize(bw);
			bw.close();
		} catch (IOException e) {
			System.err.print("Error while writing config to the file: ");
			System.err.println(filePath);
		}
	}

	private void serialize(BufferedWriter bw) throws IOException {

		// BallDetector.threshold
		bw.write("detector.threshold = ");
		bw.write(String.valueOf(ballDetector.getThreshold()));
		bw.newLine();
		
		//cam1_xoffset
		bw.write("cam1_xoffset = ");
		bw.write(String.valueOf(ballDetector.getX1()));
		bw.newLine();
		
		//cam1_yoffset
		bw.write("cam1_yoffset = ");
		bw.write(String.valueOf(ballDetector.getY1()));
		bw.newLine();
		
		//cam2_xoffset
		bw.write("cam2_xoffset = ");
		bw.write(String.valueOf(ballDetector.getX2()));
		bw.newLine();
		
		//cam2_yoffset
		bw.write("cam2_yoffset = ");
		bw.write(String.valueOf(ballDetector.getY2()));
		bw.newLine();
		
		// OpenPool.combinedImageCorners
		bw.write("combinedImage.corners = ");
		bw.write(String.valueOf(op.getCombinedImageTopLeft().x));
		bw.write(", ");
		bw.write(String.valueOf(op.getCombinedImageTopLeft().y));
		bw.write(", ");
		bw.write(String.valueOf(op.getCombinedImageBottomRight().x));
		bw.write(", ");
		bw.write(String.valueOf(op.getCombinedImageBottomRight().y));
		bw.newLine();

		// OpenPool.poolCorners
		bw.write("pool.corners = ");
		bw.write(String.valueOf(op.getPoolTopLeft().x));
		bw.write(", ");
		bw.write(String.valueOf(op.getPoolTopLeft().y));
		bw.write(", ");
		bw.write(String.valueOf(op.getPoolBottomRight().x));
		bw.write(", ");
		bw.write(String.valueOf(op.getPoolBottomRight().y));
		bw.newLine();

		// TODO Output other settings.
	}
}
