class ShoalSystem
{
  ArrayList shoals;
  //construct
  ShoalSystem()
  {
    shoals = new ArrayList();
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

  void Update()
  { 
    ListIterator iter_i = shoals.listIterator();
    while (iter_i.hasNext ())
    {
      Shoal shoal_i = (Shoal)iter_i.next();
      Iterator iter_j = shoals.iterator();
      while (iter_j.hasNext ())
      {
        Shoal shoal_j = (Shoal)iter_j.next();
        if (shoal_i != shoal_j)
        {
          shoal_i.addForce((shoal_j.x-shoal_i.x), (shoal_j.y-shoal_i.y));
        }
      }
      shoal_i.update();
      iter_i.set(shoal_i);
    }
    //  rule4();       
    return;
  }

  void Draw()
  {
    Iterator iter = shoals.iterator();
    while (iter.hasNext ())
    {
      Shoal shoal = (Shoal)iter.next();
      shoal.draw();
    }
  }
}

