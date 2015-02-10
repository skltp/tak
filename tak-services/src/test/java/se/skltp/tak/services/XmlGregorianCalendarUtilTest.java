/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tak.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Ignore;
import org.junit.Test;

public class XmlGregorianCalendarUtilTest {

	@Ignore("only for performance testing of issue NTP-142")
	@Test
	public void performanceTestFromDate_issue_NTP_142() {
		// 2015-02-06: ca 20.000 records in TAK prod for
		// hamtaAllaAnropsBehorigheterResponse makes up for 2x the number of
		// calls to fromDate()
		int numberOfIterations = 2 * 20000;

		Date testDate = new Date();
		long timeStart = System.currentTimeMillis();
		for (int i = 0; i < numberOfIterations; i++) {
			XmlGregorianCalendarUtil.fromDate(testDate);
		}
		long timeStop = System.currentTimeMillis();

		System.out.println("Execution time [ms]: " + (timeStop - timeStart));
	}

	@Test
	public void testFromDate() {
		Date testDate = new Date();

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil
				.fromDate(testDate);

		assertEquals(testDate.getTime(), xmlDate.toGregorianCalendar()
				.getTime().getTime());
	}

	@Test
	public void testGetNowAsXMLGregorianCalendar() {
		long tsBefore = System.currentTimeMillis();

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil
				.getNowAsXMLGregorianCalendar();

		long tsAfter = System.currentTimeMillis();

		assertTrue(xmlDate.toGregorianCalendar().getTime().getTime() >= tsBefore);
		assertTrue(xmlDate.toGregorianCalendar().getTime().getTime() <= tsAfter);
	}
}
