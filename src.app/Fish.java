import processing.core.PApplet;
import processing.core.PVector;

class Fish
{
  private OpenPoolExampleWithFluids ope;
  
  float speed;
  float r, g, b;
  
  /**
   * Location of this fish
   */
  float x, y;
  
  /**
   * Speed of this fish
   */
  float vx, vy;

  /**
   * For param 1-4 and p
   */
  PVector v1, v2, v3, v4, v5;

  int id;

  //constructor
  Fish(OpenPoolExampleWithFluids ope, float x, float y, 
    float vx, float vy, 
    int id, 
    float r, float g, float b, 
    float speed)
  {
	this.ope = ope;
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.id = id;

    this.r = r;
    this.g = g;
    this.b = b;
    this.speed = speed;
    
    v1 = new PVector();
    v2 = new PVector();
    v3 = new PVector();
    v4 = new PVector();
    v5 = new PVector();
  }

  void move()
  {
    vx += ope.r1 * v1.x + ope.r2 * v2.x + ope.r3 * v3.x + ope.r4 * v4.x + ope.r5 * v5.x;
    vy += ope.r1 * v1.y + ope.r2 * v2.y + ope.r3 * v3.y + ope.r4 * v4.y + ope.r5 * v5.y;
    
    // Check if the speed is faster than the limit.
    float vVector = PApplet.sqrt(vx * vx + vy * vy);
    if (vVector > speed) 
    {
      vx = (vx / vVector) * speed;
      vy = (vy / vVector) * speed;
    }

    x += vx;
    y += vy;

    // Hit the left edge
    if (x - ope.R <= 0 + ope.wband) 
    {
      x = ope.R + ope.wband;
      vx *= -1;
    }
    
    // Hit the right edge
    if (x + ope.R >= (ope.width - ope.wband)) 
    {
      x = ope.width - ope.wband - ope.R;
      vx *= -1;
    }

    // Hit the upper edge
    if (y - ope.R <= 0 + ope.hband) 
    {
      y = ope.R + ope.hband;
      vy *= -1;
    }
    
    // Hit the bottom edge
    if (y + ope.R >= ope.height - ope.hband) 
    {
      y = ope.height - ope.R - ope.hband;
      vy *= -1;
    }
  }

  void draw() 
  {
    float dx = 0;
    float dy = 0;
    float rtemp;

    ope.noStroke();
    ope.fill(r, g, b, 100);

    for (int i = 0 ; i < 5 ; i++)
    {
      dx = -vx * 5 * i / 10;
      dy = -vy * 5 * i / 10;
      rtemp = ope.R * (5-i) / 10;
      ope.ellipse(x-dx, y-dy, rtemp * 2, rtemp * 2);
    }
    for (int i = 0 ; i < 10 ; i++ )
    {
    	ope.noStroke();
    	ope.fill(r, g, b, 100);//255*((i)/10));
      dx = -vx * 5 * i / 10;
      dy = -vy * 5 * i / 10;
      rtemp = ope.R * (10-i) / 10;
      ope.ellipse(x+dx, y+dy, rtemp * 2, rtemp * 2);
    }
  }

  /**
   * Clear vectors
   */
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
  }
}

