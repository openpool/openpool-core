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

		// OpenPool.tableCorners
		if ("table.corners".equals(key)) {
			String[] coordinates = value.split(",");
			if (coordinates.length != 4) {
				return;
			}
			try {
				for (int i = 0; i < 2; i ++) {
					Point p = op.getTableCorner(i);
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

		// OpenPool.tableCorners
		bw.write("table.corners = ");
		bw.write(String.valueOf(op.getTableTopLeft().x));
		bw.write(", ");
		bw.write(String.valueOf(op.getTableTopLeft().y));
		bw.write(", ");
		bw.write(String.valueOf(op.getTableBottomRight().x));
		bw.write(", ");
		bw.write(String.valueOf(op.getTableBottomRight().y));
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
