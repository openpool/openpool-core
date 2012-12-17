class EllipseObject
{
  int x;
  int y;
  int R;
  int ID;

  EllipseObject(int _x, int _y, int _R)
  {
    x = _x;
    y = _y;
    R = _R;
    //ID = hogefuga;
  }

  void draw()
  {
    if (DEBUG)
    {
      ellipse(x, y, R*2, R*2);
      text("object", x, y);
      text("x:", x, y+15);
      text(x, x+30, y+15);
      text("y:", x, y+30);
      text(y, x+30, y+30);
      text("R:", x, y+45);
      text(R, x+30, y+45);
    }
  }
}

