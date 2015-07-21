package libj.poi.plugin;

import java.text.DecimalFormat;
import java.util.Date;

import libj.error.RuntimeError;
import libj.poi.Plugin;
import libj.utils.App;
import libj.utils.Cal;
import libj.utils.Text;
import libj.xml.XMLSchema;

public class Formatter implements Plugin {

	private static String formatDate = "formatDate";
	private static String formatDouble = "formatDouble";

	public boolean hasFunc(String funcName) {
		return Text.equals(funcName, formatDate, formatDouble);
	}

	public String call(String funcName, Object[] args) throws Exception {

		if (funcName.equals(formatDate)) {
			return formatDate(args);
		} else if (funcName.equals(formatDouble)) {
			return formatDouble(args);
		} else {
			throw new RuntimeError("Invalid function: %s.%s", App.thisClassSimpleName(), funcName);
		}
	}

	private String formatDate(Object[] args) throws Exception {

		Date date = (Date) XMLSchema.createObject(Date.class, args[0].toString());
		String format = args[1].toString();

		return Cal.formatDate(date, format);
	}

	private String formatDouble(Object[] args) throws Exception {

		Double number = (Double) XMLSchema.createObject(Date.class, args[0].toString());
		String format = args[1].toString();

		DecimalFormat df = new DecimalFormat(format);

		return df.format(number);
	}


}
