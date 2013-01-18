/***********************************************************************
 
 Copyright (c) takashyx 2012. ( http://takashyx.com )
 * All rights reserved.
 
 This work is licensed under a Creative Commons Attribution-ShareAlike
 3.0 Unported License.(http://creativecommons.org/licenses/by-sa/3.0/)
 
 For the Particle System MSAFluid:
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of MSA Visuals nor the names of its contributors 
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * ***********************************************************************/
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import msafluid.*;
import SimpleOpenNI.*;
import processing.core.*;

//OpenGL
import processing.opengl.*;
import javax.media.opengl.*;

public class OpenPool {
PApplet pa;

//Field
Field field;

//Particle fluid config
float invWidth, invHeight;    // inverse of screen dimensions
float aspectRatio, aspectRatio2;

//Fluid and Particle
MSAFluidSolver2D fluidSolver;
ParticleSystem particleSystem;

PImage imgFluid;
boolean drawFluid = true;
static boolean DEBUG = false;
final float FLUID_WIDTH = 120;

//Fish config
float SPEED = 5;
float R = 4;       
int FISHNUMBER = 10;   // number of fishes
int FISHFORCE = 2000;

//Ball config
int BALLNUM = 8;
int BALLRINGS = 8;

BackGroundDiff bg;

//Shoal system
ShoalSystem shoalSystem;
BallSystem ballSystem;


int   selected = -1;  // �I������Ă��钸�_


float SHOALCOLISION = 100;

int timecount;

float r1 = 1.0f;   //param: shoal gathering
float r2 = 0.1f; //  param: avoid conflict with other fishes in shoal 
float r3 = 0.5f; // param: along with other fish in shoal
float r4 = 1;   //  param: avoid other shoal
float r5 = 100;   //  param: avoid balls

int redaddx = -100; //initial position of the red shoal
int redaddy = 0;

int blueaddx = 100; //initial position of the blue shoal
int blueaddy = 0;

int greenaddx = 0;  //initial position of the green shoal
int greenaddy = 0;

// margin for billiard pool edge
int wband = 80;
int hband = 80;

//img for billiard pool background
PImage img;

public OpenPool(PApplet pa) 
{
  this.pa = pa;

  //498*2
  //282*2
  pa.size(996, 564, PApplet.OPENGL);
  pa.hint( PApplet.ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
  //frameRate(30);

  //backgrounddiff
  SimpleOpenNI kinect1 = new SimpleOpenNI(1, pa);
  SimpleOpenNI kinect2 = new SimpleOpenNI(0, pa);
      /*
    if ( kinect1.openFileRecording("straight1.oni") == false)
    {
      println("can't find recorded file1 !!!!");
      exit();
    }
    if ( _kinect1.openFileRecording("straight2.oni") == false)
    {
      println("can't find recorded file2 !!!!");
      exit();
    }
    */
     // enable depthMap generation 
  if(kinect1.enableDepth() == false)
  {
     throw new IllegalStateException("Can't open the depthMap of cam1, maybe the camera is not connected!");
  }
 
  // enable depthMap generation 
  if(kinect2.enableDepth() == false)
  {
     throw new IllegalStateException("Can't open the depthMap of cam2, maybe the camera is not connected!");
  }
    kinect1.enableDepth();                       // 距離画像有効化
    kinect2.enableDepth();
    
  bg = new BackGroundDiff(kinect1,kinect2);

  timecount = 0;

  PVector v1 = new PVector(0+wband, 0+hband);
  PVector v2 = new PVector(pa.width-wband, 0+hband);
  PVector v3 = new PVector(pa.width-wband, pa.height-hband);
  PVector v4 = new PVector(0+wband, pa.height-hband);
  field = new Field(v1, v2, v3, v4);

  invWidth  = 1.0f/pa.width;
  invHeight = 1.0f/pa.height;
  aspectRatio  = pa.width * invHeight;
  aspectRatio2 = aspectRatio * aspectRatio;

  // create fluid and set options
  fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * pa.height/pa.width));
  fluidSolver.enableRGB(true).setFadeSpeed(0.05f).setDeltaT(0.5f).setVisc(0.001f);

  // create image to hold fluid picture
  imgFluid = pa.createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), PApplet.RGB);

  // create particle system
  particleSystem = new ParticleSystem();

  pa.stroke(255, 255, 255);

  img = pa.loadImage("billiards.jpg");
  pa.tint(255, 127);

  shoalSystem = new ShoalSystem(this);

  shoalSystem.addShoal(this, 1, 0.75f, 0.75f, redaddx, redaddy, FISHNUMBER, SPEED);
  shoalSystem.addShoal(this, 0.75f, 1, 0.75f, greenaddx, greenaddy, FISHNUMBER, SPEED);
  shoalSystem.addShoal(this, 0.75f, 0.75f, 1, blueaddx, blueaddy, FISHNUMBER, SPEED);
  shoalSystem.addShoal(this,     1, 1,     1, greenaddx, greenaddy, FISHNUMBER, SPEED);
  shoalSystem.addShoal(this,     1, 1,     1, greenaddx, greenaddy, FISHNUMBER, SPEED);

  ballSystem = new BallSystem();

  /*
  setBallandSetAvoid(200, 180, 50);
   setBallandSetAvoid(200, 380, 50);
   setBallandSetAvoid(400, 180, 50);
   setBallandSetAvoid(400, 380, 50);
   setBallandSetAvoid(600, 180, 50);
   setBallandSetAvoid(600, 380, 50);
   setBallandSetAvoid(800, 180, 50);
   setBallandSetAvoid(800, 380, 50);
   */
}

