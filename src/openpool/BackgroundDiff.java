package openpool;
import SimpleOpenNI.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.googlecode.javacv.Blobs;
import com.googlecode.javacv.JavaCV;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import processing.core.PImage;

class BackGroundDiff
{
  // OpenCV
  SimpleOpenNI kinect1;
  SimpleOpenNI kinect2;

  JavaCV opencv;
  IplImage currentImage;
  IplImage backgroundImage;
  IplImage temporaryImage;
  BufferedImage currentBufferedImage;
  
  Blobs blobs = null;
  ArrayList<Point> bgPoints;
  double threshold = 0.1;
  int depth_width;
  int depth_height;

  int   pos[][] = {
    {
      50, 50
    }
    , {
      640+50, 240+50
    }
  };

  BackGroundDiff(SimpleOpenNI kinect1, SimpleOpenNI kinect2)
  {
	opencv = new JavaCV();
    this.kinect1 = kinect1;
    this.kinect2 = kinect2;

    bgPoints = new ArrayList<Point>();
    
    blobs = new Blobs();

    //kinect.enableRGB(); // カラー画像有効化
    kinect1.update();
    kinect2.update();

    depth_width  = kinect1.depthWidth() + kinect2.depthWidth();
    depth_height = kinect1.depthHeight();

    currentImage = opencv_core.cvCreateImage(
    		opencv_core.cvSize(depth_width, depth_height), opencv_core.IPL_DEPTH_8U, 1);
    backgroundImage = opencv_core.cvCloneImage(
    		currentImage);

    rememberBackground();

    update();
  }

  void update()  
  {    
    kinect1.update();
    kinect2.update();

    retrieveDepthImage(currentImage);

    // Calculate the diff image
    opencv_core.cvAbsDiff(
    		currentImage,
    		backgroundImage,
    		currentImage);

    // Blur
    opencv_imgproc.cvSmooth(
    		currentImage,
    		currentImage,
    		opencv_imgproc.CV_BLUR_NO_SCALE,
    		3);

    // Binarization
    opencv_imgproc.cvThreshold(
    		currentImage,
    		currentImage,
    		threshold, threshold,
    		opencv_imgproc.CV_THRESH_BINARY);

    // Dilation
    opencv_imgproc.cvDilate(
    		currentImage,
    		currentImage, null, 5);
    
    // Detect blobs
    /*
    CvMemStorage mem = opencv_core.cvCreateMemStorage(0);
    CvSeq contours = new CvSeq();
    CvSeq ptr = new CvSeq();
    opencv_imgproc.cvFindContours(
    		currentImage,
    		mem,
    		contours,
    		Loader.sizeof(CvContour.class),
    		opencv_imgproc.CV_RETR_CCOMP,
    		opencv_imgproc.CV_CHAIN_APPROX_SIMPLE,
    		opencv_core.cvPoint(0,0));
    */
    // http://code.google.com/p/javacv/source/browse/samples/BlobDemo.java
    blobs.BlobAnalysis(currentImage,
    		-1, -1, // ROI start col, row
    		-1, -1, // ROI cols, rows
    		1, // border (0 = black; 1 = white)
    		6);
  }

  void rememberBackground()
  {
    System.out.println("remember background!!!");
    retrieveDepthImage(backgroundImage);
  }

  void retrieveDepthImage(IplImage target)
  {
	combineDepthImage(target,
			kinect1.depthImage(), kinect2.depthImage(),
			0, 0, kinect1.depthWidth(), kinect1.depthHeight(),
			0, 0, kinect2.depthWidth(), kinect2.depthHeight());
	fixDepthImage(target,
			kinect1.depthMap(), kinect2.depthMap(),
			0, 0, kinect1.depthWidth(), kinect1.depthHeight(),
			0, 0, kinect2.depthWidth(), kinect2.depthHeight());
  }

  PImage DilateWhite(OpenPool op, PImage in, int times)
  {
    int BLACK = 0;
    int WHITE = 0xffffff;
    PImage out;
    out = in.get();

    for (int t=0; t<times;t++)
    {
      //
      for (int i=0;i<in.width;i++)
      {
        out.set(i, 0, BLACK);
        out.set(i, in.height, BLACK);
      }
      for (int j=0;j<in.height;j++)
      {
        out.set(0, j, BLACK);
        out.set(in.width, j, BLACK);
      }

      for (int i = 1 ; i < in.width-1 ; i++)
      {
        for (int j = 1 ; j < in.height-1 ; j++ )
        {
          if (
          in.get(i-1, j-1) == WHITE &&
            in.get(i, j-1) == WHITE &&
            in.get(i+1, j-1) == WHITE &&
            in.get(i-1, j) == WHITE &&
            in.get(i+1, j) == WHITE &&
            in.get(i-1, j+1) == WHITE &&
            in.get(i, j+1) == WHITE &&
            in.get(i+1, j+1) == WHITE 
            )
          {
            out.set(i, j, WHITE);
          }
          else
          { 
            out.set(i, j, BLACK);
          }
        }
      }
    }
    return out;
  }

