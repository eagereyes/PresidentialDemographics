package org.eagereyes.presidents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
 * DataSet - parser and data collection for Presidential Demographics
 */
public class DataSet {

	List<President> presidents = new ArrayList<President>(50);

	private static int referenceDate = 0;

	public static float maxOffset = 0;
	
	President mccain;
	
	President obama;
	
	public DataSet() {
		referenceDate = parseDate("1/1/1730");
		maxOffset = 0; // reset or that first calculation will throw the max off!
	}

	public void parse(String fileName) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)));
		boolean seenGroverCleveland = false;
		President cleveland1 = null;
		String line = reader.readLine().trim();
		while (line != null) {
			if (!line.startsWith("#") && line.length() > 0) {
				String[] fields = line.split(",");
				President p = new President();
				p.name = fields[1];
				p.born = parseDate(p.sBorn = fields[2]);
				p.died = parseDate(p.sDied = fields[3]);
				p.ascension = parseDate(p.sAscension = fields[4]);
				p.endOfTerm = parseDate(p.sEndOfTerm = fields[5]);
				p.alive = fields[3].length() == 0;
				if (p.name.equals("Grover Cleveland")) { // the president with two non-consecutive terms
					if (seenGroverCleveland) {
						p.groverCleveland2 = true;
						p.otherCleveland = cleveland1;
						cleveland1.otherCleveland = p;
					} else {
						p.groverCleveland1 = true;
						cleveland1 = p;
						seenGroverCleveland = true;
					}
				}
				presidents.add(p);
			}
			line = reader.readLine();
			if (line != null)
				line = line.trim();
		}
		presidents.get(presidents.size()-1).inOffice = true;

		mccain = new President();
		mccain.name = "John McCain";
		mccain.born = parseDate(mccain.sBorn = "8/29/1936");
		mccain.died = parseDate("");
		mccain.ascension = parseDate("1/20/2009");
		mccain.alive = true;
		mccain.candidate = true;
		presidents.add(mccain);
		
		obama = new President();
		obama.name = "Barack Obama";
		obama.born = parseDate(obama.sBorn = "8/4/1961");
		obama.died = parseDate("");
		obama.ascension = parseDate("1/20/2009");
		obama.alive = true;
		obama.candidate = true;
		presidents.add(obama);
	}

	/**
	 * Parse a date and return it as the number of days since January 1, 1730.
	 * 
	 * Java's date handling is really annoying. There doesn't seem to be any way
	 * to use standard means to parse a date before 1970 into a Calendar object,
	 * or then to do anything with it (like calculate the number of days
	 * difference).
	 * 
	 * @param s
	 * @return
	 */
	public static int parseDate(String s) {
		int month = 0, day = 0, year = 0;
		if (s.length() == 0) {
			Calendar now = GregorianCalendar.getInstance();
			year = now.get(Calendar.YEAR);
			month = now.get(Calendar.MONTH) + 1;
			day = now.get(Calendar.DAY_OF_MONTH);
		} else {
			String mdy[] = s.split("/");
			month = Integer.parseInt(mdy[0]);
			day = Integer.parseInt(mdy[1]);
			year = Integer.parseInt(mdy[2]);
		}

		if (month > 2)
			month++;
		else {
			year--;
			month += 13;
		}

		double julian = (java.lang.Math.floor(365.25 * year)
				+ java.lang.Math.floor(30.6001 * month) + day + 1720995.0);
		// additional changes because all dates are after October 15, 1582
		int ja = (int) (0.01 * year);
		julian += 2 - ja + (0.25 * ja);
		int offset = ((int) julian) - referenceDate;

		if (offset > maxOffset)
			maxOffset = offset;

		return offset;
	}
}
