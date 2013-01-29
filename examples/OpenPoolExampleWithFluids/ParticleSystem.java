/**
 * Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 * The Mega Super Awesome Visuals Company
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * * Neither the name of MSA Visuals nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import processing.opengl.PGraphicsOpenGL;

class ParticleSystem {

	private boolean isVertexArrayEnabled = true;

	private Particle[] particles;
	private int particleIndex;

	private FloatBuffer positions;
	private FloatBuffer colors;

	ParticleSystem(OpenPoolExampleWithFluids ope, int numParticles) {

		particles = new Particle[numParticles];
		for (int i = 0; i < numParticles; i++) {
			particles[i] = new Particle(ope);
		}
		particleIndex = 0;

		// 2 coordinates per point, 2 points per particle (current and previous)
		positions = BufferUtil.newFloatBuffer(particles.length * 2 * 2);

		// 3 parameters per point, 2 colors per particle (current and previous)
		colors = BufferUtil.newFloatBuffer(particles.length * 3 * 2);
	}

	public boolean isVertexArrayEnabled() {
		return isVertexArrayEnabled;
	}
	
	public void setVertexArrayEnabled(boolean isVertexArrayEnabled) {
		this.isVertexArrayEnabled = isVertexArrayEnabled;
	}

	public void updateAndDraw(PGraphicsOpenGL pgl) {

		// Get OpenGL object.
		GL gl = pgl.beginGL();

		// Enable blending.
		boolean isBlendEnabled = gl.glIsEnabled(GL.GL_BLEND);
		if (!isBlendEnabled) {
			gl.glEnable(GL.GL_BLEND);
		}

		// Additive blending (ignore alpha)
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);

		// Make points round
		boolean isLineSmoothened = gl.glIsEnabled(GL.GL_LINE_SMOOTH);
		if (!isLineSmoothened) {
			gl.glEnable(GL.GL_LINE_SMOOTH);
		}
		gl.glLineWidth(1);
		
		updateAndDraw(gl);

		// Reset settings.
		if (!isBlendEnabled) {
			gl.glDisable(GL.GL_BLEND);
		}
		if (!isLineSmoothened) {
			gl.glDisable(GL.GL_LINE_SMOOTH);
		}
		
		pgl.endGL();
	}
	
	private void updateAndDraw(GL gl) {
		
		// Render particles using vertex arrays.
		if (isVertexArrayEnabled) {
			for (int i = 0; i < particles.length; i ++) {
				Particle particle = particles[i];
				particle.update();
				particle.updateVertexArrays(i, positions, colors);
			}
			gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			gl.glVertexPointer(2, GL.GL_FLOAT, 0, positions);
			gl.glEnableClientState(GL.GL_COLOR_ARRAY);
			gl.glColorPointer(3, GL.GL_FLOAT, 0, colors);
			gl.glDrawArrays(GL.GL_LINES, 0, particles.length * 2);

		// Render particles without vertex arrays.
		} else {
			gl.glBegin(GL.GL_LINES);
			for (Particle particle: particles) {
				particle.update();
				particle.draw(gl);
			}
			gl.glEnd();
		}
	}

	public void addParticles(float x, float y, int count) {
		for (int i = 0; i < count; i++) {
			addParticle(
					x + (float) (Math.random() * 30 - 15),
					y + (float) (Math.random() * 30 - 15));
		}
	}

	private void addParticle(float x, float y) {
		particles[particleIndex ++].initialize(x, y);
		if (particleIndex >= particles.length) {
			particleIndex = 0;
		}
	}
}
