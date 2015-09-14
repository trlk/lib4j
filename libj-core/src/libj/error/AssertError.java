package libj.error;

@SuppressWarnings("serial")
public class AssertError extends RuntimeError {

	public AssertError(String message) {

		super(message);
	}

	public AssertError(String format, Object... args) {

		super(format, args);
	}

}