//main draw
void draw()
{
  pa.background(0);
  if (timecount >= 2*50)
  {
    timecount -= 2*50;
  }
  timecount++;
  //println(timecount);


  if (DEBUG)
  {
    pa.image(img, 0, 0, 498*2, 282*2);
    field.Draw(this);
  }

  //draw shoals
  shoalSystem.Update(this);
  shoalSystem.Draw(this);

  //draw particles
  fluidSolver.update();

  if (drawFluid)
  {
    for (int i=0; i<fluidSolver.getNumCells(); i++)
    {
      int d = 1;
      imgFluid.pixels[i] = pa.color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
    }  

    imgFluid.updatePixels();
    pa.image(imgFluid, 0, 0, pa.width, pa.height);
  } 
  //clear all Balls
  clearBallandAvoid();

  bg.update();
  bg.draw(this);
  //draw balls
  int itercount=0;
  ArrayList<Point> bgPoints = bg.bgPoints;
  Iterator<Point> iter = bgPoints.iterator();
  while (iter.hasNext ())
  {
    Point pt = (Point)iter.next();
    setBallandSetAvoid(pt.x, pt.y, 30);
    itercount++;
  }
  if (DEBUG)
  {
    pa.text("ball count:", 900, 20);
    pa.text(bgPoints.size(), 970, 20);
  }
  if (!DEBUG)
  {
    ballSystem.draw(this);
    particleSystem.updateAndDraw(this);
  }

  //////////////////////////////////////////////////  
  if ( pa.mousePressed && selected >= 0 )
  {
    bg.pos[selected][0] = pa.mouseX;
    bg.pos[selected][1] = pa.mouseY;
  }
  else 
  {
    float min_d = 20; 
    selected = -1;
    for (int i=0; i<2; i++) {
      float d = PApplet.dist( pa.mouseX, pa.mouseY, bg.pos[i][0], bg.pos[i][1] );
      if ( d < min_d ) {
        min_d = d;
        selected = i;
      }
    }
  }
  if ( selected >= 0 ) {
    pa.ellipse( pa.mouseX, pa.mouseY, 20, 20 );
  }
}

void mouseMoved()
{
  float mouseNormX = pa.mouseX * invWidth;
  float mouseNormY = pa.mouseY * invHeight;
  float mouseVelX = (pa.mouseX - pa.pmouseX) * invWidth;
  float mouseVelY = (pa.mouseY - pa.pmouseY) * invHeight;

  addForceToFluid(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
}

void mousePressed()
{
  ;
}

void OutputStatus()
{
  if (DEBUG)
  {
    PApplet.println("DEBUG MODE");
  }
  else
  {
    PApplet.println("NORMAL MODE");
  }
}

void keyPressed()
{
  switch(pa.key)
  {
  case'b':
    bg.rememberBackground();
    break;
  case 'r': 
    renderUsingVA ^= true; 
    println("renderUsingVA: " + renderUsingVA);
    break;
  case ' ':
    DEBUG^= true;
    drawFluid^=true;
    OutputStatus();
    break;
  }
  PApplet.print("FRAMERATE: ");
  PApplet.println(pa.frameRate);
}

void clearBallandAvoid()
{
  ballSystem.clearBall();
  shoalSystem.clearAvoidEllipseObject();
}

void setBallandSetAvoid(int x, int y, int R)
{
  ballSystem.addBall(x, y, R, R, BALLRINGS);
  shoalSystem.addEllipseObject(x, y, R*2);
}


// add force and dye to fluid, and create particles
void addForceToFluid(float x, float y, float dx, float dy)
{
  float speed = dx * dx  + dy * dy * aspectRatio2;    // balance the x and y components of speed with the screen aspect ratio

  if (speed > 0) {
    if (x<0) x = 0; 
    else if (x>1) x = 1;
    if (y<0) y = 0; 
    else if (y>1) y = 1;

    float colorMult = 5;
    float velocityMult = 30.0f;

    int index = fluidSolver.getIndexForNormalizedPosition(x, y);

    int drawColor;

    pa.colorMode(PApplet.HSB, 360, 1, 1);
    float hue = ((x + y) * 180 + pa.frameCount) % 360;
    drawColor = pa.color(hue, 1, 1);
    pa.colorMode(PApplet.RGB, 1);  

    fluidSolver.rOld[index]  += pa.red(drawColor) * colorMult;
    fluidSolver.gOld[index]  += pa.green(drawColor) * colorMult;
    fluidSolver.bOld[index]  += pa.blue(drawColor) * colorMult;

    particleSystem.addParticles(x * pa.width, y * pa.height, 10);
    fluidSolver.uOld[index] += dx * velocityMult;
    fluidSolver.vOld[index] += dy * velocityMult;
  }
}

}
