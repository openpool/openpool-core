class EllipseObject
{
	private OpenPoolExampleWithFluids ope;
  float x;
  float y;
  float r;
  int id;

  EllipseObject(OpenPoolExampleWithFluids ope, float x, float y, int r)
  {
	  this.ope = ope;
    this.x = x;
    this.y = y;
    this.r = r;
    //this.id = hogefuga;
  }

  void draw()
  {
    if (ope.DEBUG)
    {
    	ope.ellipse(x, y, r*2, r*2);
    	ope.text("object", x, y);
    	ope.text("x:", x, y+15);
    	ope.text(x, x+30, y+15);
    	ope.text("y:", x, y+30);
    	ope.text(y, x+30, y+30);
    	ope.text("R:", x, y+45);
    	ope.text(r, x+30, y+45);
    }
  }
}

