package libj.error;

import libj.utils.Text;

@Deprecated
@SuppressWarnings("serial")
public class RuntimeException2 extends java.lang.RuntimeException {

	public RuntimeException2(Throwable e) {

		super(e);
	}

	public RuntimeException2(String message) {

		super(message);
	}

	public RuntimeException2(String format, Object... args) throws RuntimeException {

		super(Text.printf(format, args));
	}

}
