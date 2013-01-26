package openpool;

import SimpleOpenNI.*;

import java.awt.image.BufferedImage;
import com.googlecode.javacv.Blobs;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import processing.core.PImage;

public class BallDetector implements Runnable {
	private SimpleOpenNI cam1;
	private SimpleOpenNI cam2;

	private int depthWidth;
	private int depthHeight;

	/**
	 * 1 channel grayscale image.
	 */
	private IplImage currentImage;

	/**
	 * 1 channel grayscale image.
	 */
	private IplImage backgroundImage;

	/**
	 * 4 channel full color (+ alpha) image.
	 */
	private IplImage temporaryImage;

	/**
	 * Background-substracted image.
	 */
	private BufferedImage diffImage;

	/**
	 * Final result image.
	 */
	private BufferedImage resultImage;

	/**
	 * Blob detector. (Current detection results.)
	 */
	private Blobs blobs = null;

	/**
	 * Threshold for binarization.
	 */
	private double threshold = 10;

	/**
	 * Offset for the first camera.
	 */
	private int x1 = 0, y1 = 0;

	/**
	 * Offset for the second camera.
	 */
	private int x2 = 0, y2 = 0;

	BallDetector(SimpleOpenNI cam1, SimpleOpenNI cam2) {
		this.cam1 = cam1;
		this.cam2 = cam2;

		blobs = new Blobs();

		cam1.update();
		depthWidth = cam1.depthWidth();
		depthHeight = cam1.depthHeight();

		if (cam2 != null) {
			cam2.update();
			depthWidth += cam2.depthWidth();
		}

		currentImage = cvCreateImage(
				cvSize(depthWidth, depthHeight),
				IPL_DEPTH_8U, 1);

		temporaryImage = cvCreateImage(
				cvSize(depthWidth, depthHeight),
				IPL_DEPTH_8U, 4);

		diffImage = new BufferedImage(
				currentImage.width(), currentImage.height(),
				BufferedImage.TYPE_INT_ARGB);

		resultImage = new BufferedImage(
				currentImage.width(), currentImage.height(),
				BufferedImage.TYPE_INT_ARGB);
		
		backgroundImage = cvCloneImage(currentImage);

		rememberBackground();
	}

	public void run() {
		cam1.update();
		if (cam2 != null) {
			cam2.update();
		}

		retrieveDepthImage(currentImage);

		// Calculate the diff image
		cvAbsDiff(currentImage, backgroundImage, currentImage);

		// Comment out the line below when calculating difference between full color (+ alpha) images.
		// cvAddS(currentImage, cvScalar(0, 0, 0, 255), currentImage, null);

		synchronized (this) {
			cvCvtColor(currentImage, temporaryImage, CV_GRAY2RGBA);
			temporaryImage.copyTo(diffImage);
		}

		// Binarization
		cvThreshold(currentImage, currentImage, getThreshold(), 255, CV_THRESH_BINARY);

		// Dilation
		// cvDilate(currentImage, currentImage, null, 5);

		// Detect blobs
		// http://code.google.com/p/javacv/source/browse/samples/BlobDemo.java
		blobs.BlobAnalysis(currentImage,
				-1, -1, // ROI start col, row
				-1, -1, // ROI cols, rows
				1, // border (0 = black; 1 = white)
				6);

		synchronized (this) {
			cvCvtColor(currentImage, temporaryImage, CV_GRAY2RGBA);
			temporaryImage.copyTo(resultImage);
		}
	}

	public BufferedImage getDiffImage() {
		return diffImage;
	}

	public BufferedImage getImage() {
		return resultImage;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public void rememberBackground() {
		retrieveDepthImage(backgroundImage);
	}

	private void retrieveDepthImage(IplImage target) {
		copyImage(target, cam1.depthImage(), x1, y1, cam1.depthWidth(), cam1.depthHeight());
		fillDepthErrorHoles(target, cam1.depthMap(), x1, y1, cam1.depthWidth(), cam1.depthHeight());

		if (cam2 != null) {
			copyImage(target, cam2.depthImage(), x2, y2, cam2.depthWidth(), cam2.depthHeight());
			fillDepthErrorHoles(target, cam2.depthMap(), x2, y2, cam2.depthWidth(), cam2.depthHeight());
		}

		// Blur
		// FIXME This code breaks the target image. Need to investigate the correct use of this method.
		// cvSmooth(target, target, CV_BLUR_NO_SCALE, 3);
	}

	/**
	 * Copy image region from PImage to IplImage.
	 * @param target Target IplImage object
	 * @param source Source PImage object
	 * @param x Target x-coordinate
	 * @param y Target y-coordinate
	 * @param width Target width
	 * @param height Target height
	 */
	private void copyImage(IplImage target, PImage source,
			int x, int y, int width, int height) {
		IplImage sourceImage = IplImage.createFrom((BufferedImage) source.getImage());
		cvSetImageROI(target, cvRect(x, y, width, height));
		cvCvtColor(sourceImage, target, CV_RGBA2GRAY);
		// cvReleaseImage(sourceImage);
	}

	/**
	 * Fill black holes caused by IR absorption.
	 * @param target Target IplImage (1 channel grayscale)
	 * @param depthMap Source depth map
	 * @param x Target x-coordinate
	 * @param y Target y-coordinate
	 * @param width Target width
	 * @param height Target height
	 */
	private void fillDepthErrorHoles(IplImage target, int[] depthMap,
			int x, int y, int width, int height) {
		CvMat mat = target.asCvMat();
		for (int targetX = x >= 0 ? x : 0; targetX < target.width(); targetX++) {
			for (int targetY = y >= 0 ? y : 0; targetY < target.height(); targetY++) {
				int depth = depthMap[targetX - x + (targetY - y) * width];

				// Assume depth errors are caused by the black ball
				if (depth <= 0) {
					mat.put(targetX + targetY * width, 0xff);
				}
			}
		}
	}
	
	public void dispose() {
		/*
		cvReleaseImage(currentImage);
		cvReleaseImage(backgroundImage);
		cvReleaseImage(temporaryImage);
		*/
	}
}
