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

  void UpdateandDraw()
  { 
    for (int i = 0 ; i < shoals.size() ; i++)
    {
      Shoal shoal = (Shoal)shoals.get(i);
      shoal.update(); 
      shoal.draw(); 
    }
       //  rule4();       
    return;
  }
}

