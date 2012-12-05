float SPEED = 5;  
float R = 4;       
int NUMBER = 50;   // number of fishes
int BALLNUM = 8;
int RINGNUM = 15;

Fish[] redfishes = new Fish[NUMBER];
Fish[] bluefishes = new Fish[NUMBER];
Fish[] greenfishes = new Fish[NUMBER];

float r1 = 1.0;   //param: shoal gathering
float r2 = 0.1; //  param: avoid conflict with other fishes in shoal 
float r3 = 0.5; // param: along with other fish in shoal
float r4 = 10;   //  param: avoid balls

int redaddx = -100; //initial position of the red shoal
int redaddy = 0;

int blueaddx = 100; //initial position of the blue shoal
int blueaddy = 0;

int greenaddx = 0;
int greenaddy = 0;

int CENTER_PULL_FACTOR = 300;
int  DIST_THRESHOLD = 30;

int ballcount = 0;
Ball[] balls = new Ball[BALLNUM];

int wband = 80;
int hband = 80;

PImage img;


void setup() 
{
  //498*2
  //282*2
  size(996, 564);
  frameRate(30);
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

void draw()
{
  background(0, 0, 0);
  //image(img, 0, 0, 498*2, 282*2);

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

    fish = (Fish) bluefishes[i];
    fish.check();
    fish.move();
    fish.draw();
    
    fish = (Fish) greenfishes[i];
    fish.check();
    fish.move();
    fish.draw();
  }
  for (int i = 0 ; i < ballcount ; i++)
  {
    Ball ball = (Ball) balls[i];
    ball.draw();
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

  void move() {
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

  void draw() 
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
  void clearVector()
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
  void check() 
  {
    rule1();
    rule2();
    rule3();
    rule4();
  }

  //calc param1
  void rule1()
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
  void rule2()
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
  void rule3()
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
  void rule4()
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

  void draw()
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
  float GetPotential(float _x, float _y)
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

