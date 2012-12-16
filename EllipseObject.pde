class EllipseObject
{
  int x;
  int y;
  int R;
  
  EllipseObject(int _x, int _y, int _R)
  {
    x = _x;
    y = _y;
    R = _R;
  }
  
  void draw()
  {
    ellipse(x,y,R*2,R*2);
    text("object",x,y);
    text(x,x,y+15);
    text(y,x,y+30);
    text(R,x,y+45);
  }
}