  void draw(OpenPool op)
  {
    bgPoints.clear();    

    /*
    for (Blob blob:blobs)
    {
      Point pt = new Point();
      pt.x = (blob.centroid.x* (pos[1][0]-pos[0][0])) / depthImage.width + pos[0][0];
      pt.y = (blob.centroid.y* (pos[1][1]-pos[0][1])) / depthImage.height + pos[0][1]; 
      bgPoints.add(pt);

      if (OpenPool.DEBUG)
      {
        op.pa.line(pt.x-50, pt.y, pt.x+50, pt.y);
        op.pa.line(pt.x, pt.y-50, pt.x, pt.y+50);
        text( str(blob.area), pt.x, pt.y-30);
      }
    }
    */

    if (OpenPool.DEBUG)
    {
      op.pa.stroke(255,255,  0);
      op.pa.fill(255,255, 0);
      //draw image boundingbox
      op.pa.line(pos[0][0],pos[0][1],pos[1][0],pos[0][1]);
      op.pa.line(pos[1][0],pos[0][1],pos[1][0],pos[1][1]);
      op.pa.line(pos[1][0],pos[1][1],pos[0][0],pos[1][1]);
      op.pa.line(pos[0][0],pos[1][1],pos[0][0],pos[0][1]);
      
      //draw an arrow

      op.pa.line(pos[0][0], pos[0][1], pos[0][0]+8, pos[0][1]+4);
      op.pa.line(pos[0][0], pos[0][1], pos[0][0]+4, pos[0][1]+8);
      op.pa.line(pos[0][0], pos[0][1], pos[0][0]+10, pos[0][1]+10);

      //draw xy
      op.pa.text("X:", pos[0][0]+20, pos[0][1]+20);
      op.pa.text(pos[0][0], pos[0][0]+30, pos[0][1]+20);
      op.pa.text("Y:", pos[0][0]+20, pos[0][1]+30);
      op.pa.text(pos[0][1], pos[0][0]+30, pos[0][1]+30);

      //draw an arrow
      op.pa.line(pos[1][0], pos[1][1], pos[1][0]-8, pos[1][1]-4);
      op.pa.line(pos[1][0], pos[1][1], pos[1][0]-4, pos[1][1]-8);
      op.pa.line(pos[1][0], pos[1][1], pos[1][0]-10, pos[1][1]-10);

      //draw xy
      op.pa.text("X:", pos[1][0]-50, pos[1][1]-10);
      op.pa.text(pos[1][0], pos[1][0]-40, pos[1][1]-10);
      op.pa.text("Y:", pos[1][0]-50, pos[1][1]-20);
      op.pa.text(pos[1][1], pos[1][0]-40, pos[1][1]-20);

      op.pa.noFill();
      op.pa.stroke(255, 255, 255);

      //draw depthimage
      BufferedImage currentBufferedImage = new BufferedImage(currentImage.width(), currentImage.height(), BufferedImage.TYPE_INT_ARGB);
      currentImage.copyTo(currentBufferedImage);
      op.pa.image(new PImage(currentBufferedImage), pos[0][0], pos[0][1], pos[1][0]-pos[0][0], pos[1][1]-pos[0][1]);
    }
  }

  void combineDepthImage(IplImage combined,
		  PImage img1, PImage img2,
		  int img1x, int img1y, int img1w, int img1h,
		  int img2x, int img2y, int img2w, int img2h)
  {
	  copyImage(combined, img1, img1x, img1y, img1w, img1h);
	  copyImage(combined, img2, img2x, img2y, img2w, img2h);
  }
  
  void copyImage(IplImage target,
		  PImage source, int x, int y, int width, int height) {
	    IplImage iplImage1 = IplImage.createFrom((BufferedImage)kinect1.depthImage().getImage());
	    opencv_core.cvSetImageROI(target, opencv_core.cvRect(
	    		x, y,
	    		width, height));
	    opencv_core.cvCopy(iplImage1, target);
  }
  
  void fixDepthImage(IplImage combined,
		  int[] img1, int[] img2,
		  int img1x, int img1y, int img1w, int img1h,
		  int img2x, int img2y, int img2w, int img2h)
  {
	  fixDepthImage(combined, img1, img1x, img1y, img1w, img1h);
	  fixDepthImage(combined, img2, img2x, img2y, img2w, img2h);
  }
  
  void fixDepthImage(IplImage target,
		  int[] depthMap, int x, int y, int width, int height) {
		ByteBuffer bb1 = target.asByteBuffer();
		for (int targetX = x >= 0 ? x : 0; targetX < target.width(); targetX ++) {
			for (int targetY = y >= 0 ? y : 0; targetY < target.height(); targetY ++) {
				int depth = depthMap[targetX - x + (targetY - y) * width];

				// Assume depth errors are caused by the black ball
				if (depth <= 0) {
					bb1.put(targetX + targetY * width, (byte) 0xff);
				}
			}
		}
  }
}

