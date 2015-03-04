package libj.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Date {

	public static final String defaultDateFormat = "dd.MM.yyyy";
	public static final String defaultTimeFormat = "HH:mm:ss";

	public static java.util.Date now() {

		return new java.util.Date();
	}

	// дата JAVA->GC
	public static GregorianCalendar getGC(java.util.Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		return gc;
	}

	// дата SQL->JAVA
	public static java.util.Date getDate(java.sql.Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		return gc.getTime();
	}

	// дата XML->JAVA
	public static java.util.Date getDate(XMLGregorianCalendar d) {

		if (d == null)
			return null;

		return d.toGregorianCalendar().getTime();
	}

	// дата GC->JAVA
	public static java.util.Date getDate(GregorianCalendar gc) {

		if (gc == null)
			return null;

		return gc.getTime();
	}

	// дата JAVA->XML
	public static XMLGregorianCalendar getXmlDate(java.util.Date d) {

		if (d == null)
			return null;

		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);

		XMLGregorianCalendar xgc;

		try {
			xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return xgc;
	}

	// дата XML->SQL
	public static java.sql.Date getSqlDate(XMLGregorianCalendar d) {

		if (d == null)
			return null;

		java.util.Date r1 = d.toGregorianCalendar().getTime();
		java.sql.Date r2 = new java.sql.Date(r1.getTime());

		return r2;
	}

	// дата JAVA->SQL
	public static java.sql.Date getSqlDate(java.util.Date d) {

		if (d == null)
			return null;

		java.sql.Date r = new java.sql.Date(d.getTime());

		return r;
	}

	// дата JAVA->SQL
	public static java.sql.Time getSqlTime(java.util.Date d) {

		if (d == null)
			return null;

		java.sql.Time r = new java.sql.Time(d.getTime());

		return r;
	}

	// дата+время JAVA->SQL
	public static java.sql.Timestamp getSqlTimestamp(java.util.Date d) {

		if (d == null)
			return null;

		java.sql.Timestamp r = new java.sql.Timestamp(d.getTime());

		return r;
	}

	// преобразование строки в дату согласно формату
	public static java.util.Date toDate(String text, String format) {

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
	public static String formatDate(java.util.Date date, String format) {

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

}
