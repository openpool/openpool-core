import msafluid.*;

import processing.opengl.*;
import javax.media.opengl.*;

final float FLUID_WIDTH = 100;

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

float r1 = 1.0;   //param: shoal gathering
float r2 = 0.1; //  param: avoid conflict with other fishes in shoal 
float r3 = 0.5; // param: along with other fish in shoal
float r4 = 0.1;   //  param: avoid balls

int redaddx = -100; //initial position of the red shoal
int redaddy = 0;

int blueaddx = 100; //initial position of the blue shoal
int blueaddy = 0;

int greenaddx = 0;
int greenaddy = 0;



int DIST_THRESHOLD = 30;

int ballcount = 0;
Ball[] balls = new Ball[BALLNUM];

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

  invWidth = 1.0f/width;
  invHeight = 1.0f/height;
  aspectRatio = width * invHeight;
  aspectRatio2 = aspectRatio * aspectRatio;

  // create fluid and set options
  fluidSolver = new MSAFluidSolver2D((int)(FLUID_WIDTH), (int)(FLUID_WIDTH * height/width));
  fluidSolver.enableRGB(true).setFadeSpeed(0.05).setDeltaT(0.5).setVisc(0.001);

  // create image to hold fluid picture
  imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);

  // create particle system
  particleSystem = new ParticleSystem();

  // init TUIO
  initTUIO();

  stroke(255, 255, 255);

  img = loadImage("billiards.jpg");
  tint(255, 127);

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
    1, 0, 0, SPEED,
    redfishes);

    bluefishes[i-1] = new Fish(
    width / 2 + addx * 50 + blueaddx, 
    height / 2 + addy * 50 + blueaddy, 
    random(- SPEED, SPEED) * addx, 
    random(- SPEED, SPEED) * addy, 
    i - 1, 
    0, 0, 1, SPEED,
    bluefishes);

    greenfishes[i-1] = new Fish(
    width / 2 + addx * 50 + greenaddx, 
    height / 2 + addy * 50 + greenaddy, 
    random(- SPEED, SPEED) * addx, 
    random(- SPEED, SPEED) * addy, 
    i - 1, 
    0, 1, 0, SPEED,
    greenfishes);
  }

  ballcount = BALLNUM;
  balls[0] = new Ball(200, 200, 25, 25);
  balls[1] = new Ball(400, 400, 25, 25);
  balls[2] = new Ball(200, 400, 50, 50);
  balls[3] = new Ball(400, 200, 50, 50);
  balls[4] = new Ball(600, 200, 25, 50);
  balls[5] = new Ball(600, 400, 50, 50);
  balls[6] = new Ball(800, 200, 50, 50);
  balls[7] = new Ball(800, 400, 25, 50);
}

//main draw
void draw()
{
  updateTUIO();
  fluidSolver.update();

  background(0, 0, 0);
  image(img, 0, 0, 498*2, 282*2);

  if (drawFluid) {
    for (int i=0; i<fluidSolver.getNumCells(); i++) {
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
    addForce(fish.x/width, fish.y/height, -fish.vx/5000, -fish.vy/5000);

    fish = (Fish) bluefishes[i];
    fish.check();
    fish.move();
    fish.draw();
    addForce(fish.x/width, fish.y/height, -fish.vx/5000, -fish.vy/5000);

    fish = (Fish) greenfishes[i];
    fish.check();
    fish.move();
    fish.draw();
    addForce(fish.x/width, fish.y/height, -fish.vx/5000, -fish.vy/5000);
  }
  for (int i = 0 ; i < ballcount ; i++)
  {
    Ball ball = (Ball) balls[i];
    ball.draw();
  }
}

void mouseMoved() {
  float mouseNormX = mouseX * invWidth;
  float mouseNormY = mouseY * invHeight;
  float mouseVelX = (mouseX - pmouseX) * invWidth;
  float mouseVelY = (mouseY - pmouseY) * invHeight;

  addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
}

void mousePressed() {
  drawFluid ^= true;
}

void keyPressed() {
  switch(key) {
  case 'r': 
    renderUsingVA ^= true; 
    println("renderUsingVA: " + renderUsingVA);
    break;
  }
  println(frameRate);
}



// add force and dye to fluid, and create particles
void addForce(float x, float y, float dx, float dy) {
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

