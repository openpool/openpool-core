package openpool;

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
import java.util.ArrayList;
import java.util.Iterator;


import SimpleOpenNI.*;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

public class OpenPool {
	PApplet pa;

	// Field
	Field field;

	BackGroundDiff bg;

	BallSystem ballSystem;

	int selected = -1; // true when camera image corner is grabbed with mouse
						// pointer

	// img for billiard pool background
	PImage img;

	public OpenPool(PApplet pa) {
		this.pa = pa;

		// 498*2
		// 282*2
		pa.size(996, 564, PApplet.OPENGL);
		pa.hint(PApplet.ENABLE_OPENGL_4X_SMOOTH); // Turn on 4X antialiasing
		// frameRate(30);

		// backgrounddiff
		SimpleOpenNI kinect1 = new SimpleOpenNI(1, pa);
		SimpleOpenNI kinect2 = new SimpleOpenNI(0, pa);

		/*
		 * if ( kinect1.openFileRecording("straight1.oni") == false) {
		 * println("can't find recorded file1 !!!!"); exit(); } if (
		 * _kinect1.openFileRecording("straight2.oni") == false) {
		 * println("can't find recorded file2 !!!!"); exit(); }
		 */

		// enable depthMap generation
		if (kinect1.enableDepth() == false) {
			throw new IllegalStateException(
					"Can't open the depthMap of cam1, maybe the camera is not connected!");
		}

		// enable depthMap generation
		if (kinect2.enableDepth() == false) {
			throw new IllegalStateException(
					"Can't open the depthMap of cam2, maybe the camera is not connected!");
		}

		kinect1.enableDepth();
		kinect2.enableDepth();

		bg = new BackGroundDiff(kinect1, kinect2);

	}

	// main draw
	/**
	 * Call this from Processing code.
	 */
	public void drawSettings() {
		  //////////////////////////////////////////////////  
		  if ( pa.mousePressed && selected >= 0 )
		  {
		    bg.pos[selected][0] = pa.mouseX;
		    bg.pos[selected][1] = pa.mouseY;
		  }
		  else 
		  {
		    float min_d = 20; 
		    selected = -1;
		    for (int i=0; i<2; i++) {
		      float d = PApplet.dist( pa.mouseX, pa.mouseY, bg.pos[i][0], bg.pos[i][1] );
		      if ( d < min_d ) {
		        min_d = d;
		        selected = i;
		      }
		    }
		  }
		  if ( selected >= 0 ) {
		    pa.ellipse( pa.mouseX, pa.mouseY, 20, 20 );
		  }
	}

	public void mouseMoved() {

	}

	public void keyPressed() {
		switch (pa.key) {
		case 'b':
			bg.rememberBackground();
			break;

		}

	}

}
