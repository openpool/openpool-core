import processing.core.*; 
import processing.xml.*; 

import msafluid.*; 
import processing.opengl.*; 
import javax.media.opengl.*; 
import java.nio.FloatBuffer; 
import com.sun.opengl.util.*; 

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






final float FLUID_WIDTH = 80;

float invWidth, invHeight;    // inverse of screen dimensions
float aspectRatio, aspectRatio2;

MSAFluidSolver2D fluidSolver;

ParticleSystem particleSystem;

PImage imgFluid;
boolean drawFluid = true;

float SPEED = 5;  
float R = 4;       
int NUMBER = 20;   // number of fishes
int BALLNUM = 8;
int RINGNUM = 15;

Fish[] redfishes = new Fish[NUMBER];
Fish[] bluefishes = new Fish[NUMBER];
Fish[] greenfishes = new Fish[NUMBER];

float r1 = 1.0f;   //param: shoal gathering
float r2 = 0.1f; //  param: avoid conflict with other fishes in shoal 
float r3 = 0.5f; // param: along with other fish in shoal
float r4 = 0.1f;   //  param: avoid balls

int redaddx = -100; //initial position of the red shoal
int redaddy = 0;

int blueaddx = 100; //initial position of the blue shoal
int blueaddy = 0;

int greenaddx = 0;
int greenaddy = 0;

int CENTER_PULL_FACTOR = 300;
int DIST_THRESHOLD = 30;

int ballcount = 0;
Ball[] balls = new Ball[BALLNUM];

int wband = 80;
int hband = 80;

PImage img;


public void setup() 
{
  //498*2
  //282*2
  size(996, 564,OPENGL);
  hint( ENABLE_OPENGL_4X_SMOOTH );    // Turn on 4X antialiasing
  //frameRate(30);

  invWidth = 1.0f/width;
  invHeight = 1.0f/height;
  aspectRatio = width * invHeight;
  aspectRatio2 = aspectRatio * aspectRatio;

    // create fluid and set options
    fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * height/width));
    fluidSolver.enableRGB(true).setFadeSpeed(0.05f).setDeltaT(0.5f).setVisc(0.001f);

    // create image to hold fluid picture
    imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);

    // create particle system
    particleSystem = new ParticleSystem();

    // init TUIO
    initTUIO();

  stroke(255, 255, 255);

  img = loadImage("billiards.jpg");
  tint(255,127);

  float angle = TWO_PI / NUMBER;
  for (int i = 1; i <= NUMBER; i++)
  {
    float addx = cos(angle * i);
    float addy = sin(angle * i);

    redfishes[i-1] = new Fish(
    width / 2 + addx * 50 + redaddx, 
    height / 2 + addy * 50 + redaddy, 
    random(- SPEED, SPEED) * addx, 
    random(- SPEED, SPEED) * addy, 
    i - 1, 
    255, 0, 0, 
    redfishes);

    bluefishes[i-1] = new Fish(
    width / 2 + addx * 50 + blueaddx, 
    height / 2 + addy * 50 + blueaddy, 
    random(- SPEED, SPEED) * addx, 
    random(- SPEED, SPEED) * addy, 
    i - 1, 
    0, 0, 255, 
    bluefishes);
        
    greenfishes[i-1] = new Fish(
    width / 2 + addx * 50 + greenaddx, 
    height / 2 + addy * 50 + greenaddy, 
    random(- SPEED, SPEED) * addx, 
    random(- SPEED, SPEED) * addy, 
    i - 1, 
    0, 255, 0, 
    greenfishes);
  }

  ballcount = BALLNUM;
  balls[0] = new Ball(200, 200, 25,25);
  balls[1] = new Ball(400, 400, 25,25);
  balls[2] = new Ball(200, 400, 50,50);
  balls[3] = new Ball(400, 200, 50,50);
  balls[4] = new Ball(600, 200, 25,50);
  balls[5] = new Ball(600, 400, 50,50);
  balls[6] = new Ball(800, 200, 50,50);
  balls[7] = new Ball(800, 400, 25,50);
}

