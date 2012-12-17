class Field
{
  ArrayList AreaPoints;

  Field(PVector p1, PVector p2, PVector p3, PVector p4)
  {
    AreaPoints = new ArrayList();
    AreaPoints.add(p1);
    AreaPoints.add(p2);
    AreaPoints.add(p3);
    AreaPoints.add(p4);
  }
  void Draw()
  {
    Iterator iter = AreaPoints.iterator();
    PVector v_start = (PVector) iter.next();
    PVector v1 = v_start;
    while (iter.hasNext ())
    {
      PVector v2 = (PVector)iter.next();
      line(v1.x,v1.y,v2.x,v2.y);
      v1=v2;
    }
    line(v1.x,v1.y,v_start.x,v_start.y);
  }
}

