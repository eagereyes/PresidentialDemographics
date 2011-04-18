package org.eagereyes.presidents;

import java.applet.AppletContext;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Timer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

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
 * The canvas class that draws the visualization for Presidential Demographics
 * 
 */
@SuppressWarnings("serial")
public class PresidentsCanvas extends JPanel implements MouseListener, MouseMotionListener {

	private static final int TIMELINEHEIGHT = 25;

	private static final int BIRTHALIGNX = 220;

	public static final int PREFERREDWIDTH = 580;

	public static final int PREFERREDHEIGHT = 505;

	private static final String WIKIPEDIAURL = "http://en.wikipedia.org/wiki/";

	private static final String TARGET = "presidentWiki";
	
	private static final float DASHES[] = {2, 2};
	
	private static final Stroke DOTTEDSTROKE = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, DASHES, 0);

	private static final int NAMELABELY = 400;
	
	List<President> presidents;
	
	boolean needsLayout = true;
	
	enum LayoutType {timeline, birth_aligned, office_aligned};
	
	LayoutType currentLayout = LayoutType.timeline;

	private int lineHeight;
	
	President highlighted;

	private AppletContext appletContext;

	private Font smallFont;
	private Font largeFont;

	private float tenYears;

	private Box radioBox;

	private int maxAscensionOffset;

	private Timer timer;
	
	final static float NUMSTEPS = 10;

	private JRadioButton timelineRB;

	private JRadioButton birthRB;

	private JRadioButton officeRB;

	public PresidentsCanvas() {
		super();
		setBackground(Color.WHITE);
		addMouseMotionListener(this);
		addMouseListener(this);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		smallFont = getFont().deriveFont(10f);
		largeFont = getFont().deriveFont(Font.BOLD);
		
		radioBox = new Box(BoxLayout.Y_AXIS);
		radioBox.setBorder(new TitledBorder("Align"));
		radioBox.setBackground(Color.WHITE);
		radioBox.setOpaque(true);
		ButtonGroup bg = new ButtonGroup();
		timelineRB = new JRadioButton("Time line", true);
		timelineRB.setBackground(Color.WHITE);
		timelineRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timelineRB.isSelected()) {
					setLayoutType(LayoutType.timeline);
				}
			}
		});
		bg.add(timelineRB);
		radioBox.add(timelineRB);
		
		birthRB = new JRadioButton("Birth");
		birthRB.setBackground(Color.WHITE);
		birthRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (birthRB.isSelected()) {
					setLayoutType(LayoutType.birth_aligned);
				}
			}
		});
		bg.add(birthRB);
		radioBox.add(birthRB);
		
		officeRB = new JRadioButton("Ascension");
		officeRB.setBackground(Color.WHITE);
		officeRB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (officeRB.isSelected()) {
					setLayoutType(LayoutType.office_aligned);
				}
			}
		});
		bg.add(officeRB);
		radioBox.add(officeRB);
		
		Box b = new Box(BoxLayout.Y_AXIS);
		b.add(radioBox);
		b.add(Box.createVerticalGlue());
		add(Box.createHorizontalGlue());
		add(b);
	}

	public void paint(Graphics g) {
		//super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (presidents == null)
			return;
		
		lineHeight = (getHeight()-25)/(presidents.size()+3);
		if (needsLayout)
			layout(lineHeight);
		
		if (highlighted != null) {
			Graphics2D g2 = (Graphics2D)g;
			Stroke s = g2.getStroke();
			int y = 10;
			g2.setStroke(DOTTEDSTROKE);
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(highlighted.x, y, highlighted.x, getHeight()-30);
			if ((!highlighted.candidate) && (highlighted.died != highlighted.endOfTerm) && (!highlighted.inOffice))
				g.drawLine(highlighted.x+highlighted.length, y, highlighted.x+highlighted.length, getHeight()-30);
			g.setColor(Color.DARK_GRAY);
			g.drawLine(highlighted.x+highlighted.ascensionOffset, y, highlighted.x+highlighted.ascensionOffset, getHeight()-30);
			if ((!highlighted.candidate) && (!highlighted.alive))
				g.drawLine(highlighted.x+highlighted.ascensionOffset+highlighted.officeLength, y, highlighted.x+highlighted.ascensionOffset+highlighted.officeLength, getHeight()-30);
			g2.setStroke(s);
		}
		
		for (President p : presidents)
			p.paint(g, lineHeight/2, p == highlighted);
		
		// Boxes are always transparent, so fill white rectangle below it to keep lines
		// from introding
		g.setColor(Color.WHITE);
		g.fillRect(getWidth()-radioBox.getWidth(), 0, radioBox.getWidth(), radioBox.getHeight());
		
		// fill Box below name label
		g.fillRect(3, NAMELABELY-g.getFontMetrics(largeFont).getAscent(),
				g.getFontMetrics(smallFont).stringWidth("Office: XX/XX/XXXX - XX/XX/XXXX"),
				g.getFontMetrics(largeFont).getHeight()+2*g.getFontMetrics(smallFont).getHeight()+5);
		
		int y = NAMELABELY;
		if (highlighted != null) {
			g.setFont(largeFont);
			g.setColor(Color.DARK_GRAY);
			g.drawString(highlighted.name, 3, y);
			y += g.getFontMetrics().getHeight();

			g.setFont(smallFont);
			//g.setColor(Color.LIGHT_GRAY);
			String s = null;
			if (highlighted.alive)
				s = "Born: "+highlighted.sBorn;
			else
				s = "Lived: "+highlighted.sBorn+" - "+highlighted.sDied;
			g.drawString(s, 3, y);
			y += g.getFontMetrics().getHeight();
			
			if (!highlighted.candidate) {
				s = "Office: "+highlighted.sAscension+" - ";
				if (!highlighted.inOffice)
					s = s.concat(highlighted.sEndOfTerm);
			} else
				s = "";
			g.drawString(s, 3, y);
		}

		drawTimeLine(g);
		
		paintComponents(g);
	}

	private void drawTimeLine(Graphics g) {
		g.setFont(smallFont);
		if (tenYears == 0) {
			tenYears = DataSet.parseDate("1/1/1740");
			tenYears = tenYears/DataSet.maxOffset*(float)(getWidth()-20);
		}
		g.setColor(Color.LIGHT_GRAY);
		g.drawString("EagerEyes.org", 2, getHeight()-g.getFontMetrics().getDescent()-1);
		float x;
		switch(currentLayout) {
		case timeline:
			x = 10;
			for (int year = 1730; year <= 2010; year += 10) {
				if (year % 100 == 0) {
					g.setColor(Color.DARK_GRAY);
					g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT+10, Math.round(x), getHeight()-TIMELINEHEIGHT);
					String yearString = Integer.toString(year);
					int sWidth = g.getFontMetrics().stringWidth(yearString);
					g.drawString(yearString, Math.round(x)-sWidth/2, getHeight()-g.getFontMetrics().getDescent());
				} else {
					g.setColor(Color.LIGHT_GRAY);
					if (year % 50 == 0)
						g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT+8, Math.round(x), getHeight()-TIMELINEHEIGHT);
					else
						g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT+5, Math.round(x), getHeight()-TIMELINEHEIGHT);
				}
				x += tenYears;
			}
			break;
			
		case birth_aligned:
			x = BIRTHALIGNX;
			g.setColor(Color.DARK_GRAY);
			g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+10);
			g.drawLine(Math.round(x+10*tenYears), getHeight()-TIMELINEHEIGHT, Math.round(x+10*tenYears), getHeight()-TIMELINEHEIGHT+10);

			g.drawString("0", BIRTHALIGNX-g.getFontMetrics().stringWidth("0")/2, getHeight()-g.getFontMetrics().getDescent());
			g.drawString("100", BIRTHALIGNX-g.getFontMetrics().stringWidth("100")/2+Math.round(10f*tenYears), getHeight()-g.getFontMetrics().getDescent());

			g.setColor(Color.LIGHT_GRAY);
			x += tenYears;
			for (int i = 1; i < 10; i++) {
				if (i == 5)
					g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+8);
				else
					g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+5);
				x += tenYears;
			}
			break;
			
		case office_aligned:
			x = BIRTHALIGNX+maxAscensionOffset;
			g.setColor(Color.DARK_GRAY);
			g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+10);

			g.drawString("0", BIRTHALIGNX+maxAscensionOffset-g.getFontMetrics().stringWidth("0")/2, getHeight()-g.getFontMetrics().getDescent());
			g.drawString("+40", BIRTHALIGNX+maxAscensionOffset-g.getFontMetrics().stringWidth("+40")/2+Math.round(4f*tenYears), getHeight()-g.getFontMetrics().getDescent());
			g.drawString("-80", BIRTHALIGNX+maxAscensionOffset-g.getFontMetrics().stringWidth("-80")/2-Math.round(8f*tenYears), getHeight()-g.getFontMetrics().getDescent());

			g.setColor(Color.LIGHT_GRAY);
			x += tenYears;
			for (int i = 1; i < 5; i++) {
				g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+5);
				x += tenYears;
			}
			
			x = BIRTHALIGNX+maxAscensionOffset-tenYears;
			for (int i = 1; i < 9; i++) {
				if (i == 5)
					g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+8);
				else
					g.drawLine(Math.round(x), getHeight()-TIMELINEHEIGHT, Math.round(x), getHeight()-TIMELINEHEIGHT+5);
				x -= tenYears;
			}
		}
	}
	
	protected void layout(int height) {
		float width = getWidth()-25;
		int xofs = 10;
		if (currentLayout != LayoutType.timeline)
			xofs = BIRTHALIGNX;
		int y = 10;
		maxAscensionOffset = 0;
		for (President p : presidents) {
			p.oldX = p.newX;
			if (currentLayout == LayoutType.timeline)
				p.newX = xofs+Math.round(p.born/DataSet.maxOffset*width);
			else
				p.newX = xofs;
			p.x = (int)p.newX;
			if (p.candidate) {
				y += height;
				height /= 2;
			}
			p.y = y;
			p.length = Math.round((p.died-p.born)/DataSet.maxOffset*width);
			p.ascensionOffset = Math.round((p.ascension-p.born)/DataSet.maxOffset*width);
			if (p.ascensionOffset > maxAscensionOffset)
				maxAscensionOffset = p.ascensionOffset;
			p.officeLength = Math.round((p.endOfTerm-p.ascension)/DataSet.maxOffset*width);
			if (p.officeLength < 1) { // poor William Harrison's term won't show up otherwise
				p.officeLength = 1;
				p.ascensionOffset -= 1;
			}
			// make sure that rounding errors don't make it appear as if somebody who
			// died in office lived after end of term
			if ((p.endOfTerm == p.died) && (p.ascensionOffset+p.officeLength < p.died))
				p.officeLength++;

			y += height;
		}
		if (currentLayout == LayoutType.office_aligned) {
			for (President p : presidents) {
				p.newX = p.x = xofs+maxAscensionOffset-p.ascensionOffset;
			}
		}
		needsLayout = false;
	}
	
	private void setLayoutType(LayoutType newLayout) {
		if (currentLayout == newLayout)
			return;
		currentLayout = newLayout;
		layout(lineHeight);
		timelineRB.setEnabled(false);
		birthRB.setEnabled(false);
		officeRB.setEnabled(false);
		int period = (int) (1000f / NUMSTEPS);
		timer = new Timer();
		timer.scheduleAtFixedRate(new StepTask(period, this), 0, period);
	}

	public void takeStep(int stepNum) {
		if (stepNum > NUMSTEPS) {
			timer.cancel();
			timer = null;
			timelineRB.setEnabled(true);
			birthRB.setEnabled(true);
			officeRB.setEnabled(true);
		} else {
			float t = pace(stepNum / NUMSTEPS);
			for (President p : presidents)
				p.interpolate(t);
			repaint();
		}
	}
	
	private static float pace(float f) {
		if (f == 0f)
			return 0f;
		else if (f >= 1f)
			return 1f;
		else {
			f = 12f * f - 6f;
			return (1f / (1f + (float) Math.exp(-f)));
		}
	}

	public void setData(List<President> presidentsList) {
		presidents = presidentsList;
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(PREFERREDWIDTH, PREFERREDHEIGHT);
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	public void mouseMoved(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		President oldhighlighted = highlighted;
		highlighted = null;
		for (President p : presidents) {
			if ((my >= p.y) && (my < p.y+lineHeight) && (mx >= p.x) && (mx <= p.x+p.length))
				highlighted = p;
		}
		if (highlighted != oldhighlighted)
			repaint();
	}

	public void setAppletContext(AppletContext context) {
		appletContext = context;
	}
	
	public void mouseClicked(MouseEvent e) {
		if (highlighted != null) {
			String nameCopy = highlighted.name.replaceAll(" ", "_");
			try {
//				System.err.println(WIKIPEDIAURL+nameCopy);
				appletContext.showDocument(new URL(WIKIPEDIAURL+nameCopy), TARGET);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) {	}

	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseDragged(MouseEvent e) { }

}
