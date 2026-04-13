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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class XmlGregorianCalendarUtilTest {

	/**
	 * The old implementation included time-part info; the new one strips it.
	 * Comparing raw milliseconds is therefore no longer meaningful.
	 */
	@Disabled("fromDate() now returns date-only (time fields = FIELD_UNDEFINED); millisecond comparison is not valid for the new implementation")
	@Test
	public void testFromDate() {
		Date testDate = new Date();

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil
				.fromDate(testDate);

		assertEquals(testDate.getTime(), xmlDate.toGregorianCalendar()
				.getTime().getTime());
	}

	// -----------------------------------------------------------------------
	// Tests for the new date-only implementation of fromDate()
	// -----------------------------------------------------------------------

	/** Year, month and day must be preserved exactly. */
	@Test
	public void testFromDate_preservesYearMonthDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(2009, Calendar.MARCH, 9, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(cal.getTime());

		assertEquals(2009, xmlDate.getYear());
		assertEquals(3,    xmlDate.getMonth()); // 1-based; not 0-based like Calendar
		assertEquals(9,    xmlDate.getDay());
	}

	/** The result must carry NO time information (no T23:00:00.000 in the XML output). */
	@Test
	public void testFromDate_hasNoTimePart() {
		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(new Date());

		assertEquals(DatatypeConstants.FIELD_UNDEFINED, xmlDate.getHour());
		assertEquals(DatatypeConstants.FIELD_UNDEFINED, xmlDate.getMinute());
		assertEquals(DatatypeConstants.FIELD_UNDEFINED, xmlDate.getSecond());
		assertEquals(DatatypeConstants.FIELD_UNDEFINED, xmlDate.getMillisecond());
		assertEquals(DatatypeConstants.FIELD_UNDEFINED, xmlDate.getTimezone());
	}

	/**
	 * Hibernate returns java.sql.Date for DATE columns.
	 * Verify that the day is not shifted back by one when converting.
	 */
	@Test
	public void testFromDate_sqlDate_doesNotShiftDay() {
		java.sql.Date sqlDate = java.sql.Date.valueOf("2025-09-10");

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(sqlDate);

		assertEquals(2025, xmlDate.getYear());
		assertEquals(9,    xmlDate.getMonth());
		assertEquals(10,   xmlDate.getDay());
	}

	/**
	 * A Date value late in the evening (e.g. 23:30) must still map to the
	 * SAME calendar day – not be bumped forward to the next day.
	 */
	@Test
	public void testFromDate_eveningTime_doesNotShiftToNextDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(2026, Calendar.MAY, 1, 23, 30, 0);
		cal.set(Calendar.MILLISECOND, 0);

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(cal.getTime());

		assertEquals(2026, xmlDate.getYear());
		assertEquals(5,    xmlDate.getMonth());
		assertEquals(1,    xmlDate.getDay());
	}

	/**
	 * A Date value early in the morning (e.g. 00:30) must still map to the
	 * SAME calendar day – not be bumped back to the previous day.
	 */
	@Test
	public void testFromDate_earlyMorning_doesNotShiftToPreviousDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(2026, Calendar.MAY, 1, 0, 30, 0);
		cal.set(Calendar.MILLISECOND, 0);

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(cal.getTime());

		assertEquals(2026, xmlDate.getYear());
		assertEquals(5,    xmlDate.getMonth());
		assertEquals(1,    xmlDate.getDay());
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
