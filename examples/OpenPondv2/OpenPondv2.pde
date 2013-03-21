/*
 Openpool Demo Effect by Azuminium & Mai Ikinaga
 Sound Mio Adachi
 
 Processing Water Simulation
 adapted by: Rodrigo Amaya
 
 oscP5message by andreas schlegel
 example shows how to create osc messages.
 oscP5 website at http://www.sojamo.de/oscP5
*/

//OSC
import oscP5.*;
import netP5.*; 
OscP5 oscP5;
NetAddress myRemoteLocation;
 
 //OpenGL
import processing.opengl.*;
import javax.media.opengl.*;

//OpenPool
import java.awt.*;
import SimpleOpenNI.*;
import openpool.*;
OpenPool op;

//pocket detector
import processing.serial.*;
import openpool.pocket.*;
Serial serial;
OpenpoolBoostPocket obp;

//Background image etc... 
PImage img;
PImage moon;
PImage hasu1;
Moon otsukisama;
int hasunum = 5;
PImage[] hasunohana = new PImage[hasunum];
PImage[] hasunokage = new PImage[hasunum];
Lotus[] hasu = new Lotus[hasunum];

Sakura[] sakura = new Sakura[6];

//pocket animation
int numFrames_edge = 75;
PImage[] animation_edge = new PImage[numFrames_edge];
int numFrames_center = 86;
PImage[] animation_center = new PImage[numFrames_center];

boolean DEBUG = false;

/**
 * Debug mode?
 */
boolean isDebugMode = false;

/**
 * Window metrics-related information.
 * (Inverse of screen dimensions)
 */
float invWidth, invHeight;

/**
 * Window metrics-related information.
 * (Aspect ratio)
 */
private float aspectRatio;

 
int size;
int hwidth,hheight;
int riprad;
 
int ripplemap[];
int ripple[];
int texture[];
 
int oldind,newind, mapind;
 
int i,a,b;
 
void setup(){
  
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,7300);
  
  /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
   * an ip address and a port number. myRemoteLocation is used as parameter in
   * oscP5.send() when sending osc packets to another computer, device, 
   * application. usage see below. for testing purposes the listening port
   * and the port of the remote location address are the same, hence you will
   * send messages back to this sketch.
   */
  myRemoteLocation = new NetAddress("127.0.0.1",7300);
  
  OscMessage myMessage = new OscMessage("/Music");
  myMessage.add(1);
  oscP5.send(myMessage, myRemoteLocation); 
  
  /*           D A N G E R ! ! !
  ///////////////////////////////
  // List all the available serial ports
  println(Serial.list()); 
  try {
    Serial myPort = new Serial(this, Serial.list()[0], 9600);
    // Connect to the pocket detector.
    obp = new OpenpoolBoostPocket(this, serial);
    obp.start();
  }
  // Open the port you are using at the rate you want:
  try {
    Serial myPort = new Serial(this, Serial.list()[0], 9600);
    // Connect to the pocket detector.
    obp = new OpenpoolBoostPocket(this, serial);
    obp.start();
  }
  catch(java.lang.RuntimeException e) {
    println("POCKET DETECTOR ERROR!!!!!!");
  }
  ////////////////////////////////
  */
  
  img = loadImage("mizukusa2.png");
  moon = loadImage("moon3.png");
  
  otsukisama = new Moon();
  
  for(int i =0;i<hasunum;i++){
    hasunohana[i] = loadImage("hasu"+ (i+1) +".1.png");
    hasunokage[i] = loadImage("hasu"+ (i+1) +"_shadow.png");
  }
  
  for(int i = 0;i<hasunum;i++){
    hasu[i] = new Lotus(i);
  }
  
  for(int i =0;i<6;i++){
    sakura[i] = new Sakura(i);
  }
  
  for (int i = 0; i < animation_edge.length; i++) {
    animation_edge[i] = loadImage("edge_" + nf(i, 5) + ".png"); 
    print("*");
  }
  println();
  
  for (int i = 0; i < animation_center.length; i++) {
    animation_center[i] = loadImage("center_" + nf(i, 5) + ".png"); 
    print("*");
  }
  println();
  
  println("Animation loaded");
  
  
  //width = img.width;
  //height = img.height;
  //size(width, height);
  size(1280, 800);
  //hint( ENABLE_OPENGL_4X_SMOOTH );  
  
  //op = new OpenPool(this);
  op = new OpenPool(this, "straight1.oni");
  //op = new DummyPool(this);
  op.loadConfig("config.txt");
  //op.loadConfig("libsetting.conf");

  invWidth = 1.0f / width;
  invHeight = 1.0f / height;
  aspectRatio = width * invHeight;
  
  
  //frameRate(40);
   
  hwidth = width>>1;
  hheight = height>>1;
  riprad=6; //test with 3
   
  size = width * (height+2) * 2;
   
  ripplemap = new int[size];
  ripple = new int[width*height];
  texture = new int[width*height];
   
  oldind = width;
  newind = width * (height+3);
   
  image(img, 0, 0);
  loadPixels();
   
  smooth();
}
 
