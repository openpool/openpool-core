/***********************************************************************
 
 Copyright (c) takashyx 2012. ( http://takashyx.com )
 * All rights reserved.
 
 This work is licensed under a Creative Commons Attribution-ShareAlike
 3.0 Unported License.(http://creativecommons.org/licenses/by-sa/3.0/)
 
 For the Particle System MSAFluid:
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of MSA Visuals nor the names of its contributors 
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * ***********************************************************************/
import java.awt.*;
import SimpleOpenNI.*;
import openpool.*;

//OpenGL
import processing.opengl.*;
import javax.media.opengl.*;

//img for ball
PImage img;

boolean SETTING = false;

OpenPool op;

void setup() 
{
  size(1280, 800, OPENGL);
  // Turn on 4X antialiasing
  hint( ENABLE_OPENGL_4X_SMOOTH );

  op = new OpenPool(this, "straight1.oni");
  img = loadImage("sample.png");
}

void draw()
{

  op.updateBalls();

  if (SETTING)
  {
    ;
  }
  else
  {
    background(0);
    for (Ball ball : op.balls) 
    {
      image(img, ball.x-img.width/2, ball.y-img.height/2, img.width, img.height);
    }
  }
}

void keyPressed()
{
  switch(key)
  {
  case 's':
    SETTING^= true;
    op.setConfigMode(SETTING);
    break;
  }
}

