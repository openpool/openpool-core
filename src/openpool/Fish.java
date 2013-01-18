package openpool;
import processing.core.PVector;

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

  void move(OpenPool op)
  {
    vx += op.r1 * v1.x + op.r2 * v2.x + op.r3 * v3.x + op.r4 * v4.x + op.r5 * v5.x;
    vy += op.r1 * v1.y + op.r2 * v2.y + op.r3 * v3.y + op.r4 * v4.y + op.r5 * v5.y;

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
    float vVector = (float) Math.sqrt(vx * vx + vy * vy);
    if (vVector > speed) 
    {
      vx = (vx / vVector) * speed;
      vy = (vy / vVector) * speed;
    }

    x += vx;
    y += vy;

    if (x - op.R <= 0 + op.wband) 
    {
      x = op.R + op.wband;
      vx *= -1;
    }
    if (x + op.R >= (op.pa.width - op.wband)) 
    {
      x = op.pa.width - op.wband - op.R;
      vx *= -1;
    }

    if (y - op.R <= 0 + op.hband) 
    {
      y = op.R + op.hband;
      vy *= -1;
    }
    if (y + op.R >= op.pa.height - op.hband) 
    {
      y = op.pa.height - op.R - op.hband;
      vy *= -1;
    }
  }

  void draw(OpenPool op) 
  {
    float dx = 0;
    float dy = 0;
    float rtemp;

    op.pa.noStroke();
    op.pa.fill(r, g, b, 100);

    for (int i = 0 ; i < 5 ; i++)
    {
      dx = -vx * 5 * i / 10;
      dy = -vy * 5 * i / 10;
      rtemp = op.R * (5-i) / 10;
      op.pa.ellipse(x-dx, y-dy, rtemp * 2, rtemp * 2);
    }
    for (int i = 0 ; i < 10 ; i++ )
    {
      op.pa.noStroke();
      op.pa.fill(r, g, b, 100);//255*((i)/10));
      dx = -vx * 5 * i / 10;
      dy = -vy * 5 * i / 10;
      rtemp = op.R * (10-i) / 10;
      op.pa.ellipse(x+dx, y+dy, rtemp * 2, rtemp * 2);
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

