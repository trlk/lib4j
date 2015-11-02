package libj.error;

import libj.utils.Text;

@SuppressWarnings("serial")
public class RuntimeError extends RuntimeException {

	protected RuntimeError() {

		super();
	}

	public RuntimeError(Throwable e) {

		super(e);
	}

	public RuntimeError(String message) {

		super(message);
	}

	public RuntimeError(String format, Object... args) {

		this(Text.sprintf(format, args));
	}

}
