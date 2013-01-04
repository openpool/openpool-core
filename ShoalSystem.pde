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

  Shoal addShoal(
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

  void addEllipseObject(int x, int y, int R)
  {
    EllipseObject obj = new EllipseObject(x, y, R);
    avoidEllipseObject.add(obj);
  }

  void clearAvoidEllipseObject()
  {
    avoidEllipseObject.clear();
  }

  void Update()
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


  PVector AvoidEllipse(float _x, float _y, float _xb, float _yb, float R)
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

  void Draw()
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
      EllipseObject EO = (EllipseObject)iter.next();
      EO.draw();
    }
  }
}

