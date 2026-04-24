package se.skltp.tak.services;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XmlGregorianCalendarUtil {
	private static DatatypeFactory datatypeFactory = getDatatypeFactory();
	
	// a DatatypeFactory is really expensive to create, only do it once
	private static DatatypeFactory getDatatypeFactory() {
		try {
			return DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Could not create DatatypeFactory", e);
		}
	}

	/**
	 * Creates an XMLGregorianCalendar representing current time.
	 * @return
	 */
	public static final XMLGregorianCalendar getNowAsXMLGregorianCalendar() {
		GregorianCalendar now = (GregorianCalendar) GregorianCalendar.getInstance();
		return datatypeFactory.newXMLGregorianCalendar(now);
	}
	
	public static final XMLGregorianCalendar fromDate(Date date) {
		Calendar theDate = Calendar.getInstance();
		theDate.setTime(date);
		return datatypeFactory.newXMLGregorianCalendar(
				theDate.get(Calendar.YEAR),
				theDate.get(Calendar.MONTH) + 1,
				theDate.get(Calendar.DATE),
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED,
				DatatypeConstants.FIELD_UNDEFINED);
	}
}