//main draw
public void draw()
{
  updateTUIO();
  fluidSolver.update();
    
  background(0, 0, 0);
  //image(img, 0, 0, 498*2, 282*2);
  
    if(drawFluid) {
        for(int i=0; i<fluidSolver.getNumCells(); i++) {
            int d = 2;
            imgFluid.pixels[i] = color(fluidSolver.r[i] * d, fluidSolver.g[i] * d, fluidSolver.b[i] * d);
        }  
        imgFluid.updatePixels();//  fastblur(imgFluid, 2);
        image(imgFluid, 0, 0, width, height);
    } 

    particleSystem.updateAndDraw();

  for (int i = 0; i < NUMBER; i++)
  {
    redfishes[i].clearVector();
    bluefishes[i].clearVector();
    greenfishes[i].clearVector();
  }

  for (int i = 0; i < NUMBER; i++) 
  {
    Fish fish = (Fish) redfishes[i];
    fish.check();
    fish.move();
    fish.draw();
    addForce(fish.x/width,fish.y/height,fish.vx/5000,fish.vy/5000);
    
    fish = (Fish) bluefishes[i];
    fish.check();
    fish.move();
    fish.draw();
     addForce(fish.x/width,fish.y/height,fish.vx/5000,fish.vy/5000);
    
    fish = (Fish) greenfishes[i];
    fish.check();
    fish.move();
    fish.draw();
     addForce(fish.x/width,fish.y/height,fish.vx/5000,fish.vy/5000);
  }
  for (int i = 0 ; i < ballcount ; i++)
  {
    Ball ball = (Ball) balls[i];
    ball.draw();
  }
}

public void mouseMoved() {
    float mouseNormX = mouseX * invWidth;
    float mouseNormY = mouseY * invHeight;
    float mouseVelX = (mouseX - pmouseX) * invWidth;
    float mouseVelY = (mouseY - pmouseY) * invHeight;

    addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
}

public void mousePressed() {
    drawFluid ^= true;
}

public void keyPressed() {
    switch(key) {
    case 'r': 
        renderUsingVA ^= true; 
        println("renderUsingVA: " + renderUsingVA);
        break;
    }
    println(frameRate);
}



