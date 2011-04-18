package org.eagereyes.presidents;

import java.util.TimerTask;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\
 * Copyright (c) 2008, Robert Kosara
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of the program nor the names of its contributors
 *      may be used to endorse or promote products derived from this software
 *      without specific prior written permission.
 *      
 * THIS SOFTWARE IS PROVIDED BY ITS AUTHORS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/**
 * Auxiliary class that connects the timer with the PresidentsCanvas, part of
 * Presidential Demographics
 */
class StepTask extends TimerTask {

	private static final int MAX_TARDINESS = 10;

	private int stepNum = 0;

	private long startTime;

	private long periodLength;

	private PresidentsCanvas canvas;

	public StepTask(int period, PresidentsCanvas pCanvas) {
		startTime = System.currentTimeMillis();
		periodLength = period;
		canvas = pCanvas;
	}

	@Override
	public void run() {
		long tardiness = System.currentTimeMillis() - scheduledExecutionTime();
		if ((tardiness < MAX_TARDINESS) || (stepNum == PresidentsCanvas.NUMSTEPS-1)) {
			canvas.takeStep(stepNum);
		}
		// else
		// System.err.println("Skipping step "+stepNum+", tardiness =
		// "+tardiness);
		stepNum++;
	}

	@Override
	public long scheduledExecutionTime() {
		return startTime + (long) stepNum * periodLength;
	}
}
