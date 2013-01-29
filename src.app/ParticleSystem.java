/***********************************************************************
 
 Copyright (c) 2008, 2009, Memo Akten, www.memo.tv
 *** The Mega Super Awesome Visuals Company ***
 * All rights reserved.
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

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

import com.jogamp.common.nio.Buffers;
//import com.sun.opengl.util.*;

class ParticleSystem {
	private OpenPoolExampleWithFluids ope;

    FloatBuffer posArray;
    FloatBuffer colArray;

    final static int maxParticles = 5000;
    int curIndex;

    Particle[] particles;

    ParticleSystem(OpenPoolExampleWithFluids ope) {
        particles = new Particle[maxParticles];
        for(int i=0; i<maxParticles; i++) particles[i] = new Particle(ope);
        curIndex = 0;

        posArray =  Buffers.newDirectFloatBuffer(maxParticles * 2 * 2);// 2 coordinates per point, 2 points per particle (current and previous)
        colArray =  Buffers.newDirectFloatBuffer(maxParticles * 3 * 2);
//        posArray =  BufferUtils.newFloatBuffer(maxParticles * 2 * 2);// 2 coordinates per point, 2 points per particle (current and previous)
//        colArray =  BufferUtils.newFloatBuffer(maxParticles * 3 * 2);
    }


    void updateAndDraw(){
        PGraphicsOpenGL pgl = (PGraphicsOpenGL) ope.g;         // processings opengl graphics object
        ((PGraphicsOpenGL)ope.g).beginPGL();
        GL2 gl = PGL.gl.getGL2();               // JOGL's GL object

        gl.glEnable( GL.GL_BLEND );             // enable blending
        if(!ope.drawFluid) ope.fadeToColor(gl, 0, 0, 0, 0.05f);

        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);  // additive blending (ignore alpha)
        gl.glEnable(GL.GL_LINE_SMOOTH);        // make points round
        gl.glLineWidth(1);

        if(ope.renderUsingVA) {
            for(int i=0; i<maxParticles; i++) {
                if(particles[i].alpha > 0) {
                    particles[i].update();
                    particles[i].updateVertexArrays(i, posArray, colArray);
                }
            }    
            gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glVertexPointer(2, GL2.GL_FLOAT, 0, posArray);

            gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
            gl.glColorPointer(3, GL2.GL_FLOAT, 0, colArray);

            gl.glDrawArrays(GL2.GL_LINES, 0, maxParticles * 2);
        } 
        else {
            gl.glBegin(GL2.GL_LINES);               // start drawing points
            for(int i=0; i<maxParticles; i++) {
                if(particles[i].alpha > 0) {
                    particles[i].update();
                    particles[i].drawOldSchool(gl);    // use oldschool renderng
                }
            }
            gl.glEnd();
        }

        gl.glDisable(GL.GL_BLEND);
        pgl.endPGL();
    }


    void addParticles(float x, float y, int count ){
        for(int i=0; i<count; i++) {
        	addParticle(
        			x + (float) (Math.random() * 30 - 15),
        			y + (float) (Math.random() * 30 - 15));
        }
    }


    void addParticle(float x, float y) {
        particles[curIndex].init(x, y);
        curIndex++;
        if(curIndex >= maxParticles) curIndex = 0;
    }

}








