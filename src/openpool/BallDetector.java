package openpool;

import SimpleOpenNI.*;
import java.awt.image.BufferedImage;
import com.googlecode.javacv.Blobs;
import com.googlecode.javacv.JavaCV;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import processing.core.PImage;

class BallDetector implements Runnable {
	JavaCV opencv;
	SimpleOpenNI cam1;
	SimpleOpenNI cam2;

	IplImage currentImage;
	IplImage backgroundImage;
	IplImage temporaryImage;
	BufferedImage currentBufferedImage;

	Blobs blobs = null;
	double threshold = 0.1;
	int depthWidth;
	int depthHeight;

	BallDetector(SimpleOpenNI cam1, SimpleOpenNI cam2) {
		opencv = new JavaCV();
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
				IPL_DEPTH_8U, 4);
		currentBufferedImage = new BufferedImage(
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

		// Blur
		cvSmooth(currentImage, currentImage, CV_BLUR_NO_SCALE, 3);

		// Binarization
		cvThreshold(currentImage, currentImage, threshold, threshold, CV_THRESH_BINARY);

		// Dilation
		cvDilate(currentImage, currentImage, null, 5);

		// Detect blobs
		// http://code.google.com/p/javacv/source/browse/samples/BlobDemo.java
		blobs.BlobAnalysis(currentImage,
				-1, -1, // ROI start col, row
				-1, -1, // ROI cols, rows
				1, // border (0 = black; 1 = white)
				6);

		synchronized (this) {
			currentImage.copyTo(currentBufferedImage);
		}
	}

	void rememberBackground() {
		retrieveDepthImage(backgroundImage);
	}

	void retrieveDepthImage(IplImage target) {
		if (cam2 == null) {
			copyImage(target, cam1.depthImage(), 0, 0, cam1.depthWidth(), cam1.depthHeight());
			fixDepthImage(target, cam1.depthMap(), 0, 0, cam1.depthWidth(), cam1.depthHeight());
		} else {
			combineDepthImage(target, cam1.depthImage(), cam2.depthImage(),
					0, 0, cam1.depthWidth(), cam1.depthHeight(),
					0, 0, cam2.depthWidth(), cam2.depthHeight());
			fixDepthImage(target, cam1.depthMap(), cam2.depthMap(),
					0, 0, cam1.depthWidth(), cam1.depthHeight(),
					0, 0, cam2.depthWidth(), cam2.depthHeight());
		}
	}

	void combineDepthImage(IplImage combined, PImage img1, PImage img2,
			int img1x, int img1y, int img1w, int img1h, int img2x, int img2y,
			int img2w, int img2h) {
		copyImage(combined, img1, img1x, img1y, img1w, img1h);
		copyImage(combined, img2, img2x, img2y, img2w, img2h);
	}

	void copyImage(IplImage target, PImage source,
			int x, int y, int width, int height) {
		IplImage tmp = IplImage.createFrom((BufferedImage) source.getImage());
		cvSetImageCOI(tmp, 0);
		cvSetImageROI(target, cvRect(x, y, width, height));
		cvCopy(tmp, target);
	}

	void fixDepthImage(IplImage combined, int[] img1, int[] img2,
			int img1x, int img1y, int img1w, int img1h,
			int img2x, int img2y, int img2w, int img2h) {
		fixDepthImage(combined, img1, img1x, img1y, img1w, img1h);
		fixDepthImage(combined, img2, img2x, img2y, img2w, img2h);
	}

	void fixDepthImage(IplImage target, int[] depthMap,
			int x, int y, int width, int height) {
		CvMat mat = target.asCvMat();
		for (int targetX = x >= 0 ? x : 0; targetX < target.width(); targetX++) {
			for (int targetY = y >= 0 ? y : 0; targetY < target.height(); targetY++) {
				int depth = depthMap[targetX - x + (targetY - y) * width];

				// Assume depth errors are caused by the black ball
				if (depth <= 0) {
					// bb1.put(targetX + targetY * width, (byte) 0xff);
					mat.put(targetX + targetY * width, 0xffffff);
				}
			}
		}
	}
}
