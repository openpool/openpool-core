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
import msafluid.*;
import SimpleOpenNI.*;

//OpenGL
import processing.opengl.*;
import javax.media.opengl.*;

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
boolean DEBUG = false;
final float FLUID_WIDTH = 120;

//Fish config
float SPEED = 5;
float R = 4;       
int NUMBER = 10;   // number of fishes
int FISHFORCE = 2000;

//Ball config
int BALLNUM = 8;
int BALLRINGS = 8;

//backgrounddiff
SimpleOpenNI kinect;
BackGroundDiff bg;

//Shoal system
ShoalSystem shoalSystem;
BallSystem ballSystem;


int   selected = -1;  // �I������Ă��钸�_


float SHOALCOLISION = 100;

int timecount;

float r1 = 1.0;   //param: shoal gathering
float r2 = 0.1; //  param: avoid conflict with other fishes in shoal 
float r3 = 0.5; // param: along with other fish in shoal
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

void setup() 
{
  //498*2
  //282*2
  size(996, 564, OPENGL);
  hint( ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
  //frameRate(30);

  //backgrounddiff
  kinect = new SimpleOpenNI(this);
  bg = new BackGroundDiff(kinect);

  timecount = 0;

  PVector v1 = new PVector(0+wband, 0+hband);
  PVector v2 = new PVector(width-wband, 0+hband);
  PVector v3 = new PVector(width-wband, height-hband);
  PVector v4 = new PVector(0+wband, height-hband);
  field = new Field(v1, v2, v3, v4);

  invWidth  = 1.0f/width;
  invHeight = 1.0f/height;
  aspectRatio  = width * invHeight;
  aspectRatio2 = aspectRatio * aspectRatio;

  // create fluid and set options
  fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * height/width));
  fluidSolver.enableRGB(true).setFadeSpeed(0.05).setDeltaT(0.5).setVisc(0.001);

  // create image to hold fluid picture
  imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);

  // create particle system
  particleSystem = new ParticleSystem();

  stroke(255, 255, 255);

  img = loadImage("billiards.jpg");
  tint(255, 127);

  shoalSystem = new ShoalSystem();

  shoalSystem.addShoal(1, 0.75, 0.75, redaddx, redaddy, NUMBER, SPEED);
  shoalSystem.addShoal(0.75, 1, 0.75, greenaddx, greenaddy, NUMBER, SPEED);
  shoalSystem.addShoal(0.75, 0.75, 1, blueaddx, blueaddy, NUMBER, SPEED);
  shoalSystem.addShoal(   1, 1, 1, greenaddx, greenaddy, NUMBER, SPEED);
  shoalSystem.addShoal(   1, 1, 1, greenaddx, greenaddy, NUMBER, SPEED);

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
  background(0);
  if (timecount >= 2*50)
  {
    timecount -= 2*50;
  }
  timecount++;
  //println(timecount);


  if (DEBUG)
  {
    image(img, 0, 0, 498*2, 282*2);
    field.Draw();
  }

  //draw shoals
  shoalSystem.Update();
  shoalSystem.Draw();

  //draw particles
  fluidSolver.update();

  if (drawFluid)
  {
    for (int i=0; i<fluidSolver.getNumCells(); i++)
    {
      int d = 1;
      imgFluid.pixels[i] = color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
    }  

    imgFluid.updatePixels();
    image(imgFluid, 0, 0, width, height);
  } 
  //clear all Balls
  clearBallandAvoid();

  bg.update();
  bg.draw();
  //draw balls
  int itercount=0;
  Iterator iter = bg.Points.iterator();
  while (iter.hasNext ())
  {
    Point point = (Point)iter.next();
    setBallandSetAvoid(point.x, point.y, 30);
    itercount++;
  }
  if (DEBUG)
  {
    text("ball count:", 20, 20);
    text(bg.Points.size(), 100, 20);
  }
  if (!DEBUG)
  {
        ballSystem.draw();
    particleSystem.updateAndDraw();

  }

  //////////////////////////////////////////////////  
  if ( mousePressed && selected >= 0 )
  {
    bg.pos[selected][0] = mouseX;
    bg.pos[selected][1] = mouseY;
  }
  else 
  {
    float min_d = 20; 
    selected = -1;
    for (int i=0; i<2; i++) {
      float d = dist( mouseX, mouseY, bg.pos[i][0], bg.pos[i][1] );
      if ( d < min_d ) {
        min_d = d;
        selected = i;
      }
    }
  }
  if ( selected >= 0 ) {
    ellipse( mouseX, mouseY, 20, 20 );
  }
}

void mouseMoved()
{
  float mouseNormX = mouseX * invWidth;
  float mouseNormY = mouseY * invHeight;
  float mouseVelX = (mouseX - pmouseX) * invWidth;
  float mouseVelY = (mouseY - pmouseY) * invHeight;

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
    println("DEBUG MODE");
  }
  else
  {
    println("NORMAL MODE");
  }
}

void keyPressed()
{
  switch(key)
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
  print("FRAMERATE: ");
  println(frameRate);
}

void clearBallandAvoid()
{
  ballSystem.clearBall();
  shoalSystem.clearAvoidEllipseObject();
}

void setBallandSetAvoid(int x, int y, int R)
{
  ballSystem.addBall(x, y, R, R, BALLRINGS);
  shoalSystem.addEllipseObject(x, y, R);
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

    color drawColor;

    colorMode(HSB, 360, 1, 1);
    float hue = ((x + y) * 180 + frameCount) % 360;
    drawColor = color(hue, 1, 1);
    colorMode(RGB, 1);  

    fluidSolver.rOld[index]  += red(drawColor) * colorMult;
    fluidSolver.gOld[index]  += green(drawColor) * colorMult;
    fluidSolver.bOld[index]  += blue(drawColor) * colorMult;

    particleSystem.addParticles(x * width, y * height, 10);
    fluidSolver.uOld[index] += dx * velocityMult;
    fluidSolver.vOld[index] += dy * velocityMult;
  }
}

