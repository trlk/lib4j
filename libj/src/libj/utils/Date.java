package libj.utils;

import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class is deprecated
 * Use libj.utils.Cal instead
 */

@Deprecated
public class Date {

	public static java.util.Date now() {

		return Cal.now();
	}

	// дата JAVA->GC
	public static GregorianCalendar getGC(java.util.Date d) {

		return Cal.toGC(d);
	}

	// дата SQL->JAVA
	public static java.util.Date getDate(java.sql.Date d) {

		return Cal.toDate(d);
	}

	// дата XML->JAVA
	public static java.util.Date getDate(XMLGregorianCalendar d) {

		return Cal.toDate(d);
	}

	// дата GC->JAVA
	public static java.util.Date getDate(GregorianCalendar gc) {

		return Cal.toDate(gc);
	}

	// дата JAVA->XML
	public static XMLGregorianCalendar getXmlDate(java.util.Date d) {

		return Cal.toXMLDate(d);
	}

	// дата SQL->XML
	public static XMLGregorianCalendar getXmlDate(java.sql.Date d) {

		return Cal.toXMLDate(d);
	}

	// дата GC->XML
	public static XMLGregorianCalendar getXmlDate(GregorianCalendar gc) {

		return Cal.toXMLDate(gc);
	}

	// дата XML->SQL
	public static java.sql.Date getSqlDate(XMLGregorianCalendar d) {

		return Cal.toSQLDate(d);
	}

	// дата JAVA->SQL
	public static java.sql.Date getSqlDate(java.util.Date d) {

		return Cal.toSQLDate(d);
	}

	// дата JAVA->SQL
	public static java.sql.Time getSqlTime(java.util.Date d) {

		return Cal.toSQLTime(d);
	}

	// дата+время JAVA->SQL
	public static java.sql.Timestamp getSqlTimestamp(java.util.Date d) {

		return Cal.toSQLTimestamp(d);
	}

	// преобразование строки в дату согласно формату
	public static java.util.Date toDate(String text, String format) {

		return Cal.toDate(text, format);
	}

	// преобразование строки в дату согласно формату
	public static java.sql.Date toSqlDate(String text, String format) {

		return Cal.toSQLDate(text, format);
	}

	// преобразование строки в дату+время согласно формату
	public static java.sql.Timestamp toSqlTimestamp(String text, String format) {

		return Cal.toSQLTimestamp(text, format);
	}

	// форматирование даты
	public static String formatDate(java.util.Date date, String format) {

		return Cal.formatDate(date, format);
	}

	// форматирование даты
	public static String formatDate(GregorianCalendar gc, String format) {

		return Cal.formatDate(gc, format);
	}

}
