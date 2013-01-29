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
import java.io.File;

import msafluid.*;
import openpool.*;

import processing.core.PApplet;
import processing.core.PImage;
import javax.media.opengl.*;

public class OpenPoolExampleWithFluids extends PApplet {
	private static final long serialVersionUID = -7348561580190403869L;

	// Particle fluid config
	float invWidth, invHeight; // inverse of screen dimensions
	float aspectRatio, aspectRatioSq;

	// Fluid and Particle
	MSAFluidSolver2D fluidSolver;
	ParticleSystem particleSystem;

	PImage imgFluid;
	boolean drawFluid = true;
	boolean DEBUG = false;
	final float FLUID_WIDTH = 120;

	// Fish config
	float SPEED = 5;
	float R = 4;
	int FISHNUMBER = 20; // number of fishes
	int FISHFORCE = 2000;

	// Ball config
	int BALLNUM = 8;
	int BALLRINGS = 8;

	// Shoal system
	ShoalSystem shoalSystem;

	float SHOALCOLLISION = 100;
	float SHOALCOLLISION_SQ = PApplet.sq(SHOALCOLLISION);

	float r1 = 1.0f; // param: shoal gathering
	float r2 = 0.1f; // param: avoid conflict with other fishes in shoal
	float r3 = 0.5f; // param: along with other fish in shoal
	float r4 = 1; // param: avoid other shoal
	float r5 = 100; // param: avoid balls

	int redaddx = -100; // initial position of the red shoal
	int redaddy = 0;

	int blueaddx = 100; // initial position of the blue shoal
	int blueaddy = 0;

	int greenaddx = 0; // initial position of the green shoal
	int greenaddy = 0;

	// margin for billiard pool edge
	int wband = 80;
	int hband = 80;

	// For particle system
	boolean renderUsingVA = true;

	// img for billiard pool background
	PImage img;

	OpenPool op;

	// dummypool op;

	public void setup() {
		String userDir = System.getProperty("user.dir");
		String binPath = File.separatorChar + "bin";
		if (userDir.endsWith(binPath)) {
			userDir = userDir.substring(0, userDir.length() - binPath.length());
		}
		op = new OpenPool(this, userDir + "\\recordings\\straight1.oni");
		// op = new dummypool();

		invWidth = 1.0f / width;
		invHeight = 1.0f / height;
		aspectRatio = width * invHeight;
		aspectRatioSq = aspectRatio * aspectRatio;

		// Create fluid solver and set options.
		fluidSolver = new MSAFluidSolver2D((int) (FLUID_WIDTH),
				(int) (FLUID_WIDTH * height / width));
		fluidSolver.enableRGB(true).setFadeSpeed(0.05f).setDeltaT(0.5f)
				.setVisc(0.001f);

		// Create PImage to hold fluid picture.
		imgFluid = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(),
				RGB);

		stroke(255, 255, 255);

		img = loadImage(userDir + "\\recordings\\billiards.jpg");
		tint(255, 127);

		shoalSystem = new ShoalSystem(this);
		shoalSystem.addShoal(1, 0.75f, 0.75f, redaddx, redaddy, FISHNUMBER,
				SPEED);
		shoalSystem.addShoal(0.75f, 1, 0.75f, greenaddx, greenaddy, FISHNUMBER,
				SPEED);
		shoalSystem.addShoal(0.75f, 0.75f, 1, blueaddx, blueaddy, FISHNUMBER,
				SPEED);
		shoalSystem.addShoal(1, 1, 1, greenaddx, greenaddy, FISHNUMBER, SPEED);
		shoalSystem.addShoal(1, 1, 1, greenaddx, greenaddy, FISHNUMBER, SPEED);
		
		particleSystem = new ParticleSystem(this);
	}

	public void draw() {
		// Update ball positions and other related information.
		shoalSystem.clearEllipseObjects();
		op.updateBalls();
		for (Ball ball : op.balls) {
			shoalSystem.addEllipseObject(ball.x, ball.y, 30);
		}
		shoalSystem.update();
		fluidSolver.update();

		// Draw background.
		background(0);
		if (DEBUG) {
			image(img, 0, 0, 498 * 2, 282 * 2);
		}

		// Draw fluids.
		if (drawFluid) {
			for (int i = 0; i < fluidSolver.getNumCells(); i++) {
				imgFluid.pixels[i] = color(fluidSolver.r[i], fluidSolver.g[i],
						fluidSolver.b[i]);
			}
			imgFluid.updatePixels();
			image(imgFluid, 0, 0, width, height);
		}

		// Draw balls.
		for (Ball ball : op.balls) {
			ellipse(ball.x, ball.y, 50, 50);
		}

		// Draw shoals.
		shoalSystem.draw();

		if (!DEBUG) {
			particleSystem.updateAndDraw();
		}
	}

	public void mouseMoved() {
		float mouseNormX = mouseX * invWidth;
		float mouseNormY = mouseY * invHeight;
		float mouseVelX = (mouseX - pmouseX) * invWidth;
		float mouseVelY = (mouseY - pmouseY) * invHeight;
		addForceToFluid(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
	}

	void outputStatus() {
		if (DEBUG) {
			println("DEBUG MODE");
		} else {
			println("NORMAL MODE");
		}
	}

	public void keyPressed() {
		switch (key) {
		case 'r':
			renderUsingVA ^= true;
			println("renderUsingVA: " + renderUsingVA);
			break;
		case ' ':
			DEBUG ^= true;
			drawFluid ^= true;
			outputStatus();
			break;
		}
		print("FRAMERATE: ");
		println(frameRate);
	}

	/**
	 * Add force and dye to fluid, and create particles.
	 */
	void addForceToFluid(float x, float y, float dx, float dy) {
		// Balance the x and y components of speed with the screen aspect ratio.
		float speed = sq(dx) + sq(dy) * aspectRatioSq;

		if (speed > 0) {
			x = x < 0 ? 0 : (x > 1 ? 1 : x);
			y = y < 0 ? 0 : (y > 1 ? 1 : y);

			colorMode(HSB, 360, 1, 1);
			float hue = ((x + y) * 180 + frameCount) % 360;
			int drawColor = color(hue, 1, 1);
			colorMode(RGB, 1);

			float colorMult = 5;
			int index = fluidSolver.getIndexForNormalizedPosition(x, y);
			fluidSolver.rOld[index] += red(drawColor) * colorMult;
			fluidSolver.gOld[index] += green(drawColor) * colorMult;
			fluidSolver.bOld[index] += blue(drawColor) * colorMult;

			float velocityMult = 30.0f;
			fluidSolver.uOld[index] += dx * velocityMult;
			fluidSolver.vOld[index] += dy * velocityMult;

			particleSystem.addParticles(x * width, y * height, 10);
		}
	}

	void fadeToColor(GL gl, float r, float g, float b, float speed) {
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(r, g, b, speed);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex2f(0, 0);
		gl.glVertex2f(width, 0);
		gl.glVertex2f(width, height);
		gl.glVertex2f(0, height);
		gl.glEnd();
	}
}
