import monclubelec.javacvPro.*;
class BackGroundDiff
{
  // OpenCV
  OpenCV opencv;
  Blob[] blobsArray = null;
  float threshold = 0.2;
  PImage depthImage;
  int[] depthMap;
  int depth_width;
  int depth_height;

  BackGroundDiff(int _depth_width, int _depth_height, PImage _depthImage, int[] _depthMap)
  {
    depth_width = _depth_width;
    depth_height = _depth_height;

    depthImage = _depthImage;
    depthMap   = _depthMap;
    opencv = new OpenCV();
    opencv.allocate(_depth_width, _depth_height);
    rememberBackground(_depthImage);

    update(_depthImage, _depthMap);
  }

  void update(PImage _depthImage, int[] _depthMap)
  {
    depthImage = _depthImage.get();
    depthMap   = _depthMap;

    // Update the camera image
    PImage depthImage = retrieveDepthImage();

    // Calculate the diff image
    opencv.copy(depthImage);
    opencv.absDiff(); // result stored in the secondary memory.
    opencv.restore2(); // restore the secondary memory data to the main buffer
    opencv.blur(3);
    opencv.threshold(threshold, "BINARY");
    depthImage = opencv.getBuffer();
    depthImage = DilateWhite(depthImage, 1); //DilateElode(depthImage, 2);

    // Detect blobs
    opencv.copy(depthImage);
    blobsArray = opencv.blobs(25, 2000, 20, false, 100);
  }
  void rememberBackground(PImage _depthImage)
  {
    opencv.copy(_depthImage);
    opencv.remember(); // Store in the first buffer.
  }

  PImage retrieveDepthImage()
  {
    // Assume depth errors are caused by the black ball
    color white = color(255);
    for (int x = 0; x < depth_width; x ++) {
      for (int y = 0; y < depth_height; y ++) {
        if (depthMap[x + y * depth_width] <= 0) {
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
    opencv.copy(depthImage);
    blobsArray = opencv.blobs(25, 2000, 20, false, 100);

    java.awt.Point point = new java.awt.Point();
    for(Blob blob:blobsArray)
    {
      point = blob.centroid;
      print("x:");
      print(point.x);
      print(" y:");
      println(point.y);
    }
    //opencv.drawBlobs(blobsArray, 0, 0, 0.5);
  }
}

