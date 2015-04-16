package libj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Cal {

	public static final long MILLIS_PER_SECOND = 1000;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
	public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	public static final long SECONDS_PER_MINUTE = 60;
	public static final long SECONDS_PER_HOUR = 3600;
	public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;

	public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

	public static Date now() {

		return new Date();
	}

	public static Date today() {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		return cal.getTime();
	}

	public static Date zero() {

		return new Date(0);
	}

	public static Date copy(Date d) {

		return new Date(d.getTime());
	}

	public static Date min(Date d0, Date d1) {

		if (d0.getTime() < d1.getTime())
			return d0;
		else
			return d1;
	}

	public static Date max(Date d0, Date d1) {

		if (d0.getTime() > d1.getTime())
			return d0;
		else
			return d1;
	}

	public static Date nvl(Date d0, Date d1) {

		if (d0 != null)
			return d0;
		else
			return d1;
	}

	public static Long diff(Date d0, Date d1) {

		if (d0 == null || d1 == null)
			return null;

		return d0.getTime() - d1.getTime();
	}

	public static boolean equals(Date d0, Date d1) {

		if (d0 == null || d1 == null)
			return false;

		return diff(d0, d1) == 0;
	}

	// дата JAVA->GC
	public static GregorianCalendar getGC(Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		return gc;
	}

	// дата SQL->JAVA
	public static Date getDate(java.sql.Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		return gc.getTime();
	}

	// дата XML->JAVA
	public static Date getDate(XMLGregorianCalendar d) {

		if (d == null)
			return null;

		return d.toGregorianCalendar().getTime();
	}

	// дата GC->JAVA
	public static Date getDate(GregorianCalendar gc) {

		if (gc == null)
			return null;

		return gc.getTime();
	}

	// дата JAVA->XML
	public static XMLGregorianCalendar getXmlDate(Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		XMLGregorianCalendar xgc;

		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return xgc;
	}

	// дата SQL->XML
	public static XMLGregorianCalendar getXmlDate(java.sql.Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		XMLGregorianCalendar xgc;

		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return xgc;
	}

	// дата GC->XML
	public static XMLGregorianCalendar getXmlDate(GregorianCalendar gc) {

		if (gc == null)
			return null;

		XMLGregorianCalendar xgc;

		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return xgc;
	}

	// дата XML->SQL
	public static java.sql.Date getSqlDate(XMLGregorianCalendar d) {

		if (d == null)
			return null;

		Date r1 = d.toGregorianCalendar().getTime();
		java.sql.Date r2 = new java.sql.Date(r1.getTime());

		return r2;
	}

	// дата JAVA->SQL
	public static java.sql.Date getSqlDate(Date d) {

		if (d == null)
			return null;

		java.sql.Date r = new java.sql.Date(d.getTime());

		return r;
	}

	// дата JAVA->SQL
	public static java.sql.Time getSqlTime(Date d) {

		if (d == null)
			return null;

		java.sql.Time r = new java.sql.Time(d.getTime());

		return r;
	}

	// дата+время JAVA->SQL
	public static java.sql.Timestamp getSqlTimestamp(Date d) {

		if (d == null)
			return null;

		java.sql.Timestamp r = new java.sql.Timestamp(d.getTime());

		return r;
	}

	// преобразование строки в дату согласно формату
	public static Date toDate(String text, String format) {

		try {

			return new SimpleDateFormat(format).parse(text);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// преобразование строки в дату согласно формату
	public static java.sql.Date toSqlDate(String text, String format) {

		try {

			return getSqlDate(new SimpleDateFormat(format).parse(text));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// преобразование строки в дату+время согласно формату
	public static java.sql.Timestamp toSqlTimestamp(String text, String format) {

		try {

			return getSqlTimestamp(new SimpleDateFormat(format).parse(text));

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	// форматирование даты
	public static String formatDate(Date date, String format) {

		try {

			if (format != null)
				return new SimpleDateFormat(format).format(date);
			else
				return date.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// форматирование даты
	public static String formatDate(GregorianCalendar gc, String format) {

		try {

			if (format != null)
				return new SimpleDateFormat(format).format(gc.getTime());
			else
				return gc.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static long millisBetween(Date d0, Date d1) {
		return d0.getTime() - d1.getTime();
	}

	public static long secondsBetween(Date d0, Date d1) {
		return millisBetween(d0, d1) / MILLIS_PER_SECOND;
	}

	public static long minutesBetween(Date d0, Date d1) {
		return secondsBetween(d0, d1) / SECONDS_PER_MINUTE;
	}

	public static long hoursBetween(Date d0, Date d1) {
		return secondsBetween(d0, d1) / SECONDS_PER_HOUR;
	}

	public static long daysBetween(Date d0, Date d1) {
		return secondsBetween(d0, d1) / SECONDS_PER_DAY;
	}

	public static Float minutesDiff(Date d0, Date d1) {
		return new Float(millisBetween(d0, d1)) / MILLIS_PER_MINUTE;
	}

	public static Float hoursDiff(Date d0, Date d1) {
		return new Float(secondsBetween(d0, d1)) / SECONDS_PER_HOUR;
	}

	public static Float daysDiff(Date d0, Date d1) {
		return new Float(secondsBetween(d0, d1)) / SECONDS_PER_DAY;
	}

	public static Date addMillis(Date d, long millis) {

		Date result = new Date();

		result.setTime(d.getTime() + millis);

		return result;
	}

	public static Date addSeconds(Date d, int seconds) {

		return addMillis(d, seconds * MILLIS_PER_SECOND);
	}

	public static Date addMinutes(Date d, int minutes) {

		return addMillis(d, minutes * MILLIS_PER_MINUTE);
	}

	public static Date addDays(Date d, int days) {

		return addMillis(d, days * MILLIS_PER_DAY);
	}

	public static Date addMonths(Date d, int months) {

		Calendar cal = Calendar.getInstance();

		cal.setTime(d);
		cal.add(Calendar.MONTH, months);

		return cal.getTime();
	}

	public static Date trunc(Date d) {

		Calendar cal = Calendar.getInstance();

		cal.setTime(d);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		return cal.getTime();
	}

	public static TimeZone getTimeZone() {

		return TimeZone.getDefault();
	}

	public static TimeZone getTimeZone(String tzID) {

		return TimeZone.getTimeZone(tzID);
	}

	public static int getTimeZoneOffset(Date d) {

		return getTimeZone().getOffset(d.getTime());
	}

	public static int getTimeZoneOffset() {

		return getTimeZoneOffset(now());
	}

	public static Date utcToLocal(Date d) {

		return new Date(d.getTime() + getTimeZoneOffset(d));
	}

	public static Date localToUTC(Date d) {

		return new Date(d.getTime() - getTimeZoneOffset(d));
	}

}
