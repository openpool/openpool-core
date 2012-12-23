import processing.core.*; 
import processing.xml.*; 

import msafluid.*; 
import processing.opengl.*; 
import javax.media.opengl.*; 
import java.nio.FloatBuffer; 
import com.sun.opengl.util.*; 

import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class openpool extends PApplet {

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



//OpenGL



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
boolean noUpdate = false;
final float FLUID_WIDTH = 120;

//Fish config
float SPEED = 5;
float R = 4;       
int NUMBER = 10;   // number of fishes
int FISHFORCE = 2000;

//Ball config
int BALLNUM = 8;
int BALLRINGS = 8;

//Shoal system
ShoalSystem shoalSystem;
BallSystem ballSystem;

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

public void setup() 
{
  //498*2
  //282*2
  size(996, 564, OPENGL);
  hint( ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
  frameRate(30);

  timecount = 0;
  
  PVector v1 = new PVector(0+wband,0+hband);
  PVector v2 = new PVector(width-wband,0+hband);
  PVector v3 = new PVector(width-wband,height-hband);
  PVector v4 = new PVector(0+wband,height-hband);
  field = new Field(v1,v2,v3,v4);

  invWidth  = 1.0f/width;
  invHeight = 1.0f/height;
  aspectRatio  = width * invHeight;
  aspectRatio2 = aspectRatio * aspectRatio;

  // create fluid and set options
  fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * height/width));
  fluidSolver.enableRGB(true).setFadeSpeed(0.05f).setDeltaT(0.5f).setVisc(0.001f);

  // create image to hold fluid picture
  imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);

  // create particle system
  particleSystem = new ParticleSystem();

  stroke(255, 255, 255);

  img = loadImage("billiards.jpg");
  tint(255, 127);

  shoalSystem = new ShoalSystem();

  shoalSystem.addShoal(1, 0.75f, 0.75f, redaddx, redaddy, NUMBER, SPEED);
  shoalSystem.addShoal(0.75f, 1, 0.75f, greenaddx, greenaddy, NUMBER, SPEED);
  shoalSystem.addShoal(0.75f, 0.75f, 1, blueaddx, blueaddy, NUMBER, SPEED);
  shoalSystem.addShoal(   1, 1, 1, greenaddx, greenaddy, NUMBER, SPEED);
  shoalSystem.addShoal(   1, 1, 1, greenaddx, greenaddy, NUMBER, SPEED);

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
public void draw()
{
  if (timecount >= 2*50)
  {
    timecount -= 2*50;
  }
  timecount++;
  //println(timecount);

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
  setBallandSetAvoid(200+timecount*2, 180, timecount/2);
  setBallandSetAvoid(200, 380-timecount*2, 50-timecount/2);
  setBallandSetAvoid(400+timecount*2, 180, 50-timecount/2);
  setBallandSetAvoid(400-timecount*2, 380, timecount/2);
  setBallandSetAvoid(600+timecount*2, 180, timecount/2);
  setBallandSetAvoid(600-timecount*2, 380, 50-timecount/2);
  setBallandSetAvoid(800, 180+timecount*2, 50-timecount/2);
  setBallandSetAvoid(800-timecount*2, 380, timecount/2);

  if (DEBUG)
  {
    field.Draw();
  }
  else
  {
    ballSystem.draw();
  }
}

