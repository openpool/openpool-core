package openpool.config;

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
		switch (key) {
		
			// BallDetector.threshold
			case "detector.threshold":
				try {
					Double threshold = Double.parseDouble(value);
					ballDetector.setThreshold(threshold);
				} catch (NumberFormatException nfe) {
					// Do nothing.
				}
				break;

			// TODO Read other settings.

			default:
				break;
		}
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

		// TODO Output other settings.
	}
}