void draw() {
  
  op.updateBalls();

  if (DEBUG)
  {
    ;
  }
  else
  {
    //background(0);
    tint(200,255);
    image(img, 0, 0);
    //filter(GRAY);
    //filter(BLUR, 3);
    
    //tint(255,150);
    //image(moon,0,0);
    
    for(int i = 0;i<hasunum;i++){
      hasu[i].update();
      hasu[i].kagedisplay();
    }
    
    otsukisama.update();
    otsukisama.display();
   
    loadPixels();
    texture = pixels;
     
    newframe();
       
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = ripple[i];
    }
    
    
    updatePixels();
    for (Ball ball : op.balls){
      disturb(int(ball.x), int(ball.y));
       
      fill(255);
      //ellipse(ball.x,ball.y,20,20);
    }
    /*
    if(frameCount % 60 == 0){
      int ranx = int(random(width));
      int rany = int(random(height));
      disturb(ranx,rany);
      fill(255);
      ellipse(ranx,rany,3,3);
    }
    */
    
    for(int i = 0;i<hasunum;i++){
      hasu[i].display();
    }
    
    for(int i =0; i<6;i++){
      sakura[i].display();
    }
    //println("frame");
    
    /*
    //pocket detector
    if (obp != null) {
      int[] pockets = obp.getPockets();
      for (int i = 0; i < pockets.length; i ++) {
        if (pockets[i] > 0) {
          print(pockets[i]);
          println(" balls fell in the pocket no.");
          println(i + 1);
          pocketForce(i);
        }
      }
    }
    */
    fill(0);
    rect(0,0,width,op.getPoolCorner(0).y);
    rect(0,0,op.getPoolCorner(0).y,height);
    rect(0,op.getPoolCorner(1).y,width,height);
    rect(op.getPoolCorner(1).x,0,width,height);    
  }
}

//Try switching between using the disturb method in mousePressed or mouseMoved
void mousePressed()
{
  //disturb(mouseX, mouseY);
}
 
void mouseMoved()
{
  //disturb(mouseX, mouseY);
}
 
void mouseReleased()
{
}

void keyPressed() {
  /*
  if (key == '\n') {
    op.setConfigMode(!op.isConfigMode());
    if (op.isConfigMode()) {
      println("ENTERING CONFIG MODE");
    } else {
      println("LEAVING CONFIG MODE");
    }
  }
  if (op.isConfigMode()) {
    return;
  }
  */

  switch (key) {
  case 's':
    DEBUG^= true;
    op.setConfigMode(DEBUG);
    break;
    
    
  case ' ':
    isDebugMode ^= true;
    if (isDebugMode) {
      println("DEBUG MODE");
    } else {
      println("NORMAL MODE");
    }
    break;
    
  case '1':
    sakura[0].command();
    break;
    
  case '2':
    sakura[1].command();
    break;
    
  case '3':
    sakura[2].command();
    break;  
    
  case '4':
    sakura[3].command();
    break;  
    
  case '5':
    sakura[4].command();
    break;

  case '6':
    sakura[5].command();
    break; 
    
  case 'e':
    stop();
    break; 
 
  }
  //print("FRAMERATE: ");
  //println(frameRate);
}

void stop() {
  OscMessage myMessage = new OscMessage("/Music");
  myMessage.add(0);
  oscP5.send(myMessage, myRemoteLocation); 
  println("end");
  exit();
}



