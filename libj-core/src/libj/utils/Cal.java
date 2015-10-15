package libj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import libj.error.RuntimeError;

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
	public static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + Const.SPC + DEFAULT_TIME_FORMAT;

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

	public static Date clone(Date d) {

		return new Date(d.getTime());
	}

	public static Date min(Date d0, Date d1) {

		if (d0.getTime() < d1.getTime()) {
			return d0;
		} else {
			return d1;
		}
	}

	public static Date max(Date d0, Date d1) {

		if (d0.getTime() > d1.getTime()) {
			return d0;
		} else {
			return d1;
		}
	}

	public static Date nvl(Date d0, Date d1) {

		if (d0 != null) {
			return d0;
		} else {
			return d1;
		}
	}

	public static Long diff(Date d0, Date d1) {

		if (d0 == null || d1 == null) {
			return null;
		}

		return d0.getTime() - d1.getTime();
	}

	public static Date trunc(Date d) {

		if (d == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();

		cal.setTime(d);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		return cal.getTime();
	}

	public static Date round(Date d) {

		if (d == null) {
			return null;
		}

		Date truncDate = trunc(d);

		long millis = millisBetween(d, truncDate);

		if (millis >= MILLIS_PER_DAY / 2) {
			return addDays(truncDate, 1);
		} else {
			return truncDate;
		}
	}

	public static boolean isEquals(Date d0, Date d1) {

		if (d0 == null || d1 == null) {
			return false;
		}

		return diff(d0, d1) == 0;
	}

	public static boolean isEqualsAny(Date date, Date... args) {

		if (date != null) {

			for (Date arg : args) {

				if (isEquals(date, arg)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isBetween(Date d, Date dateFrom, Date dateTo) {

		return diff(dateFrom, d) <= 0 && diff(d, dateTo) <= 0;
	}

	public static boolean isTruncated(Date d) {

		return isEquals(d, trunc(d));
	}

	// дата JAVA->GC
	public static GregorianCalendar toGC(Date d) {

		if (d == null) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		return gc;
	}

	// дата SQL->JAVA
	public static Date toDate(java.sql.Date d) {

		if (d == null) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		return gc.getTime();
	}

	// дата XML->JAVA
	public static Date toDate(XMLGregorianCalendar d) {

		if (d == null) {
			return null;
		}

		return d.toGregorianCalendar().getTime();
	}

	// дата GC->JAVA
	public static Date toDate(GregorianCalendar gc) {

		if (gc == null) {
			return null;
		}

		return gc.getTime();
	}

	// преобразование строки в дату согласно формату
	public static Date toDate(String text, String format) {

		if (Text.isEmpty(text)) {
			return null;
		}

		try {

			return new SimpleDateFormat(format).parse(text);

		} catch (Exception e) {
			throw new RuntimeError("Unparseable date: %s", text);
		}
	}

	// дата JAVA->XML
	public static XMLGregorianCalendar toXMLDate(Date d) {

		if (d == null) {
			return null;
		}

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
	public static XMLGregorianCalendar toXMLDate(java.sql.Date d) {

		if (d == null) {
			return null;
		}

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
	public static XMLGregorianCalendar toXMLDate(GregorianCalendar gc) {

		if (gc == null) {
			return null;
		}

		XMLGregorianCalendar xgc;

		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return xgc;
	}

	// дата XML->SQL
	public static java.sql.Date toSQLDate(XMLGregorianCalendar d) {

		if (d == null) {
			return null;
		}

		Date r1 = d.toGregorianCalendar().getTime();
		java.sql.Date r2 = new java.sql.Date(r1.getTime());

		return r2;
	}

	// дата JAVA->SQL
	public static java.sql.Date toSQLDate(Date d) {

		if (d == null) {
			return null;
		}

		java.sql.Date r = new java.sql.Date(d.getTime());

		return r;
	}

	// преобразование строки в дату согласно формату
	public static java.sql.Date toSQLDate(String text, String format) {

		if (Text.isEmpty(text)) {
			return null;
		}

		try {

			return toSQLDate(new SimpleDateFormat(format).parse(text));

		} catch (Exception e) {
			throw new RuntimeError("Unparseable date: %s", text);
		}

	}

	// дата JAVA->SQL
	public static java.sql.Time toSQLTime(Date d) {

		if (d == null) {
			return null;
		}

		java.sql.Time r = new java.sql.Time(d.getTime());

		return r;
	}

	// дата+время JAVA->SQL
	public static java.sql.Timestamp toSQLTimestamp(Date d) {

		if (d == null) {
			return null;
		}

		java.sql.Timestamp r = new java.sql.Timestamp(d.getTime());

		return r;
	}

	// преобразование строки в дату+время согласно формату
	public static java.sql.Timestamp toSQLTimestamp(String text, String format) {

		if (Text.isEmpty(text)) {
			return null;
		}

		try {

			return toSQLTimestamp(new SimpleDateFormat(format).parse(text));

		} catch (ParseException e) {
			throw new RuntimeError("Unparseable timestamp: %s", text);
		}
	}

	// форматирование даты
	public static String formatDate(Date date, String format) {

		if (date == null) {
			return null;
		}

		try {

			if (format != null) {
				return new SimpleDateFormat(format).format(date);
			} else {
				return date.toString();
			}

		} catch (Exception e) {
			throw new RuntimeError("%s: %s, format: %s", e.getMessage(), date.toString(), format);
		}
	}

	// форматирование даты
	public static String formatDate(GregorianCalendar date, String format) {

		if (date == null) {
			return null;
		}

		try {

			if (format != null) {
				return new SimpleDateFormat(format).format(date.getTime());
			} else {
				return date.toString();
			}

		} catch (Exception e) {
			throw new RuntimeError("%s: %s, format: %s", e.getMessage(), date.toString(), format);
		}
	}

	public static long millisBetween(Date d0, Date d1) {

		return d0.getTime() - d1.getTime() + getTimeZoneDiff(d0, d1);
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

	public static Float secondsDiff(Date d0, Date d1) {

		return new Float(millisBetween(d0, d1)) / MILLIS_PER_SECOND;
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

		long tzDelta = getTimeZoneDiff(d, result);

		if (tzDelta != 0) {
			result = addMillis(result, tzDelta);
		}

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

	public static TimeZone getTimeZone() {

		return TimeZone.getDefault();
	}

	public static TimeZone getTimeZone(String tzID) {

		return TimeZone.getTimeZone(tzID);
	}

	public static long getTimeZoneOffset(Date d) {

		return getTimeZone().getOffset(d.getTime());
	}

	public static long getTimeZoneOffset() {

		return getTimeZoneOffset(now());
	}

	public static long getTimeZoneDiff(Date d0, Date d1) {

		long offset0 = getTimeZoneOffset(d0);
		long offset1 = getTimeZoneOffset(d1);

		return offset0 - offset1;
	}

	public static Date utcToLocal(Date d) {

		return new Date(d.getTime() + getTimeZoneOffset(d));
	}

	public static Date localToUTC(Date d) {

		return new Date(d.getTime() - getTimeZoneOffset(d));
	}

}
