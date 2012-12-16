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

  void move()
  {
    vx += r1 * v1.x + r2 * v2.x + r3 * v3.x + r4 * v4.x + r5 * v5.x;
    vy += r1 * v1.y + r2 * v2.y + r3 * v3.y + r4 * v4.y + r5 * v5.y;
    /*
    print(" r1*v1.x:");    print(r1*v1.x);
     print(" r1*v1.y:");    print(r1*v1.y); 
     print(" r2*v2.x:");    print(r2*v2.x);
     print(" r2*v2.y:");    print(r2*v2.y); 
     print(" r3*v3.x:");    print(r3*v3.x);
     print(" r3*v3.y:");    print(r3*v3.y); 

    print(" r4*v4.x:");      print(r5*v5.x);
    print(" r4*v4.y:");      println(r5*v5.y);
        print(" r5*v5.x:");      print(r5*v5.x);
    print(" r5*v5.y:");      println(r5*v5.y);
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
    v5.x = 0;
    v5.y = 0;
    //println("clearvector");
  }
}

