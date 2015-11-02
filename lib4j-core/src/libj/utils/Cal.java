package libj.utils;

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
	public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy".intern();
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss".intern();
	public static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT + Const.SPC + DEFAULT_TIME_FORMAT;
	public static final TimeZone TIMEZONE_DEFAULT = TimeZone.getDefault();;
	public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

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

	public static Date clone(Date arg) {

		if (arg == null) {
			throw new IllegalArgumentException();
		}

		return new Date(arg.getTime());
	}

	public static Date min(Date... args) {

		Date result = null;

		for (Date arg : args) {

			if (arg != null) {

				if (result == null || diff(arg, result) < 0) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Date max(Date... args) {

		Date result = null;

		for (Date arg : args) {

			if (arg != null) {

				if (result == null || diff(arg, result) > 0) {
					result = arg;
				}
			}
		}

		return result;
	}

	public static Date nvl(Date... args) {

		return (Date) Obj.nvl((Object[]) args);
	}

	public static long diff(Date arg0, Date arg1) {

		if (Obj.isNullAny(arg0, arg1)) {
			throw new IllegalArgumentException();
		}

		return arg0.getTime() - arg1.getTime();
	}

	public static Date trunc(Date arg, TimeZone tz) {

		if (arg == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance(tz);

		cal.setTime(arg);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		return cal.getTime();
	}

	public static Date trunc(Date arg) {

		return trunc(arg, TIMEZONE_DEFAULT);
	}

	public static Date round(Date arg, TimeZone tz) {

		if (arg == null) {
			return null;
		}

		Date truncDate = trunc(arg, tz);

		long millis = millisBetween(arg, truncDate);

		if (millis >= MILLIS_PER_DAY / 2) {
			return addDays(truncDate, 1);
		} else {
			return truncDate;
		}
	}

	public static Date round(Date arg) {

		return round(arg, TIMEZONE_DEFAULT);
	}

	public static Date utcTrunc(Date arg) {

		return trunc(arg, TIMEZONE_UTC);
	}

	public static Date utcRound(Date arg) {

		return round(arg, TIMEZONE_UTC);
	}

	public static boolean isEquals(Date arg0, Date arg1) {

		if (Obj.isNullAny(arg0, arg1)) {
			return false;
		}

		return diff(arg0, arg1) == 0;
	}

	public static boolean isEqualsAny(Date what, Date... args) {

		return Obj.isEqualsAny(what, (Object[]) args);
	}

	public static boolean isBefore(Date what, Date than) {

		if (Obj.isNullAny(what, than)) {
			return false;
		}

		return diff(what, than) < 0;
	}

	public static boolean isAfter(Date what, Date than) {

		if (Obj.isNullAny(what, than)) {
			return false;
		}

		return diff(what, than) > 0;
	}

	public static boolean isBetween(Date what, Date dateFrom, Date dateTo) {

		if (Obj.isNullAny(what, dateFrom, dateTo)) {
			return false;
		}

		return diff(dateFrom, what) <= 0 && diff(what, dateTo) <= 0;
	}

	public static boolean isTruncated(Date arg) {

		return isEquals(arg, trunc(arg));
	}

	// дата JAVA->CAL
	public static Calendar toCalendar(Date arg) {

		if (arg == null) {
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(arg);

		return calendar;
	}

	// дата JAVA->GC
	public static GregorianCalendar toGregorianCalendar(Date arg) {

		if (arg == null) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(arg);

		return gc;
	}

	// дата SQL->JAVA
	public static Date toDate(java.sql.Date arg) {

		if (arg == null) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(arg);

		return gc.getTime();
	}

	// дата XML->JAVA
	public static Date toDate(XMLGregorianCalendar arg) {

		if (arg == null) {
			return null;
		}

		return arg.toGregorianCalendar().getTime();
	}

	// дата GC->JAVA
	public static Date toDate(GregorianCalendar arg) {

		if (arg == null) {
			return null;
		}

		return arg.getTime();
	}

	// дата CAL->JAVA
	public static Date toDate(Calendar arg) {

		if (arg == null) {
			return null;
		}

		return arg.getTime();
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
	public static XMLGregorianCalendar toXMLDate(Date arg) {

		if (arg == null) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(arg);

		XMLGregorianCalendar xgc;

		try {

			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return xgc;
	}

	// дата SQL->XML
	public static XMLGregorianCalendar toXMLDate(java.sql.Date arg) {

		if (arg == null) {
			return null;
		}

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(arg);

		return toXMLDate(gc);
	}

	// дата GC->XML
	public static XMLGregorianCalendar toXMLDate(GregorianCalendar arg) {

		if (arg == null) {
			return null;
		}

		try {

			return DatatypeFactory.newInstance().newXMLGregorianCalendar(arg);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// дата XML->SQL
	public static java.sql.Date toSQLDate(XMLGregorianCalendar arg) {

		if (arg == null) {
			return null;
		}

		Date date = arg.toGregorianCalendar().getTime();

		return new java.sql.Date(date.getTime());
	}

	// дата JAVA->SQL
	public static java.sql.Date toSQLDate(Date arg) {

		if (arg == null) {
			return null;
		}

		return new java.sql.Date(arg.getTime());
	}

	// преобразование строки в дату согласно формату
	public static java.sql.Date toSQLDate(String text, String format) {

		if (Text.isEmpty(text)) {
			return null;
		}

		return toSQLDate(toDate(text, format));
	}

	// дата JAVA->SQL
	public static java.sql.Time toSQLTime(Date arg) {

		if (arg == null) {
			return null;
		}

		return new java.sql.Time(arg.getTime());
	}

	// преобразование строки во время согласно формату
	public static java.sql.Time toSQLTime(String text, String format) {

		if (Text.isEmpty(text)) {
			return null;
		}

		return toSQLTime(toDate(text, format));
	}

	// дата+время JAVA->SQL
	public static java.sql.Timestamp toSQLTimestamp(Date arg) {

		if (arg == null) {
			return null;
		}

		return new java.sql.Timestamp(arg.getTime());
	}

	// преобразование строки в дату+время согласно формату
	public static java.sql.Timestamp toSQLTimestamp(String text, String format) {

		if (Text.isEmpty(text)) {
			return null;
		}

		return toSQLTimestamp(toDate(text, format));
	}

	public static long millisBetween(Date arg0, Date arg1) {

		if (Obj.isNullAny(arg0, arg1)) {
			throw new IllegalArgumentException();
		}

		return arg0.getTime() - arg1.getTime() + getTimeZoneDiff(arg0, arg1);
	}

	public static long secondsBetween(Date arg0, Date arg1) {

		return millisBetween(arg0, arg1) / MILLIS_PER_SECOND;
	}

	public static long minutesBetween(Date arg0, Date arg1) {

		return secondsBetween(arg0, arg1) / SECONDS_PER_MINUTE;
	}

	public static long hoursBetween(Date arg0, Date arg1) {

		return secondsBetween(arg0, arg1) / SECONDS_PER_HOUR;
	}

	public static long daysBetween(Date arg0, Date arg1) {

		return secondsBetween(arg0, arg1) / SECONDS_PER_DAY;
	}

	public static Float secondsDiff(Date arg0, Date arg1) {

		return new Float(millisBetween(arg0, arg1)) / MILLIS_PER_SECOND;
	}

	public static Float minutesDiff(Date arg0, Date arg1) {

		return new Float(millisBetween(arg0, arg1)) / MILLIS_PER_MINUTE;
	}

	public static Float hoursDiff(Date arg0, Date arg1) {

		return new Float(secondsBetween(arg0, arg1)) / SECONDS_PER_HOUR;
	}

	public static Float daysDiff(Date arg0, Date arg1) {

		return new Float(secondsBetween(arg0, arg1)) / SECONDS_PER_DAY;
	}

	public static Date addMillis(Date date, long millis) {

		if (date == null) {
			return null;
		}

		Date result = new Date(0);

		result.setTime(date.getTime() + millis);

		long tzDelta = getTimeZoneDiff(date, result);

		if (tzDelta != 0) {
			result = addMillis(result, tzDelta);
		}

		return result;
	}

	public static Date addSeconds(Date date, int seconds) {

		return addMillis(date, seconds * MILLIS_PER_SECOND);
	}

	public static Date addMinutes(Date date, int minutes) {

		return addMillis(date, minutes * MILLIS_PER_MINUTE);
	}

	public static Date addDays(Date date, int days) {

		return addMillis(date, days * MILLIS_PER_DAY);
	}

	public static Date addMonths(Date date, int months) {

		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.add(Calendar.MONTH, months);

		return cal.getTime();
	}

	public static TimeZone getTimeZone(String tzID) {

		return TimeZone.getTimeZone(tzID);
	}

	public static long getTimeZoneOffset(Date date, TimeZone tz) {

		if (date == null || tz == null) {
			throw new IllegalArgumentException();
		}

		return tz.getOffset(date.getTime());
	}

	public static long getTimeZoneOffset(Date date) {

		return getTimeZoneOffset(date, TIMEZONE_DEFAULT);
	}

	public static long getTimeZoneOffset() {

		return getTimeZoneOffset(now());
	}

	public static long getTimeZoneDiff(Date arg0, Date arg1) {

		if (Obj.isNullAny(arg0, arg1)) {
			throw new IllegalArgumentException();
		}

		long offset0 = getTimeZoneOffset(arg0);
		long offset1 = getTimeZoneOffset(arg1);

		return offset0 - offset1;
	}

	public static Date utcToLocal(Date date) {

		if (date == null) {
			return null;
		}

		return new Date(date.getTime() + getTimeZoneOffset(date));
	}

	public static Date localToUTC(Date date) {

		if (date == null) {
			return null;
		}

		return new Date(date.getTime() - getTimeZoneOffset(date));
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

}
