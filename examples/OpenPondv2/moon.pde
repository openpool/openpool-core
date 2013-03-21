class Moon{
  float x;
  float y;
  int tranceparency;
  Boolean collision;
  int collisionFrame;
  Boolean play;
  int sound;
  int collisthld = 80;
  
  Moon(){
    x = 640;
    y = 400;
    play = false;
    collision = false;
    sound = int(random(14));
    tranceparency = 200;
  }
  
  void update(){
    
    if(collision == true){
      tranceparency -= 10;
      if(frameCount - collisionFrame > 20){
        collision = false;
        x = random(200,width-200);
        y = random(250,height-250);
      }
    }
    
    else{
      if(tranceparency < 200){
        tranceparency += 10;
      }
      else{
        tranceparency = 200;
        for(Ball ball : op.balls){
          if(dist(ball.x,ball.y,x,y) < collisthld){
            collision = true;
            collisionFrame=frameCount;
          }
        }
      }
      if(collision == true){
        playSound();
      }
    }
  }
  
  void display(){
    tint(255,tranceparency);
    image(moon,x-(moon.width/2),y-(moon.height/2));
  }
  
  void playSound(){
    println("MOONsound");
    OscMessage myMessage = new OscMessage("/Moon");
    myMessage.add(sound);
    oscP5.send(myMessage, myRemoteLocation); 
    sound = int(random(14));
  }
}