// add force and dye to fluid, and create particles
public void addForce(float x, float y, float dx, float dy) {
    float speed = dx * dx  + dy * dy * aspectRatio2;    // balance the x and y components of speed with the screen aspect ratio

    if(speed > 0) {
        if(x<0) x = 0; 
        else if(x>1) x = 1;
        if(y<0) y = 0; 
        else if(y>1) y = 1;

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
class Fish
{
  float r, g, b;
  float x, y;   //location of fish
  float vx, vy; //speed of fish
  PVector v1 = new PVector();  //for param1
  PVector v2 = new PVector();  //for param2
  PVector v3 = new PVector();  //for param3
  PVector v4 = new PVector();  //for param4

  int id;
  Fish[] others;

  //constructor
  Fish(float _x, float _y, 
  float _vx, float _vy, 
  int _id, 
  float _r, float _g, float _b, 
  Fish[] _others) 
  {
    x = _x;
    y = _y;
    vx = _vx;
    vy = _vy;
    id = _id;
    others = _others;
    r=_r;
    g=_g;
    b=_b;
  }

  public void move() {
    vx += r1 * v1.x + r2 * v2.x + r3 * v3.x + r4 * v4.x;
    vy += r1 * v1.y + r2 * v2.y + r3 * v3.y + r4 * v4.y;

    //max speed check 
    float vVector = sqrt(vx * vx + vy * vy);
    if (vVector > SPEED) 
    {
      vx = (vx / vVector) * SPEED;
      vy = (vy / vVector) * SPEED;
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
  }

  //check rules
  public void check() 
  {
    rule1();
    rule2();
    rule3();
    rule4();
  }

  //calc param1
  public void rule1()
  {
    for (int i = 0; i < NUMBER; i++) 
    {
      Fish otherfish = (Fish) others[i];
      if (this != otherfish) 
      {
        v1.x += otherfish.x;
        v1.y += otherfish.y;
      } // end if
    } // end for

    v1.x /= (NUMBER - 1);
    v1.y /= (NUMBER - 1);

    v1.x = (v1.x - x) / CENTER_PULL_FACTOR;
    v1.y = (v1.y - y) / CENTER_PULL_FACTOR;
  }//end rule1


  // calc param2
  public void rule2()
  {
    for (int i = 0; i < NUMBER; i++) 
    {
      Fish otherfish = (Fish) others[i];
      if (this != otherfish)
      {
        if (dist(x, y, otherfish.x, otherfish.y) < DIST_THRESHOLD)
        {
          v2.x -= otherfish.x - x;
          v2.y -= otherfish.y - y;
        } // end if
      } // end if
    }
  }// end rule2

  // calc param3
  public void rule3()
  {
    for (int i = 0; i < NUMBER; i++)
    {
      Fish otherfish = (Fish) others[i];
      if (this != otherfish)
      {
        v3.x += otherfish.vx;
        v3.y += otherfish.vy;
      } // end if
    } // end for

    v3.x /= (NUMBER - 1);
    v3.y /= (NUMBER - 1);

    v3.x = (v3.x - vx)/2;
    v3.y = (v3.y - vy)/2;
  }// end rule3

  //calc param4
  public void rule4()
  {
    for ( int j = 0;j < BALLNUM ; j++)
    {
      if (sq(balls[j].x - x) + sq(balls[j].y - y) < sq(balls[j].R))
      {
        v4.x += (x - balls[j].x);
        v4.y += (y - balls[j].y);
      }
    }
  }
}

class Ball
{
  int x;
  int y;
  int R;
  int realr;
  int timecount;

  //Construct
  Ball(int _x, int _y,int _realr, int _R)
  {
    x = _x;
    y = _y;
    realr = _realr;
    R = _R;
    timecount = 0;
  }

  public void draw()
  {
    if (timecount >= 2*R)
    {
      timecount -= 2*R;
    }

    timecount++;

    stroke(255, 255, 255);
    fill(255, 255, 255);
    ellipse(x, y,2*realr,2*realr);// R*2, R*2);
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

class Field
{
  //construct
  Field()
  {
    ;
  }
  public float GetPotential(float _x, float _y)
  {
    float xsq = 0;
    float ysq = 0;
    float p = 0;

    for (int i = 0 ; i < BALLNUM ; i++)
    {
      xsq = sq(balls[i].x - _x); 
      ysq = sq(balls[i].y - _y);

      if (sq(balls[i].R) < xsq + ysq)
        p += xsq;   
      p += ysq;
    }
    return p;
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


    public void update() {
        // only update if particle is visible
        if(alpha == 0) return;

        // read fluid info and add to velocity
        int fluidIndex = fluidSolver.getIndexForNormalizedPosition(x * invWidth, y * invHeight);
        vx = fluidSolver.u[fluidIndex] * width * mass * FLUID_FORCE + vx * MOMENTUM;
        vy = fluidSolver.v[fluidIndex] * height * mass * FLUID_FORCE + vy * MOMENTUM;

        // update position
        x += vx;
        y += vy;

        // bounce of edges
        if(x<0) {
            x = 0;
            vx *= -1;
        }
        else if(x > width) {
            x = width;
            vx *= -1;
        }

        if(y<0) {
            y = 0;
            vy *= -1;
        }
        else if(y > height) {
            y = height;
            vy *= -1;
        }

        // hackish way to make particles glitter when the slow down a lot
        if(vx * vx + vy * vy < 1) {
            vx = random(-1, 1);
            vy = random(-1, 1);
        }

        // fade out a bit (and kill if alpha == 0);
        alpha *= 0.999f;
        if(alpha < 0.01f) alpha = 0;

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
        for(int i=0; i<maxParticles; i++) particles[i] = new Particle();
        curIndex = 0;

        posArray = BufferUtil.newFloatBuffer(maxParticles * 2 * 2);// 2 coordinates per point, 2 points per particle (current and previous)
        colArray = BufferUtil.newFloatBuffer(maxParticles * 3 * 2);
    }


    public void updateAndDraw(){
        PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;         // processings opengl graphics object
        GL gl = pgl.beginGL();                // JOGL's GL object

        gl.glEnable( GL.GL_BLEND );             // enable blending
        if(!drawFluid) fadeToColor(gl, 0, 0, 0, 0.05f);

        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);  // additive blending (ignore alpha)
        gl.glEnable(GL.GL_LINE_SMOOTH);        // make points round
        gl.glLineWidth(1);


        if(renderUsingVA) {
            for(int i=0; i<maxParticles; i++) {
                if(particles[i].alpha > 0) {
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
            for(int i=0; i<maxParticles; i++) {
                if(particles[i].alpha > 0) {
                    particles[i].update();
                    particles[i].drawOldSchool(gl);    // use oldschool renderng
                }
            }
            gl.glEnd();
        }

        gl.glDisable(GL.GL_BLEND);
        pgl.endGL();
    }


    public void addParticles(float x, float y, int count ){
        for(int i=0; i<count; i++) addParticle(x + random(-15, 15), y + random(-15, 15));
    }


    public void addParticle(float x, float y) {
        particles[curIndex].init(x, y);
        curIndex++;
        if(curIndex >= maxParticles) curIndex = 0;
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

public void initTUIO() {
    // implemented in the TUIO example
}


public void updateTUIO() {
    // implemented in the TUIO example
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--stop-color=#cccccc", "openpool" });
  }
}
