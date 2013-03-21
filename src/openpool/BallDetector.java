package openpool;

import SimpleOpenNI.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import processing.core.PApplet;
import processing.core.PImage;

public class BallDetector implements Runnable {
	private BallSystem ballSystem;
	private SimpleOpenNI cam1;
	private SimpleOpenNI cam2;
	private int camCount = 0;

	private int depthWidth;
	private int depthHeight;
	
	private int cam1DepthWidth;
	private int cam1DepthHeight;
	
	private int cam2DepthWidth;
	private int cam2DepthHeight;

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
	private PImage diffPImage;
	private WritableRaster diffWritableRaster;

	/**
	 * Final result image.
	 */
	private BufferedImage resultImage;
	private PImage resultPImage;
	private WritableRaster resultWritableRaster;

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
	private int cam1_xoffset = 0, cam1_yoffset = 0;

	/**
	 * Offset for the second camera.
	 */
	private int cam2_xoffset = 0, cam2_yoffset = 0;

	BallDetector(BallSystem ballSystem, SimpleOpenNI cam1, SimpleOpenNI cam2, PApplet pa) {
		this.ballSystem = ballSystem;
		this.cam1 = cam1;
		this.cam2 = cam2;


		if (cam1 == null) {
			depthWidth = 640;
			depthHeight = 480;
		} else {
			camCount ++;
			cam1.update();
			cam1DepthHeight = cam1.depthHeight();
			cam1DepthWidth = cam1.depthWidth();
			
			depthWidth = cam1.depthWidth();
			depthHeight = cam1.depthHeight();
		}

		if (cam2 != null) {
			cam2.update();
			cam2DepthHeight = cam2.depthHeight();
			cam2DepthWidth = cam2.depthWidth();
			depthWidth += cam2DepthWidth;
			camCount++;
		}

		currentImage = IplImage.create(
				cvSize(depthWidth, depthHeight),
				IPL_DEPTH_8U, 1);

		temporaryImage = IplImage.create(
				cvSize(depthWidth, depthHeight),
				IPL_DEPTH_8U, 4);

		diffImage = new BufferedImage(
				currentImage.width(), currentImage.height(),
				BufferedImage.TYPE_INT_ARGB);

		resultImage = new BufferedImage(
				currentImage.width(), currentImage.height(),
				BufferedImage.TYPE_INT_ARGB);
		
		backgroundImage = IplImage.createCompatible(currentImage);

		rememberBackground();
	}

