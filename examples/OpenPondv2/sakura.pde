class Sakura{
  boolean play;//
  int pnum;//poclet number
  int pframe;//framecount of pocket time
  int framenum;//length of animation
  int currentframe;//
  
  Sakura(int number){
    pnum = number +1;
    pframe = 0;
    currentframe = 0;
    play = false;
    if(pnum == 1 || 
       pnum == 3 || 
       pnum == 4 || 
       pnum == 6){
      framenum = numFrames_edge;
    }
    else{
      framenum = numFrames_center;
    }
  }
    
  void command(){
    play = true;
    currentframe = 0;
    OscMessage myMessage = new OscMessage("/Sakura");
    myMessage.add(1);
    oscP5.send(myMessage, myRemoteLocation); 
    println("SakuraSound");
  }  
    
  void display(){
    tint(255,255);
    if(play == true){
      if(pnum == 1){
        image(animation_edge[currentframe], op.getPoolCorner(0).x, op.getPoolCorner(0).y);
      }
      else if(pnum == 2){
        image(animation_center[currentframe], 0, op.getPoolCorner(0).y);
      }
      else if(pnum == 3){
        pushMatrix();
        scale(-1, 1);
        image(animation_edge[currentframe],-op.getPoolCorner(1).x,op.getPoolCorner(0).y); //画像を表示
        popMatrix();
      }
      else if(pnum == 4){
        pushMatrix();
        scale(-1, -1);
        image(animation_edge[currentframe],-op.getPoolCorner(1).x,-op.getPoolCorner(1).y); //画像を表示
        popMatrix();
      }
      else if(pnum == 5){
        pushMatrix();
        scale(1, -1);
        image(animation_center[currentframe],0,-op.getPoolCorner(1).y); //画像を表示
        popMatrix();
      }
      else if(pnum == 6){
        pushMatrix();
        scale(1, -1);
        image(animation_edge[currentframe],op.getPoolCorner(0).x,-op.getPoolCorner(1).y); //画像を表示
        popMatrix();
      }
      currentframe ++;
    }
    
    if(currentframe == framenum-1){
      play = false;
      currentframe = 0;
    }
  }
}


