package libj.error;

import libj.debug.Error;
import libj.utils.Text;

public class Throw {

	public static void exception(Throwable e) throws Exception {

		Error.print(e);
		throw new Exception(e);
	}

	public static void runtimeException(Throwable e) throws RuntimeException {

		Error.print(e);
		throw new RuntimeException(e);
	}

	public static void runtimeException(String text) throws RuntimeException {

		throw new RuntimeException(text);
	}

	public static void runtimeException(String format, Object... args) throws RuntimeException {

		throw new RuntimeException(Text.sprintf(format, args));
	}

}
