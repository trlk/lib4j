package libj.utils;

import java.net.InetAddress;

import libj.error.Raise;

public class Net {

	public static String getHostName() {

		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			Raise.runtimeException(e);
		}

		return null;
	}

	public static String getHostAddress() {

		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			Raise.runtimeException(e);
		}

		return null;
	}

}
