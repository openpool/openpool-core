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

  void draw(OpenPool op)
  {
    op.pa.stroke(255, 255, 255);
    op.pa.fill(255, 255, 255);
    op.pa.ellipse(x, y, 2*realr, 2*realr);// R*2, R*2);
    op.pa.noFill();

    for (int i=0;i<RINGNUM;i++)
    {
      int tempring = op.timecount + i*(4*R/RINGNUM);

      if (2*R < tempring && tempring < 4*R)
      {
        //TODO: need improvement for gradation
    	op.pa.stroke(255, 255, 255, 
        255*(6*R-tempring)/(20*R));
        //fill(0, 255, 0);
    	op.pa.ellipse(x, y, tempring, tempring);
        //TODO: change transparency of the ring
      }
    }
  }
}
