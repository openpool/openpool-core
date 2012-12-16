class Shoal
{
  ArrayList fishes;
  float x, y;
  float vx, vy;
  int CENTER_PULL_FACTOR;
  int DIST_THRESHOLD;

  Shoal()
  {
    fishes = new ArrayList();
    x=0;
    y=0;
    vx=0;
    vy=0;

    CENTER_PULL_FACTOR = 300;
    DIST_THRESHOLD = 30;
    return;
  }

  void add(Fish _fish)
  {
    fishes.add(_fish);
  }


  int size()
  {
    return fishes.size();
  }

  //calc param1
  Fish shoalrules(int i, Fish fish_i)
  {
      for (int j = 0 ; j< fishes.size() ; j++)
      {
        if (i != j) 
        {
          Fish fish_j = (Fish) fishes.get(j);

          //rule1
          fish_i.v1.x = fish_i.v1.x + fish_j.x;
          fish_i.v1.y = fish_i.v1.y + fish_j.y;

          //rule2
          if (dist(fish_i.x, fish_i.y, fish_j.x, fish_j.y) < DIST_THRESHOLD)
          {
            fish_i.v2.x -= (fish_j.x - fish_i.x);
            fish_i.v2.y -= (fish_j.y - fish_i.y);
          }

          //rule3
          
          fish_i.v3.x += fish_j.vx;
          fish_i.v3.y += fish_j.vy;
          
        }
      }    

      //rule1
      fish_i.v1.x = ( fish_i.v1.x / (fishes.size() - 1)); 
      fish_i.v1.y = ( fish_i.v1.y / (fishes.size() - 1));

      fish_i.v1.x = (fish_i.v1.x - fish_i.x) / CENTER_PULL_FACTOR;
      fish_i.v1.y = (fish_i.v1.y - fish_i.y) / CENTER_PULL_FACTOR;

      //rule2 none

      //rule3
      fish_i.v3.x /= (fishes.size() - 1);
      fish_i.v3.y /= (fishes.size() - 1);

      fish_i.v3.x = (fish_i.v3.x - fish_i.vx)/2;
      fish_i.v3.y = (fish_i.v3.y - fish_i.vy)/2;

    return fish_i;
  }//end shoalrules

  //calc param4
  /*void rule4()
   {
   for ( int j = 0;j < BALLNUM ; j++)
   {
   if (sq(balls[j].x - x) + sq(balls[j].y - y) < sq(balls[j].R))
   {
   fish_i.v4.x += (fish_i.x - balls[j].x);
   fish_i.v4.y += (fish_i.y - balls[j].y);
   }
   }
   }
   */

  //check rules
  Fish check(int i, Fish fish_i) 
  {
    return shoalrules(i, fish_i);
    //rule4();
  }

  void update()
  {
    x=0;
    y=0;
    vx=0;
    vy=0;

    for (int i = 0 ; i < fishes.size() ; i++)
    {
      x = x + ((Fish)fishes.get(i)).x;
      y = y + ((Fish)fishes.get(i)).y;
    }
    x = x / fishes.size();
    y = y / fishes.size();

    for (int i = 0 ; i < fishes.size() ; i++)
    {
      Fish fish_i = (Fish)fishes.get(i);
      fish_i.clearVector();
      check(i,fish_i);
      fish_i.move();
      fishes.set(i,fish_i);
      vx = vx + fish_i.vx;
      vy = vy + fish_i.vy;

      addForce(fish_i.x/width, fish_i.y/height, -fish_i.vx/FISHFORCE, -fish_i.vy/FISHFORCE);
    }

    vx = vx / fishes.size();
    vy = vy / fishes.size();
    return;
  }

  void draw()
  {    
    for (int i = 0 ; i < fishes.size(); i++) 
    {
      Fish fish_i = (Fish)fishes.get(i);  
      fish_i.draw();
    }
    return;
  }
}

