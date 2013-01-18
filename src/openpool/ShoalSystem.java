package openpool;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PVector;

class ShoalSystem
{
  ArrayList<Shoal> shoals;
  ArrayList<EllipseObject> avoidEllipseObject;
  //construct
  ShoalSystem(OpenPool op)
  {
    shoals = new ArrayList<Shoal>();
    avoidEllipseObject = new ArrayList<EllipseObject>();
  }

  Shoal addShoal(
  OpenPool op,
  float _R, float _G, float _B, 
  int _x, int _y, 
  int _number, float speed)
  {
    Shoal shoal = new Shoal();

    float angle = (float) (Math.PI * 2 / _number);

    for (int i = 1; i <= _number; i++)
    {
      float addx = (float) Math.cos(angle * i);
      float addy = (float) Math.sin(angle * i);

      Fish fishtemp = new Fish(
      op.pa.width / 2 + addx * 50 + _x, 
      op.pa.height / 2 + addy * 50 + _y, 
      (float) (Math.random() - 0.5) * speed * addx, 
      (float) (Math.random() - 0.5) * speed * addy, 
      i - 1, 
      _R, _G, _B, speed
        );
      shoal.add(fishtemp);
    }

    shoals.add(shoal);
    return shoal;
  }

  void addEllipseObject(int x, int y, int R)
  {
    EllipseObject obj = new EllipseObject(x, y, R);
    avoidEllipseObject.add(obj);
  }

  void clearAvoidEllipseObject()
  {
    avoidEllipseObject.clear();
  }

  void Update(OpenPool op)
  { 
    Iterator<Shoal> iter_i = shoals.iterator();  
    while (iter_i.hasNext ())
    {
      Shoal shoal_i = (Shoal)iter_i.next();

      shoal_i.clearVector();

      Iterator<Shoal> iter_j = shoals.iterator();

      while (iter_j.hasNext ())
      {
        Shoal shoal_j = iter_j.next();

        if (shoal_i != shoal_j)
        {
          PVector force = new PVector();
          force = AvoidEllipse(shoal_i.x, shoal_i.y, shoal_j.x, shoal_j.y, op.SHOALCOLISION);
          shoal_i.addForce(force.x, force.y);
        }
      }

      Iterator<Fish> iter_f = (shoal_i.fishes).iterator();
      while (iter_f.hasNext ())
      {
        Fish fish = (Fish)iter_f.next();
        
        Iterator<EllipseObject> iter_o = avoidEllipseObject.iterator();
        PVector force = new PVector();
        while (iter_o.hasNext ())
        {
          EllipseObject obj = (EllipseObject) iter_o.next();
          force = AvoidEllipse(fish.x, fish.y, obj.x, obj.y, obj.R);
          fish.v5.x = fish.v5.x + force.x;
          fish.v5.y = fish.v5.y + force.y;
        }       
      }
      shoal_i.update(op);

    }

    return;
  }


  PVector AvoidEllipse(float _x, float _y, float _xb, float _yb, float R)
  {
    PVector v = new PVector();
    v.x = 0;
    v.y = 0;

    if (sq(_x - _xb) + sq(_y - _yb) <sq(R))
    {
      if ((_x-_xb)!=0)
      {
        v.x = (_x-_xb)/Math.abs(_x-_xb);
      }
      if ((_y-_yb)!=0)
      {
        v.y = (_y-_yb)/Math.abs(_y-_yb);
      }
    }
    return v;
  }

  void Draw(OpenPool op)
  {
    Iterator<Shoal> iter_shoal = shoals.iterator();
    while (iter_shoal.hasNext ())
    {
      Shoal shoal = iter_shoal.next();
      shoal.draw(op);
    }
    
    Iterator<EllipseObject> iter = avoidEllipseObject.iterator();
    while (iter.hasNext ())
    {
      EllipseObject EO = (EllipseObject)iter.next();
      EO.draw(op);
    }
  }

  private float sq(float a) { return a*a; }
}

