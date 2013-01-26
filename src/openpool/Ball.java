package openpool;
class Ball
{
  int x;
  int y;
  int vx;
  int vy;
  
  int R;
  int realr;

  int RINGNUM;

  //Construct
  Ball(int _x, int _y, int _realr, int _R, int _RINGNUM)
  {
    //id=_id;
    x = _x;
    y = _y;
    vx=0;
    vy=0;
    realr = _realr;
    R = _R;
    RINGNUM = _RINGNUM;

  }

}
