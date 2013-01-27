package openpool;

import SimpleOpenNI.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import processing.core.PImage;

public class BallDetector implements Runnable {
	private BallSystem ballSystem;
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

	private int blurSize = 2;

	/**
	 * Threshold for binarization.
	 */
	private double binarizationThreshold = 70;

	private int erosionSize = 3;

	private int dilationSize = 2;

	/**
	 * Offset for the first camera.
	 */
	private int x1 = 0, y1 = 0;

	/**
	 * Offset for the second camera.
	 */
	private int x2 = 0, y2 = 0;

	BallDetector(BallSystem ballSystem, SimpleOpenNI cam1, SimpleOpenNI cam2) {
		this.ballSystem = ballSystem;
		this.cam1 = cam1;
		this.cam2 = cam2;

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

	public synchronized void run() {
		cam1.update();
		if (cam2 != null) {
			cam2.update();
		}

		retrieveDepthImage(currentImage);

		// Calculate the diff image
		cvAbsDiff(currentImage, backgroundImage, currentImage);

		// Comment out the line below when calculating difference between full color (+ alpha) images.
		// cvAddS(currentImage, cvScalar(0, 0, 0, 255), currentImage, null);

		cvCvtColor(currentImage, temporaryImage, CV_GRAY2RGBA);
		temporaryImage.copyTo(diffImage);

		// Blur
		cvSmooth(currentImage, currentImage, CV_BLUR, blurSize);

		// Binarization
		cvThreshold(currentImage, currentImage, getThreshold(), 255, CV_THRESH_BINARY);
		// cvAdaptiveThreshold(currentImage, currentImage, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 11, 10);

		// Erosion & dilation
		cvErode(currentImage, currentImage, null, erosionSize);
		cvDilate(currentImage, currentImage, null, dilationSize);

		cvCvtColor(currentImage, temporaryImage, CV_GRAY2RGBA);

		// Contour detection
		CvSeq contours = new CvSeq();
		CvSeq ptr = new CvSeq();
		CvMemStorage mem = cvCreateMemStorage(0);
		int count = cvFindContours(currentImage, mem, contours, sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

		if (count > 0) {
			Random rand = new Random();
			for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
				double area = cvContourArea(ptr, CV_WHOLE_SEQ, 0);
				if (area > 4) {
					CvRect rect = cvBoundingRect(ptr, 0);
					ballSystem.addBall(rect);
					Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
					CvScalar color = CV_RGB(randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue());
					cvDrawContours(temporaryImage, ptr, color, CV_RGB(0, 0, 0), -1, CV_FILLED, 8, cvPoint(0, 0));
					cvRectangle(temporaryImage, cvPoint(rect.x(), rect.y()), cvPoint(rect.x() + rect.width(), rect.y() + rect.height()), CV_RGB(230, 230, 255), 1, 8, 0);
				}
			}
			cvAddS(temporaryImage, cvScalar(0, 0, 0, 255), temporaryImage, null);
		}
		ballSystem.commit();

		cvClearMemStorage(mem);
		cvReleaseMemStorage(mem);

		temporaryImage.copyTo(resultImage);
	}

	public int getDepthWidth() {
		return depthWidth;
	}
	
	public int getDepthHeight() {
		return depthHeight;
	}

	public BufferedImage getDiffImage() {
		return diffImage;
	}

	public BufferedImage getImage() {
		return resultImage;
	}

	public double getThreshold() {
		return binarizationThreshold;
	}

	public void setThreshold(double threshold) {
		this.binarizationThreshold = threshold;
	}

	public synchronized void rememberBackground() {
		retrieveDepthImage(backgroundImage);
	}

	private void retrieveDepthImage(IplImage target) {
		copyImage(target, cam1.depthImage(), x1, y1, cam1.depthWidth(), cam1.depthHeight());
		fillDepthErrorHoles(target, cam1.depthMap(), x1, y1, cam1.depthWidth(), cam1.depthHeight());

		if (cam2 != null) {
			copyImage(target, cam2.depthImage(), x2, y2, cam2.depthWidth(), cam2.depthHeight());
			fillDepthErrorHoles(target, cam2.depthMap(), x2, y2, cam2.depthWidth(), cam2.depthHeight());
		}
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
		IplImage sourceImage = IplImage.createFrom((BufferedImage) source.getNative());
		cvSetImageROI(target, cvRect(x, y, width, height));
		cvCvtColor(sourceImage, target, CV_RGBA2GRAY);
		cvResetImageROI(target);
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
					mat.put(targetX + targetY * width, 0);
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
