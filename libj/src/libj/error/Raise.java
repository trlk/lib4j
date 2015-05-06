package libj.error;

@Deprecated
public class Raise {

	public static void exception(Throwable e) throws Exception {

		Throw.exception(e);
	}

	public static void runtimeException(Throwable e) throws RuntimeException {

		Throw.runtimeException(e);
	}

	public static void runtimeException(String message) throws RuntimeException {

		Throw.runtimeException(message);
	}

	public static void runtimeException(String format, Object... args) throws RuntimeException {

		Throw.runtimeException(format, args);
	}

}