	public synchronized void run() {
		if (cam1 != null) {
			cam1.update();
		}
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
		CvSeq ptr;
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
					randomColor = null;
				}
				cvClearSeq(ptr);
			}
			cvAddS(temporaryImage, cvScalar(0, 0, 0, 255), temporaryImage, null);
			rand = null;
		}
		ballSystem.commit();

		cvClearMemStorage(mem);
		cvReleaseMemStorage(mem);

		temporaryImage.copyTo(resultImage);
		
		contours = null;
		//System.gc();
	}
	
	public int getCamCount(){
		return camCount;
	}
	public void setX1(int x1){
		this.cam1_xoffset = x1;
	}

	public int getX1(){
		return cam1_xoffset;
	}
	
	public void setX2(int x2){
		this.cam2_xoffset = x2;
	}
	
	public int getX2(){
		return cam2_xoffset;
	}
	
	public void setY1(int y1)	{
		this.cam1_yoffset = y1;
	}

	public int getY1()	{
		return cam1_yoffset;
	}
	
	public void setY2(int y2)	{
		this.cam2_yoffset = y2;
	}
	
	public int getY2()	{
		return cam2_yoffset;
	}
	
	public int getCam1Width()	{
		return cam1DepthWidth;
	}
	
	public int getCam1Height(){
		return cam1DepthHeight;
	}

	public int getCam2Width()	{
		return cam2DepthWidth;
	}
	
	public int getCam2Height(){
		return cam2DepthHeight;
	}

	public int getDepthWidth() {
		return depthWidth;
	}
	
	public int getDepthHeight() {
		return depthHeight;
	}

	public PImage getDiffImage() {
		if (diffPImage == null) {
			diffPImage = new PImage(resultImage);
			DataBufferInt dbi = new DataBufferInt(diffPImage.pixels, diffPImage.pixels.length);
			diffWritableRaster = Raster.createWritableRaster(
					resultImage.getSampleModel(), dbi, new Point(0, 0));
		}
		// diffPImage.loadPixels();
		diffImage.copyData(diffWritableRaster);
		diffPImage.updatePixels();
		return diffPImage;
	}

	public PImage getImage() {
		if (resultPImage == null) {
			resultPImage = new PImage(resultImage);
			DataBufferInt dbi = new DataBufferInt(resultPImage.pixels, resultPImage.pixels.length);
			resultWritableRaster = Raster.createWritableRaster(
					resultImage.getSampleModel(), dbi, new Point(0, 0));
		}
		// resultPImage.loadPixels();
		resultImage.copyData(resultWritableRaster);
		resultPImage.updatePixels();
		return resultPImage;
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

		// Fill image area with black
		cvSetZero(target);

		// FIXME: WORKAROUND!!! cam2 first
		if (cam2 != null) {
			copyImage(target, cam2.depthImage(), cam2_xoffset+cam1.depthWidth(), cam2_yoffset, cam2.depthWidth(), cam2.depthHeight());
			fillDepthErrorHoles(target, cam2.depthMap(), cam2_xoffset+cam1.depthWidth(), cam2_yoffset, cam2.depthWidth(), cam2.depthHeight());
		}
		if (cam1 != null) {
			copyImage(target, cam1.depthImage(), cam1_xoffset, cam1_yoffset, cam1.depthWidth(), cam1.depthHeight());
			fillDepthErrorHoles(target, cam1.depthMap(), cam1_xoffset, cam1_yoffset, cam1.depthWidth(), cam1.depthHeight());
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
	private void copyImage(IplImage target, PImage source, int x, int y,
			int width, int height) {
		IplImage sourceImage = IplImage.createFrom((BufferedImage) source.getImage());

		Point sourceCopyArea = new Point(0, 0);
		Point targetCopyArea = new Point(0, 0);

		int copyAreaWidth = 0;
		int copyAreaHeight = 0;

		sourceCopyArea.x = Math.max(1, 1 - x);
		sourceCopyArea.y = Math.max(1, 1 - y);

		targetCopyArea.x = Math.max(1, 1 + x);
		targetCopyArea.y = Math.max(1, 1 + y);

		copyAreaWidth = Math
				.max(0,
						sourceImage.width()
								+ target.width()
								- (Math.max(x + sourceImage.width(),
										target.width()) - Math.min(0, x)));
		copyAreaHeight = Math
				.max(0,
						sourceImage.height()
								+ target.height()
								- (Math.max(y + sourceImage.height(),
										target.height()) - Math.min(0, y)));

		// TODO: add function for x<0 or y<0
		if (copyAreaWidth * copyAreaHeight != 0) {

			// camera connected -> fail
			cvSetImageROI(
					sourceImage,
					cvRect(sourceCopyArea.x, sourceCopyArea.y,
							copyAreaWidth - 1, copyAreaHeight - 1));
			cvSetImageROI(
					target,
					cvRect(targetCopyArea.x, targetCopyArea.y,
							copyAreaWidth - 1, copyAreaHeight - 1));

			/*
			 * pa.print("cvCvt soure : "); pa.print(sourceImage.width());
			 * pa.print(" x "); pa.print(sourceImage.height());
			 * 
			 * pa.print(" ROI area POINT: "); pa.print(sourceCopyArea.x);
			 * pa.print(","); pa.print(sourceCopyArea.y);
			 * pa.print(" --- width x height:  "); pa.print(CopyAreaWidth);
			 * pa.print(" x "); pa.println(CopyAreaHeight);
			 * 
			 * pa.print(" Depth: "); pa.print(sourceImage.depth());
			 * pa.print(" nChannels: "); pa.println(sourceImage.nChannels());
			 * 
			 * pa.print("cvCvt target: "); pa.print(target.width());
			 * pa.print(" x "); pa.print(target.height());
			 * 
			 * pa.print(" ROI area POINT: "); pa.print(targetCopyArea.x);
			 * pa.print(","); pa.print(targetCopyArea.y);
			 * pa.print(" --- width x height: "); pa.print(CopyAreaWidth);
			 * pa.print(" x "); pa.println(CopyAreaHeight);
			 * 
			 * pa.print(" Depth: "); pa.print(target.depth());
			 * pa.print(" nChannels: "); pa.println(target.nChannels());
			 */
			cvCvtColor(sourceImage, target, CV_RGBA2GRAY);

			// pa.println("cvCvt DONE!!!");

			cvResetImageROI(sourceImage);
			cvResetImageROI(target);
		}

		// We shouldn't use cvReleaseImage but IplImage.release() to avoid hang.
		// http://code.google.com/p/javacv/issues/detail?id=152
		sourceImage.release();
		
		sourceCopyArea = null;
		targetCopyArea = null;
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
		x = Math.max(0, x);
		y = Math.max(0, y);
		width = x + width > target.width() ? target.width() - x : width;
		height = y + height > target.height() ? target.height() - y : height;
		CvMat mat = target.asCvMat();
		for (int targetX = x; targetX < x + width; targetX++) {
			for (int targetY = y; targetY < y + height; targetY++) {
				int depth = depthMap[targetX - x + (targetY - y) * width];

				// Assume depth errors are caused by the black ball
				if (depth <= 0) {
					mat.put(targetX + targetY * width, 0);
				}
			}
		}
	}
	
	public void dispose() {
		currentImage.release();
		backgroundImage.release();
		temporaryImage.release();
	}
	public Point getCamImageCorner(int camNumber){
		
		Point ret;
		
		switch(camNumber)
		{
		case 0:
			ret = new Point(cam1_xoffset,cam1_yoffset);
			break;
		case 1:
			ret = new Point(cam2_xoffset,cam2_yoffset);
			break;
		default:
			ret = new Point(0,0);
			break;
		}
		return ret;
	}

	public void setCamImageCorner(int camNumber,Point pt){
		switch(camNumber)
		{
		case 0:
			this.cam1_xoffset = pt.x;
			this.cam1_yoffset = pt.y;
			break;
		case 1:
			this.cam2_xoffset = pt.x;
			this.cam2_yoffset = pt.y;
			break;
		default:
			break;
		}
		return;
	}
}
