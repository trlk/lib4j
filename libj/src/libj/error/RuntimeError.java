package libj.error;

import libj.utils.Text;

@SuppressWarnings("serial")
public class RuntimeError extends RuntimeException {

	public RuntimeError(Throwable e) {

		super(e);
	}

	public RuntimeError(String message) {

		super(message);
	}

	public RuntimeError(String format, Object... args) throws RuntimeException {

		super(Text.printf(format, args));
	}

}
