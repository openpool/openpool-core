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
import processing.opengl.PGraphicsOpenGL;

import javax.media.opengl.*;

public class OpenPoolExampleWithFluids extends PApplet {
	private static final long serialVersionUID = -7348561580190403869L;

	/**
	 * Fish config.
	 */
	private static final int
			NUM_FISHES = 20,
			FISH_SPEED = 5;

	/**
	 * Fluid config.
	 */
	private static final float FLUID_WIDTH = 120;

	/**
	 * Particle config.
	 */
	private static final int NUM_PARTICLES = 2000;

	/**
	 * Initial position of the red shoal.
	 */
	private static final int RX = -100, RY = 0;

	/**
	 * Initial position of the blue shoal.
	 */
	private static final int BX = 100, BY = 0;

	/**
	 * Initial position of the green shoal.
	 */
	private static final int GX = 0, GY = 0;

	/**
	 * OpenPool library.
	 */
	OpenPool op;

	/**
	 * Fluid solver.
	 */
	MSAFluidSolver2D fluidSolver;
	
	/**
	 * Particle system.
	 */
	private ParticleSystem particleSystem;

	/**
	 * Shoal system.
	 */
	private ShoalSystem shoalSystem;

	/**
	 * PImage for billiard pool background.
	 */
	private PImage backgroundImage;

	/**
	 * PImage for fluid image processing.
	 */
	private PImage fluidImage;

	/**
	 * Debug mode?
	 */
	boolean isDebugMode = false;
	
	/**
	 * Draw fluid?
	 */
	private boolean drawFluid = true;

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

	public void setup() {
		String userDir = System.getProperty("user.dir");
		String binPath = File.separatorChar + "bin";
		if (userDir.endsWith(binPath)) {
			userDir = userDir.substring(0, userDir.length() - binPath.length());
		}
		op = new OpenPool(this, userDir + "\\recordings\\straight1.oni");
		
		frameRate(15);

		invWidth = 1.0f / width;
		invHeight = 1.0f / height;
		aspectRatio = width * invHeight;

		// Create fluid solver and set options.
		fluidSolver = new MSAFluidSolver2D(
				(int) (FLUID_WIDTH),
				(int) (FLUID_WIDTH / aspectRatio));
		fluidSolver.enableRGB(true).setFadeSpeed(0.05f);
		fluidSolver.setDeltaT(0.5f).setVisc(0.001f);

		// Create PImage to hold fluid picture.
		fluidImage = createImage(fluidSolver.getWidth(), fluidSolver.getHeight(), RGB);

		backgroundImage = loadImage(userDir + "\\recordings\\billiards.jpg");
		// tint(255, 127);

		shoalSystem = new ShoalSystem(this);
		shoalSystem.addShoal(1, 0.75f, 0.75f, RX, RY, NUM_FISHES, FISH_SPEED);
		shoalSystem.addShoal(0.75f, 1, 0.75f, GX, GY, NUM_FISHES, FISH_SPEED);
		shoalSystem.addShoal(0.75f, 0.75f, 1, BX, BY, NUM_FISHES, FISH_SPEED);
		shoalSystem.addShoal(1, 1, 1, GX, GY, NUM_FISHES, FISH_SPEED);
		shoalSystem.addShoal(1, 1, 1, GX, GY, NUM_FISHES, FISH_SPEED);
		
		particleSystem = new ParticleSystem(this, NUM_PARTICLES);
	}

	public void draw() {
		if (op.isConfigMode()) {
			return;
		}

		// Update ball positions and other related information.
		shoalSystem.clearEllipseObjects();
		op.updateBalls();
		for (Ball ball : op.balls) {
			shoalSystem.addObstacle(ball.x, ball.y, 30);
		}
		shoalSystem.update();
		fluidSolver.update();

		// Draw background.
		background(0);
		if (isDebugMode) {
			image(backgroundImage, 0, 0, 498 * 2, 282 * 2);
		}

		// Draw fluids.
		if (drawFluid) {
			for (int i = 0; i < fluidSolver.getNumCells(); i++) {
				fluidImage.pixels[i] = color(fluidSolver.r[i], fluidSolver.g[i],
						fluidSolver.b[i]);
			}
			fluidImage.updatePixels();
			image(fluidImage, 0, 0, width, height);
		}

		// Draw balls.
		for (Ball ball : op.balls) {
			ellipse(ball.x, ball.y, 50, 50);
		}

		// Draw shoals.
		shoalSystem.draw();

		if (!isDebugMode) {
			PGraphicsOpenGL pgl = (PGraphicsOpenGL) g;
			if (!drawFluid) {
				fadeToColor(pgl, 0, 0, 0, 0.05f);
			}
			particleSystem.updateAndDraw(pgl);
		}
	}

	public void mouseMoved() {
		if (op.isConfigMode()) {
			return;
		}

		float mouseNormX = mouseX * invWidth;
		float mouseNormY = mouseY * invHeight;
		float mouseVelX = (mouseX - pmouseX) * invWidth;
		float mouseVelY = (mouseY - pmouseY) * invHeight;
		addForceToFluid(mouseNormX, mouseNormY, mouseVelX, mouseVelY);
	}

	public void keyPressed() {
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

		switch (key) {
		case 'r':
			boolean isVertexArrayEnabled = !particleSystem.isVertexArrayEnabled();
			particleSystem.setVertexArrayEnabled(isVertexArrayEnabled);
			println("Render particles with vertex arrays: " + isVertexArrayEnabled);
			break;
		case ' ':
			isDebugMode ^= true;
			drawFluid ^= true;
			if (isDebugMode) {
				println("DEBUG MODE");
			} else {
				println("NORMAL MODE");
			}
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
		float speed = sq(dx) + sq(dy * aspectRatio);

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

	private void fadeToColor(PGraphicsOpenGL pgl, float r, float g, float b, float speed) {
		GL gl = pgl.beginGL();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(r, g, b, speed);
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2f(0, 0);
			gl.glVertex2f(width, 0);
			gl.glVertex2f(width, height);
			gl.glVertex2f(0, height);
		gl.glEnd();
		pgl.endGL();
	}
}
