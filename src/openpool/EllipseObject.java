package openpool;
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

  void draw(OpenPool op)
  {
    if (OpenPool.DEBUG)
    {
      op.pa.ellipse(x, y, R*2, R*2);
      op.pa.text("object", x, y);
      op.pa.text("x:", x, y+15);
      op.pa.text(x, x+30, y+15);
      op.pa.text("y:", x, y+30);
      op.pa.text(y, x+30, y+30);
      op.pa.text("R:", x, y+45);
      op.pa.text(R, x+30, y+45);
    }
  }
}

