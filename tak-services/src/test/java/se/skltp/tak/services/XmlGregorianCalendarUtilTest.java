/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
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

class XmlGregorianCalendarUtilTest {

	/**
	 * The old implementation included time-part info; the new one strips it.
	 * Comparing raw milliseconds is therefore no longer meaningful.
	 */
	@Disabled("fromDate() now returns date-only (time fields = FIELD_UNDEFINED); millisecond comparison is not valid for the new implementation")
	@Test
	void testFromDate() {
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
	void testFromDate_preservesYearMonthDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(2009, Calendar.MARCH, 9, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(cal.getTime());

		assertEquals(2009, xmlDate.getYear());
		assertEquals(3,    xmlDate.getMonth());
		assertEquals(9,    xmlDate.getDay());
	}

	/** The result must carry 0 as time information (no T00:00:00.000 in the XML output). */
	@Test
	void testFromDate_hasTimePart() {
		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(new Date());

		assertEquals(0, xmlDate.getHour());
		assertEquals(0, xmlDate.getMinute());
		assertEquals(0, xmlDate.getSecond());
		assertEquals(0, xmlDate.getMillisecond());
		assertEquals(DatatypeConstants.FIELD_UNDEFINED, xmlDate.getTimezone());
	}

	/**
	 * Hibernate returns java.sql.Date for DATE columns.
	 * Verify that the day is not shifted back by one when converting.
	 */
	@Test
	void testFromDate_sqlDate_doesNotShiftDay() {
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
	void testFromDate_eveningTime_doesNotShiftToNextDay() {
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
	void testFromDate_earlyMorning_doesNotShiftToPreviousDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(2026, Calendar.MAY, 1, 0, 30, 0);
		cal.set(Calendar.MILLISECOND, 0);

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil.fromDate(cal.getTime());

		assertEquals(2026, xmlDate.getYear());
		assertEquals(5,    xmlDate.getMonth());
		assertEquals(1,    xmlDate.getDay());
	}

	@Test
	void testGetNowAsXMLGregorianCalendar() {
		long tsBefore = System.currentTimeMillis();

		XMLGregorianCalendar xmlDate = XmlGregorianCalendarUtil
				.getNowAsXMLGregorianCalendar();

		long tsAfter = System.currentTimeMillis();

		assertTrue(xmlDate.toGregorianCalendar().getTime().getTime() >= tsBefore);
		assertTrue(xmlDate.toGregorianCalendar().getTime().getTime() <= tsAfter);
	}
}
