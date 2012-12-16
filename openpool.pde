/***********************************************************************
 
 Copyright (c) takashyx 2012. ( http:/takashyx.com )
 * All rights reserved.
 
 For the Particle System MSAFluid
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

import msafluid.*;

//OpenGL
import processing.opengl.*;
import javax.media.opengl.*;

//Particle fluid config
float invWidth, invHeight;    // inverse of screen dimensions
float aspectRatio, aspectRatio2;

//Fluid and Particle
MSAFluidSolver2D fluidSolver;
ParticleSystem particleSystem;

PImage imgFluid;
boolean drawFluid = true;
boolean DEBUG = false;
boolean noUpdate = false;
final float FLUID_WIDTH = 120;

//Fish config
float SPEED = 5;
float R = 4;       
int NUMBER = 10;   // number of fishes
int FISHFORCE = 1500;

//Ball config
int BALLNUM = 8;
int BALLRINGS = 8;

//Shoal system
ShoalSystem shoalSystem;
BallSystem ballSystem;

float SHOALCOLISION = 100;

int timecount;

float r1 = 1.0;   //param: shoal gathering
float r2 = 0.1; //  param: avoid conflict with other fishes in shoal 
float r3 = 0.5; // param: along with other fish in shoal
float r4 = 1;   //  param: avoid other shoal
float r5 = 50;   //  param: avoid balls

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
  frameRate(30);

  timecount = 0;

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
  // shoalSystem.addShoal(   1, 1, 1, greenaddx, greenaddy, NUMBER, SPEED);
  //  shoalSystem.addShoal(   1, 1, 1, greenaddx, greenaddy, NUMBER, SPEED);

  ballSystem = new BallSystem();

  setBallandSetAvoid(200, 180, 50);
  setBallandSetAvoid(200, 380, 50);
  setBallandSetAvoid(400, 180, 50);
  setBallandSetAvoid(400, 380, 50);
  setBallandSetAvoid(600, 180, 50);
  setBallandSetAvoid(600, 380, 50);
  setBallandSetAvoid(800, 180, 50);
  setBallandSetAvoid(800, 380, 50);
}

//main draw
void draw()
{
  if (timecount >= 2*50)
  {
    timecount -= 2*50;
  }

  timecount++;
  println(timecount);
  
  background(0, 0, 0);
  image(img, 0, 0, 498*2, 282*2);

  //Field interaction

  //draw shoals
  if (!noUpdate)
  {
    shoalSystem.Update();
  }
  shoalSystem.Draw();

  //draw particles
  if (!noUpdate)
  {
    fluidSolver.update();
  }

  if (drawFluid)
  {
    if (!noUpdate)
    {
      for (int i=0; i<fluidSolver.getNumCells(); i++)
      {
        int d = 1;
        imgFluid.pixels[i] = color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
      }  

      imgFluid.updatePixels();
    }
    image(imgFluid, 0, 0, width, height);
  } 

  particleSystem.updateAndDraw();

  //draw balls

  //clear all Balls
  clearBallandAvoid();

  //TODO:update Ball x&y here   
  setBallandSetAvoid(200, 180, 50);
  setBallandSetAvoid(200, 380, 50);
  setBallandSetAvoid(400, 180, 50);
  setBallandSetAvoid(400, 380, 50);
  setBallandSetAvoid(600, 180, 50);
  setBallandSetAvoid(600, 380, 50);
  setBallandSetAvoid(800, 180, 50);
  setBallandSetAvoid(800, 380, 50);

  if (!DEBUG)
  {
    ballSystem.draw();
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
  DEBUG^= true;
  drawFluid^=true;
}

void keyPressed()
{
  switch(key)
  {
  case 'r': 
    renderUsingVA ^= true; 
    println("renderUsingVA: " + renderUsingVA);
    break;
  case ' ':
    noUpdate ^=true;
    println("PAUSE/PLAY");
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
  shoalSystem.addAvoidEllipseObject(x, y, R);
}


// add force and dye to fluid, and create particles
void addForceToFluid(float x, float y, float dx, float dy) {
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

