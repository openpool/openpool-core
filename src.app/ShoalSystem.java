import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PVector;

class ShoalSystem
{
	private OpenPoolExampleWithFluids ope;
	
  /**
   * Shoal objects.
   */
  ArrayList<Shoal> shoals;

  /**
   * Ellipse objects to be avoided.
   */
  ArrayList<EllipseObject> avoidEllipseObject;
  
  /**
   * Temporary vector for force calculation.
   */
  PVector force = new PVector();

  /**
   * Default constructor.
   */
  ShoalSystem(OpenPoolExampleWithFluids ope)
  {
	  this.ope = ope;
    shoals = new ArrayList<Shoal>();
    avoidEllipseObject = new ArrayList<EllipseObject>();
  }

  /**
   * Create a new shoal with the specified parameters.
   */
  Shoal addShoal(
    float r, float g, float b, 
    int x, int y,
    int numFishes, float speed)
  {
    Shoal shoal = new Shoal(ope);
    float angle = (float) (Math.PI * 2  / numFishes);
    for (int i = 0; i < numFishes; i++)
    {
      float dx = (float) Math.cos(angle * i);
      float dy = (float) Math.sin(angle * i);

      Fish fish = new Fish(
    		  ope,
        ope.width / 2 + x + dx * 50,
        ope.height / 2 + y + dy * 50,
        (float) (Math.random() - 0.5) * 2 * speed * dx,
        (float) (Math.random() - 0.5) * 2 * speed * dy,
        i,
        r, g, b, speed);

      shoal.add(fish);
    }

    shoals.add(shoal);
    return shoal;
  }

  void addEllipseObject(float x, float y, int r)
  {
    EllipseObject obj = new EllipseObject(ope, x, y, r);
    avoidEllipseObject.add(obj);
  }

  void clearEllipseObjects()
  {
    avoidEllipseObject.clear();
  }

  void update()
  { 
    Iterator<Shoal> iter_i = shoals.iterator();  
    while (iter_i.hasNext())
    {
      Shoal shoal_i = iter_i.next();

      shoal_i.clearVector();

      Iterator<Shoal> iter_j = shoals.iterator();
      while (iter_j.hasNext())
      {
        Shoal shoal_j = iter_j.next();

        if (shoal_i != shoal_j)
        {
          avoidEllipse(shoal_i.x, shoal_i.y, shoal_j.x, shoal_j.y, ope.SHOALCOLLISION_SQ, force);
          shoal_i.addForce(force.x, force.y);
        }
      }

      Iterator<Fish> iter_f = shoal_i.fishes.iterator();
      while (iter_f.hasNext())
      {
        Fish fish = iter_f.next();
        
        Iterator<EllipseObject> iter_o = avoidEllipseObject.iterator();
        while (iter_o.hasNext())
        {
          EllipseObject obj = iter_o.next();
          avoidEllipse(fish.x, fish.y, obj.x, obj.y, PApplet.sq(obj.r), force);
          fish.v5.x = fish.v5.x + force.x;
          fish.v5.y = fish.v5.y + force.y;
        }
      }
      shoal_i.update();
    }
    return;
  }


  void avoidEllipse(float _x, float _y, float _xb, float _yb, float distanceSq, PVector v)
  {
    v.x = 0;
    v.y = 0;

    if (PApplet.sq(_x - _xb) + PApplet.sq(_y - _yb) < distanceSq)
    {
      if ((_x-_xb)!=0)
      {
        v.x = (_x-_xb)/PApplet.abs(_x-_xb);
      }
      if ((_y-_yb)!=0)
      {
        v.y = (_y-_yb)/PApplet.abs(_y-_yb);
      }
    }
  }

  void draw()
  {
    Iterator<Shoal> iter_shoal = shoals.iterator();
    while (iter_shoal.hasNext())
    {
      Shoal shoal = iter_shoal.next();
      shoal.draw();
    }
    
    Iterator<EllipseObject> iter = avoidEllipseObject.iterator();
    while (iter.hasNext())
    {
      EllipseObject eo = iter.next();
      eo.draw();
    }
  }
}

