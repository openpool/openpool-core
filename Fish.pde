int CENTER_PULL_FACTOR = 300;

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

  int id;
  Fish[] others;

  //constructor
  Fish(float _x, float _y, 
  float _vx, float _vy, 
  int _id, 
  float _r, float _g, float _b,float _speed, 
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
    speed = _speed;
  }

  void move() {
    vx += r1 * v1.x + r2 * v2.x + r3 * v3.x + r4 * v4.x;
    vy += r1 * v1.y + r2 * v2.y + r3 * v3.y + r4 * v4.y;

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


