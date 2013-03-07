class Lotus{
  float x;
  float y;
  float vx;
  float vy;
  Boolean collision;
  int collisionFrame;
  Boolean play;
  int sound;
  int collisthld = 100;
  float rotateAngle;
  float vR;
  float colx;
  float coly;
  int n;//hasunumber
  
  Lotus(int num){
    x = random(200,1080);
    y = random(250,550);
    play = false;
    collision = false;
    sound = int(random(12));
    vx = 0;
    vy = 0;
    colx = 0;
    coly = 0;
    n = num;
    rotateAngle = random(360);
    vR = 0;
  }
  
  void update(){
    
    if(collision == true){
      x += vx;
      y += vy;
      rotateAngle += vR;
      vx *= 0.9;
      vy *= 0.9;
      vR *= 0.97;
      
      if(x<200 || x>1080){
        vx *= -2;
      }
      if(y<250 || y >550){
        vy *= -2;
      }
      
      if(frameCount - collisionFrame > 60){
        collision = false;
      }
    }
    else{
      for(Ball ball : op.balls){
        if(dist(ball.x,ball.y,x,y) < collisthld){
          collision = true;
          collisionFrame=frameCount;
          colx = ball.x;
          coly = ball.y;
          vR = random(-0.2,0.2);
        }
        
        for(int i = 1;i < hasunum ;i++){
          if(dist(hasu[(n+i)%(hasunum)].x,hasu[(n+i)%(hasunum)].y,x,y)<collisthld){
            collision = true;
            collisionFrame=frameCount;
            colx = hasu[(n+i)%(hasunum)].x;
            coly = hasu[(n+i)%(hasunum)].y;
            vR = random(-0.2,0.2);
          }
        }
        
      }
      if(collision == true){
        playSound();
       // float rad = random(360);
        vx = (x-colx)/10;
        vy = (y-coly)/10;
        if(abs(vx)+abs(vy)<2){
          vx *= 20;
          vy *= 20;
        }
      }
    }
  }
  
  void display(){
    tint(255,220);
    float yure = 1+0.01*(sin((float(frameCount+80*n))/float(4+(n/4))));
    if(yure < 0.9905){
      disturb(int(x),int(y));
    }
    pushMatrix();
    translate(x,y);
    rotate(rotateAngle);
    image(
    hasunohana[n],
    -(hasunohana[n].width/2),// *(1+yure/2),
    -(hasunohana[n].height/2),// *(1+yure/2),
    (hasunohana[n].width)*(yure),
    (hasunohana[n].height)*(yure)
    );
    popMatrix();
  }
  
  void kagedisplay(){
    pushMatrix();
    translate(x+30,y+30);
    rotate(rotateAngle);
    image(
    hasunokage[n],
    -(hasunohana[n].width/2),// *(1+yure/2),
    -(hasunohana[n].height/2),// *(1+yure/2),
    (hasunohana[n].width),
    (hasunohana[n].height)
    );
    popMatrix();
  }
  
  void playSound(){
    println("LOTUSsound " + n);
    OscMessage myMessage = new OscMessage("/Lotus");
    myMessage.add(sound);
    oscP5.send(myMessage, myRemoteLocation); 
    sound = int(random(12));
  }  
}

