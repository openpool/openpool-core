import monclubelec.javacvPro.*;
import SimpleOpenNI.*;
import java.awt.*;

class BackGroundDiff
{
  // OpenCV
  SimpleOpenNI kinect;

  OpenCV opencv;
  Blob[] blobsArray = null;
  ArrayList bgPoints;
  float threshold = 0.1;
  PImage depthImage;
  int[] depthMap;
  int depth_width;
  int depth_height;

  int   pos[][] = {
    {
      0, 0
    }
    , {
      400, 300
    }
  };

  BackGroundDiff(SimpleOpenNI _kinect)
  {
    kinect = _kinect;           // SimpleOpenNIの初期化

    bgPoints = new ArrayList();

    if ( kinect.openFileRecording("straight.oni") == false)
    {
      println("can't find recorded file !!!!");
      exit();
    }
    kinect.enableDepth();                       // 距離画像有効化
    //kinect.enableRGB();                         // カラー画像有効化
    kinect.update();

    depth_width = kinect.depthWidth();
    depth_height =kinect.depthHeight();

    depthImage = kinect.depthImage();
    depthMap   = kinect.depthMap();
    opencv = new OpenCV();
    opencv.allocate(depth_width, depth_height);
    rememberBackground();

    update();
  }

  void update()
  {    
    kinect.update();
    depthImage = kinect.depthImage();
    depthImage = retrieveDepthImage();

    // Calculate the diff image
    opencv.copy(depthImage);

    opencv.absDiff(); // result stored in the secondary memory.
    opencv.restore2(); // restore the secondary memory data to the main buffer
    //opencv.blur(5);
    opencv.threshold(threshold, "BINARY");
    depthImage = opencv.getBuffer();
    depthImage = DilateWhite(depthImage, 2);
    
    // Detect blobs
    opencv.copy(depthImage);
    blobsArray = opencv.blobs(350, 750, 15, false, 100);
  }

  void rememberBackground()
  {
    println("remember background!!!");
    opencv.copy(kinect.depthImage());
    opencv.remember(); // Store in the first buffer.
  }

  PImage retrieveDepthImage()
  {
    PImage depthImage = kinect.depthImage();
    int[] depthMap   = kinect.depthMap();

    // Assume depth errors are caused by the black ball
    color white = color(255);
    for (int x = 0; x < depth_width; x ++)
    {
      for (int y = 0; y < depth_height; y ++)
      {
        if (depthMap[x + y * depth_width] <= 0)
        {
          depthImage.set(x, y, white);
        }
      }
    }
    return depthImage;
  }

  PImage DilateWhite(PImage in, int times)
  {
    color BLACK = color(0, 0, 0);
    color WHITE = color(255, 255, 255);
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

  void draw()
  {
    bgPoints.clear();    

    
    for (Blob blob:blobsArray)
    {
      Point pt = new Point();
      pt.x = (blob.centroid.x* (pos[1][0]-pos[0][0])) / depthImage.width + pos[0][0];
      pt.y = (blob.centroid.y* (pos[1][1]-pos[0][1])) / depthImage.height + pos[0][1]; 
      bgPoints.add(pt);

      if (DEBUG)
      {
        line(pt.x-50, pt.y, pt.x+50, pt.y);
        line(pt.x, pt.y-50, pt.x, pt.y+50);
        text( str(blob.area), pt.x, pt.y-30);
      }
    }

    if (DEBUG)
    {
      //draw an arrow
      line(pos[0][0], pos[0][1], pos[0][0]+5, pos[0][1]);
      line(pos[0][0], pos[0][1], pos[0][0], pos[0][1]+5);
      line(pos[0][0], pos[0][1], pos[0][0]+10, pos[0][1]+10);
      
      //draw xy
      text("X:", pos[0][0]+20, pos[0][1]+20);
      text(pos[0][0], pos[0][0]+30, pos[0][1]+20);
      text("Y:", pos[0][0]+20, pos[0][1]+30);
      text(pos[0][1], pos[0][0]+30, pos[0][1]+30);

      //draw an arrow
      line(pos[1][0], pos[1][1], pos[1][0]-5, pos[1][1]);
      line(pos[1][0], pos[1][1], pos[1][0], pos[1][1]-5);
      line(pos[1][0], pos[1][1], pos[1][0]-10, pos[1][1]-10);
      
      //draw xy
      text("X:", pos[1][0]-50, pos[1][1]-10);
      text(pos[1][0], pos[1][0]-40, pos[1][1]-10);
      text("Y:", pos[1][0]-50, pos[1][1]-20);
      text(pos[1][1], pos[1][0]-40, pos[1][1]-20);
      
      //draw depthimage
      image(depthImage, pos[0][0], pos[0][1], pos[1][0]-pos[0][0], pos[1][1]-pos[0][1]);
    }
  }

  /*Point TranslateXY(Point in)
   {
   Point out;
   out.x = in.x*(pos[1][0]-pos[0][0]/depthImage.width) + pos[0][0];
   out.y = in.y*(pos[1][1]-pos[0][1]/depthImage.height) + pos[0][1];
   return out;
   };
   */
}

