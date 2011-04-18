package org.eagereyes.presidents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

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
 * Simple storage class that keeps the data. All dates are stored as number of days
 * from January 1, 1730.
 */
public class President {

	public String name;
	public float born;
	public float died;
	public float ascension;
	public float endOfTerm;
	
	public String sBorn;
	public String sDied;
	public String sAscension;
	public String sEndOfTerm;
	
	boolean alive;
	boolean inOffice = false;
	
	// indicate if this is the first or second term of Grover Cleveland
	boolean groverCleveland1 = false;
	boolean groverCleveland2 = false;
	President otherCleveland; // point to other Cleveland object, only has a value if one of the above booleans is true
	
	public int x;
	public int y;
	public int length;
	public int ascensionOffset;
	public int officeLength;
	public boolean candidate = false;
	
	// for animation
	public float oldX;
	public float newX;

	public void paint(Graphics g, int height, boolean highlight) {
		g.setColor(Color.LIGHT_GRAY);
		if (groverCleveland1) {
			g.fillRect(x, y, ascensionOffset, height);
			int lineX = x+ascensionOffset+officeLength;
			g.drawLine(lineX+1, y+height/2, lineX+3, y+height/2);
			g.drawLine(lineX+3, y+height/2, lineX+3, y+height);
		} else if (groverCleveland2) {
			g.fillRect(x+ascensionOffset, y, length-ascensionOffset, height);
			int lineX = x+ascensionOffset;
			g.drawLine(lineX-4, y-1, lineX-4, y+height/2);
			g.drawLine(lineX-4, y+height/2, lineX-2, y+height/2);
		} else
			g.fillRect(x, y, length, height);
		if (alive) {
			Polygon triangle = new Polygon();
			triangle.addPoint(x+length, y);
			triangle.addPoint(x+length+5, y+height/2);
			triangle.addPoint(x+length, y+height);
			g.fillPolygon(triangle);
		}
		g.setColor(Color.DARK_GRAY);
		if (!candidate) {
			g.fillRect(x+ascensionOffset, y, officeLength, height);
			if (inOffice) {
				Polygon triangle = new Polygon();
				triangle.addPoint(x+ascensionOffset+officeLength, y);
				triangle.addPoint(x+ascensionOffset+officeLength+5, y+height/2);
				triangle.addPoint(x+ascensionOffset+officeLength, y+height);
				g.fillPolygon(triangle);
			}
		} else
			g.drawLine(x+ascensionOffset, y, x+ascensionOffset, y+height-1);

		if (highlight) {
			if (alive) {
				Polygon p = new Polygon();
				p.addPoint(x-1, y-1);
				p.addPoint(x+length+1, y-1);
				p.addPoint(x+length+6, y+height/2);
				p.addPoint(x+length+1, y+height);
				p.addPoint(x-1, y+height);
				g.drawPolygon(p);
			} else {
				if (groverCleveland1) {
					g.drawRect(x-1, y-1, ascensionOffset+officeLength+1, height+1);
					g.drawRect(x+otherCleveland.ascensionOffset-1, otherCleveland.y-1, otherCleveland.length-otherCleveland.ascensionOffset+1, height+1);
				} else if (groverCleveland2) {
					g.drawRect(x+ascensionOffset-1, y-1, length-ascensionOffset+1, height+1);
					g.drawRect(x-1, otherCleveland.y-1, otherCleveland.ascensionOffset+otherCleveland.officeLength+1, height+1);
				} else
					g.drawRect(x-1, y-1, length+1, height+1);
			}
		}
	}

	public void interpolate(float timeStep) {
		x = Math.round(oldX+(newX-oldX)*timeStep);
	}
	
}