public void mouseMoved()
{
  float mouseNormX = mouseX * invWidth;
  float mouseNormY = mouseY * invHeight;
  float mouseVelX = (mouseX - pmouseX) * invWidth;
  float mouseVelY = (mouseY - pmouseY) * invHeight;

  addForceToFluid(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
}

public void mousePressed()
{
  DEBUG^= true;
  drawFluid^=true;
  OutputStatus();
}

public void OutputStatus()
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

public void keyPressed()
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

public void clearBallandAvoid()
{
  ballSystem.clearBall();
  shoalSystem.clearAvoidEllipseObject();
}

public void setBallandSetAvoid(int x, int y, int R)
{
  ballSystem.addBall(x, y, R, R, BALLRINGS);
  shoalSystem.addEllipseObject(x, y, R);
}


// add force and dye to fluid, and create particles
public void addForceToFluid(float x, float y, float dx, float dy) {
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

class Ball
{
  int x;
  int y;
  int R;
  int realr;

  int RINGNUM;

  //Construct
  Ball(int _x, int _y, int _realr, int _R, int _RINGNUM)
  {
    x = _x;
    y = _y;
    realr = _realr;
    R = _R;
    RINGNUM = _RINGNUM;

  }

  public void draw()
  {
    stroke(255, 255, 255);
    fill(255, 255, 255);
    ellipse(x, y, 2*realr, 2*realr);// R*2, R*2);
    noFill();

    for (int i=0;i<RINGNUM;i++)
    {
      int tempring = timecount + i*(4*R/RINGNUM);

      if (2*R < tempring && tempring < 4*R)
      {
        //TODO: need improvement for gradation
        stroke(255, 255, 255, 
        255*(6*R-tempring)/(20*R));
        //fill(0, 255, 0);
        ellipse(x, y, tempring, tempring);
        //TODO: change transparency of the ring
      }
    }
  }
}
class BallSystem
{
  ArrayList balls;

  BallSystem()
  {
    balls = new ArrayList();
    return;
  }
  public void addBall(int _x, int _y, int _realr, int _R, int _RINGNUM)
  {
    Ball ball = new Ball(_x, _y, _realr, _R, _RINGNUM);
    balls.add(ball);
  }
  
  public void clearBall()
  {
    balls.clear();
  }
  public void draw()
  {
    Iterator iter = balls.iterator();
    while(iter.hasNext())
    {
      Ball ball = (Ball)iter.next();
      ball.draw();
    }
  }
}
class EllipseObject
{
  int x;
  int y;
  int R;
  int ID;

  EllipseObject(int _x, int _y, int _R)
  {
    x = _x;
    y = _y;
    R = _R;
    //ID = hogefuga;
  }

  public void draw()
  {
    if (DEBUG)
    {
      ellipse(x, y, R*2, R*2);
      text("object", x, y);
      text("x:", x, y+15);
      text(x, x+30, y+15);
      text("y:", x, y+30);
      text(y, x+30, y+30);
      text("R:", x, y+45);
      text(R, x+30, y+45);
    }
  }
}

class Field
{
  ArrayList AreaPoints;

  Field(PVector p1, PVector p2, PVector p3, PVector p4)
  {
    AreaPoints = new ArrayList();
    AreaPoints.add(p1);
    AreaPoints.add(p2);
    AreaPoints.add(p3);
    AreaPoints.add(p4);
  }
  public void Draw()
  {
    Iterator iter = AreaPoints.iterator();
    PVector v_start = (PVector) iter.next();
    PVector v1 = v_start;
    while (iter.hasNext ())
    {
      PVector v2 = (PVector)iter.next();
      line(v1.x,v1.y,v2.x,v2.y);
      v1=v2;
    }
    line(v1.x,v1.y,v_start.x,v_start.y);
  }
}

class Fish
{

  float speed;
  float r, g, b;
  float x, y;   //location of fish
  float vx, vy; //speed of fish
  PVector v1 = new PVector();  //for param1
  PVector v2 = new PVector();  //for param2
  PVector v3 = new PVector();  //for param3
  PVector v4 = new PVector();  //for param4
  PVector v5 = new PVector();  //for p

  int id;
  // Fish[] others;

  //constructor
  Fish(float _x, float _y, 
  float _vx, float _vy, 
  int _id, 
  float _r, float _g, float _b, 
  float _speed)
    //Fish[] _others
  {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    id = _id;
    //others = _others;

    r=_r;
    g=_g;
    b=_b;
    speed = _speed;
    return;
  }

  public void move()
  {
    vx += r1 * v1.x + r2 * v2.x + r3 * v3.x + r4 * v4.x + r5 * v5.x;
    vy += r1 * v1.y + r2 * v2.y + r3 * v3.y + r4 * v4.y + r5 * v5.y;

/*
    print(" r1*v1.x:");    
    print(r1*v1.x);
    print(" r1*v1.y:");    
    print(r1*v1.y); 
    print(" r2*v2.x:");    
    print(r2*v2.x);
    print(" r2*v2.y:");    
    print(r2*v2.y); 
    print(" r3*v3.x:");    
    print(r3*v3.x);
    print(" r3*v3.y:");    
    print(r3*v3.y); 

    print(" r4*v4.x:");      
    print(r5*v5.x);
    print(" r4*v4.y:");      
    println(r5*v5.y);
    print(" r5*v5.x:");      
    print(r5*v5.x);
    print(" r5*v5.y:");      
    println(r5*v5.y);
*/

    //max speed check 
    float vVector = sqrt(vx * vx + vy * vy);
    if (vVector > speed) 
    {
      vx = (vx / vVector) * speed;
      vy = (vy / vVector) * speed;
    }

    x += vx;
    y += vy;

    if (x - R <= 0 + wband) 
    {
      x = R + wband;
      vx *= -1;
    }
    if (x + R >= (width - wband)) 
    {
      x = width - wband - R;
      vx *= -1;
    }

    if (y - R <= 0 + hband) 
    {
      y = R + hband;
      vy *= -1;
    }
    if (y + R >= height - hband) 
    {
      y = height - R - hband;
      vy *= -1;
    }
  }

  public void draw() 
  {
    float dx = 0;
    float dy = 0;
    float rtemp;

    noStroke();
    fill(r, g, b, 100);

    for (int i = 0 ; i < 5 ; i++)
    {
      dx = -vx * 5 * i / 10;
      dy = -vy * 5 * i / 10;
      rtemp = R * (5-i) / 10;
      ellipse(x-dx, y-dy, rtemp * 2, rtemp * 2);
    }
    for (int i = 0 ; i < 10 ; i++ )
    {
      noStroke();
      fill(r, g, b, 100);//255*((i)/10));
      dx = -vx * 5 * i / 10;
      dy = -vy * 5 * i / 10;
      rtemp = R * (10-i) / 10;
      ellipse(x+dx, y+dy, rtemp * 2, rtemp * 2);
    }
  }

  //init vectors
  public void clearVector()
  {
    v1.x = 0;
    v1.y = 0;
    v2.x = 0;
    v2.y = 0;
    v3.x = 0;
    v3.y = 0;
    v4.x = 0;
    v4.y = 0;
    v5.x = 0;
    v5.y = 0;
    //println("clearvector");
  }
}

class ObjectSystem
{
  ArrayList Objects;
  ObjectSystem()
  {
    Objects = new ArrayList();
  }
  public int addEllipseObject()
  {
    //return ID of the created object
    //return Objects.add(EllipseObject());
    return 0;
  }

}
/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
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

class Particle {
  final static float MOMENTUM = 0.5f;
  final static float FLUID_FORCE = 0.6f;

  float x, y;
  float vx, vy;
  float radius;       // particle's size
  float alpha;
  float mass;

  public void init(float x, float y) {
    this.x = x;
    this.y = y;
    vx = 0;
    vy = 0;
    radius = 5;
    alpha  = random(0.3f, 1);
    mass = random(0.1f, 1);
  }


  public void update()
  {
    // only update if particle is visible
    if (alpha == 0) return;

    // read fluid info and add to velocity
    int fluidIndex = fluidSolver.getIndexForNormalizedPosition(x * invWidth, y * invHeight);
    vx = fluidSolver.u[fluidIndex] * width * mass * FLUID_FORCE + vx * MOMENTUM;
    vy = fluidSolver.v[fluidIndex] * height * mass * FLUID_FORCE + vy * MOMENTUM;

    // update position
    x += vx;
    y += vy;

    // bounce of edges
    if (x<0) {
      x = 0;
      vx *= -1;
    }
    else if (x > width) {
      x = width;
      vx *= -1;
    }

    if (y<0) {
      y = 0;
      vy *= -1;
    }
    else if (y > height) {
      y = height;
      vy *= -1;
    }

    // hackish way to make particles glitter when the slow down a lot
    if (vx * vx + vy * vy < 1) {
      vx = random(-1, 1);
      vy = random(-1, 1);
    }

    // fade out a bit (and kill if alpha == 0);
    alpha *= 0.999f;
    if (alpha < 0.01f) alpha = 0;
  }


  public void updateVertexArrays(int i, FloatBuffer posBuffer, FloatBuffer colBuffer) {
    int vi = i * 4;
    posBuffer.put(vi++, x - vx);
    posBuffer.put(vi++, y - vy);
    posBuffer.put(vi++, x);
    posBuffer.put(vi++, y);

    int ci = i * 6;
    colBuffer.put(ci++, alpha);
    colBuffer.put(ci++, alpha);
    colBuffer.put(ci++, alpha);
    colBuffer.put(ci++, alpha);
    colBuffer.put(ci++, alpha);
    colBuffer.put(ci++, alpha);
  }


  public void drawOldSchool(GL gl) {
    gl.glColor3f(alpha, alpha, alpha);
    gl.glVertex2f(x-vx, y-vy);
    gl.glVertex2f(x, y);
  }
}







/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
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




boolean renderUsingVA = true;

public void fadeToColor(GL gl, float r, float g, float b, float speed) {
  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
  gl.glColor4f(r, g, b, speed);
  gl.glBegin(GL.GL_QUADS);
  gl.glVertex2f(0, 0);
  gl.glVertex2f(width, 0);
  gl.glVertex2f(width, height);
  gl.glVertex2f(0, height);
  gl.glEnd();
}


class ParticleSystem {
  FloatBuffer posArray;
  FloatBuffer colArray;

  final static int maxParticles = 5000;
  int curIndex;

  Particle[] particles;

  ParticleSystem() {
    particles = new Particle[maxParticles];
    for (int i=0; i<maxParticles; i++) particles[i] = new Particle();
    curIndex = 0;

    posArray = BufferUtil.newFloatBuffer(maxParticles * 2 * 2);// 2 coordinates per point, 2 points per particle (current and previous)
    colArray = BufferUtil.newFloatBuffer(maxParticles * 3 * 2);
  }


  public void updateAndDraw() {
    PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;         // processings opengl graphics object
    GL gl = pgl.beginGL();                // JOGL's GL object

    gl.glEnable( GL.GL_BLEND );             // enable blending
    if (!drawFluid) fadeToColor(gl, 0, 0, 0, 0.05f);

    gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);  // additive blending (ignore alpha)
    gl.glEnable(GL.GL_LINE_SMOOTH);        // make points round
    gl.glLineWidth(1);


    if (renderUsingVA) {
      for (int i=0; i<maxParticles; i++) {
        if (particles[i].alpha > 0) {
          particles[i].update();
          particles[i].updateVertexArrays(i, posArray, colArray);
        }
      }    
      gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
      gl.glVertexPointer(2, GL.GL_FLOAT, 0, posArray);

      gl.glEnableClientState(GL.GL_COLOR_ARRAY);
      gl.glColorPointer(3, GL.GL_FLOAT, 0, colArray);

      gl.glDrawArrays(GL.GL_LINES, 0, maxParticles * 2);
    } 
    else {
      gl.glBegin(GL.GL_LINES);               // start drawing points
      for (int i=0; i<maxParticles; i++) {
        if (particles[i].alpha > 0) {
          particles[i].update();
          particles[i].drawOldSchool(gl);    // use oldschool renderng
        }
      }
      gl.glEnd();
    }

    gl.glDisable(GL.GL_BLEND);
    pgl.endGL();
  }


  public void addParticles(float x, float y, int count ) {
    for (int i=0; i<count; i++) addParticle(x + random(-15, 15), y + random(-15, 15));
  }


  public void addParticle(float x, float y) {
    particles[curIndex].init(x, y);
    curIndex++;
    if (curIndex >= maxParticles) curIndex = 0;
  }
}






class Shoal
{
  ArrayList fishes;
  float x, y;
  float vx, vy;
  int CENTER_PULL_FACTOR;
  int DIST_THRESHOLD;

  Shoal()
  {
    fishes = new ArrayList();
    x=0;
    y=0;
    vx=0;
    vy=0;

    CENTER_PULL_FACTOR = 300;
    DIST_THRESHOLD = 30;
    return;
  }

  public void add(Fish _fish)
  {
    fishes.add(_fish);
  }


  public int size()
  {
    return fishes.size();
  }

  //calc param1
  public Fish shoalrules(Fish fish_i)
  {
    Iterator iter_j = fishes.iterator();
    while (iter_j.hasNext ())
    {
      Fish fish_j = (Fish)iter_j.next();
      if (fish_i != fish_j) 
      {
        //rule1
        fish_i.v1.x = fish_i.v1.x + fish_j.x;
        fish_i.v1.y = fish_i.v1.y + fish_j.y;

        //rule2
        if (dist(fish_i.x, fish_i.y, fish_j.x, fish_j.y) < DIST_THRESHOLD)
        {
          fish_i.v2.x -= (fish_j.x - fish_i.x);
          fish_i.v2.y -= (fish_j.y - fish_i.y);
        }

        //rule3         
        fish_i.v3.x += fish_j.vx;
        fish_i.v3.y += fish_j.vy;
      }
    }    

    //rule1
    fish_i.v1.x = ( fish_i.v1.x / (fishes.size() - 1)); 
    fish_i.v1.y = ( fish_i.v1.y / (fishes.size() - 1));

    fish_i.v1.x = (fish_i.v1.x - fish_i.x) / CENTER_PULL_FACTOR;
    fish_i.v1.y = (fish_i.v1.y - fish_i.y) / CENTER_PULL_FACTOR;

    //rule2 none

    //rule3
    fish_i.v3.x /= (fishes.size() - 1);
    fish_i.v3.y /= (fishes.size() - 1);

    fish_i.v3.x = (fish_i.v3.x - fish_i.vx)/2;
    fish_i.v3.y = (fish_i.v3.y - fish_i.vy)/2;

    return fish_i;
  }//end shoalrules

  public void clearVector()
  {  
    Iterator iter = fishes.iterator();

    while (iter.hasNext ())
    {
      Fish fish = (Fish)iter.next();
      fish.clearVector();
    }
  }
  public void update()
  {
    x=0;
    y=0;
    vx=0;
    vy=0;

    Iterator iter = fishes.iterator();

    while (iter.hasNext ())
    {
      Fish fish = (Fish)iter.next();

      x = x + fish.x;
      y = y + fish.y;

      shoalrules(fish);
      //rule4

      fish.move();

      vx = vx + fish.vx;
      vy = vy + fish.vy;

      addForceToFluid(fish.x/width, fish.y/height, -fish.vx/FISHFORCE, -fish.vy/FISHFORCE);
    }
    x = x / fishes.size();
    y = y / fishes.size();

    vx = vx / fishes.size();
    vy = vy / fishes.size();

    return;
  }

  public void addForce(float _vx, float _vy)
  {
    Iterator iter = fishes.iterator();
    while (iter.hasNext ())
    {
      Fish fish = (Fish)iter.next();  
      fish.v4.x = fish.v4.x + _vx;
      fish.v4.y = fish.v4.y + _vy;
      /*
      print("addforce vx:");
      print(vx);
      print(" vy:");
      println(vy);
      */
    }
  }

  public void draw()
  {    
    Iterator iter = fishes.iterator();
    while (iter.hasNext ())
    {
      Fish fish = (Fish)iter.next();  
      fish.draw();
    }

    if (DEBUG)
    {
      noFill();
      stroke(1, 1, 1);
      ellipse(x, y, SHOALCOLISION, SHOALCOLISION);  
      text("SHOAL", x+50, y+50);
      text("x: ", x+50, y+50+15);
      text(x, x+50+15, y+50+15);
      text("y: ", x+50, y+50+30);
      text(y, x+50+15, y+50+30);      
    }
    return;
  }
}

class ShoalSystem
{
  ArrayList shoals;
  ArrayList avoidEllipseObject;
  //construct
  ShoalSystem()
  {
    shoals = new ArrayList();
    avoidEllipseObject = new ArrayList();
  }

  public Shoal addShoal(
  float _R, float _G, float _B, 
  int _x, int _y, 
  int _number, float speed)
  {
    Shoal shoal = new Shoal();

    Fish[] fishes = new Fish[_number];
    float angle = TWO_PI / _number;

    for (int i = 1; i <= _number; i++)
    {
      float addx = cos(angle * i);
      float addy = sin(angle * i);

      Fish fishtemp = new Fish(
      width / 2 + addx * 50 + _x, 
      height / 2 + addy * 50 + _y, 
      random(- speed, speed) * addx, 
      random(- speed, speed) * addy, 
      i - 1, 
      _R, _G, _B, speed
        );
      shoal.add(fishtemp);
    }

    shoals.add(shoal);
    return shoal;
  }

  public void addEllipseObject(int x, int y, int R)
  {
    EllipseObject obj = new EllipseObject(x, y, R);
    avoidEllipseObject.add(obj);
  }

  public void clearAvoidEllipseObject()
  {
    avoidEllipseObject.clear();
  }

  public void Update()
  { 
    Iterator iter_i = shoals.iterator();  
    while (iter_i.hasNext ())
    {
      Shoal shoal_i = (Shoal)iter_i.next();

      shoal_i.clearVector();

      Iterator iter_j = shoals.iterator();

      while (iter_j.hasNext ())
      {
        Shoal shoal_j = (Shoal)iter_j.next();

        if (shoal_i != shoal_j)
        {
          PVector force = new PVector();
          force = AvoidEllipse(shoal_i.x, shoal_i.y, shoal_j.x, shoal_j.y, SHOALCOLISION);
          shoal_i.addForce(force.x, force.y);
        }
      }

      Iterator iter_f = (shoal_i.fishes).iterator();
      while (iter_f.hasNext ())
      {
        Fish fish = (Fish)iter_f.next();
        
        Iterator iter_o = avoidEllipseObject.iterator();
        PVector force = new PVector();
        while (iter_o.hasNext ())
        {
          EllipseObject obj = (EllipseObject) iter_o.next();
          force = AvoidEllipse(fish.x, fish.y, obj.x, obj.y, obj.R);
          fish.v5.x = fish.v5.x + force.x;
          fish.v5.y = fish.v5.y + force.y;
        }       
      }
      shoal_i.update();

    }

    return;
  }


  public PVector AvoidEllipse(float _x, float _y, float _xb, float _yb, float R)
  {
    PVector v = new PVector();
    v.x = 0;
    v.y = 0;

    if (sq(_x - _xb) + sq(_y - _yb) <sq(R))
    {
      if ((_x-_xb)!=0)
      {
        v.x = (_x-_xb)/abs(_x-_xb);
      }
      if ((_y-_yb)!=0)
      {
        v.y = (_y-_yb)/abs(_y-_yb);
      }
    }
    return v;
  }

  public void Draw()
  {
    Iterator iter_shoal = shoals.iterator();
    while (iter_shoal.hasNext ())
    {
      Shoal shoal = (Shoal)iter_shoal.next();
      shoal.draw();
    }
    Iterator iter = avoidEllipseObject.iterator();
    while (iter.hasNext ())
    {
      ((EllipseObject)iter.next()).draw();
    }
  }
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--stop-color=#cccccc", "openpool" });
  }
}
