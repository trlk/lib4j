package libj.db;


public abstract class Database {

	// SQL states
	public static final String STATE_SUCCESS = "00000";
	public static final String STATE_GENERAL_WARN = "01000";
	public static final String STATE_GENERAL_ERROR = "HY000";
	public static final String STATE_TIMEOUT_EXPIRED = "HYT00";

	// таймаут по-умолчанию
	public static final Integer DEFAULT_TIMEOUT = 60;

	// переменные
	protected String url;
	protected Integer timeout = DEFAULT_TIMEOUT;

	// прочесть URL
	public String getURL() {
		return url;
	}

	// установить URL
	protected void setURL(String url) {
		this.url = url;
	}

	// прочесть таймаут
	public Integer getTimeout() {
		return timeout;
	}

	// установить таймаут
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

}
