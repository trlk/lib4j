package libj.utils;

import java.net.InetAddress;

import libj.debug.Log;

public class Net {

	public static String thisHostName() {

		try {

			return InetAddress.getLocalHost().getHostName();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String thisHostAddress() {

		try {

			return InetAddress.getLocalHost().getHostAddress();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Boolean ping(String hostName, int timeout) {

		try {

			InetAddress inet = InetAddress.getByName(hostName);

			Log.debug("Ping %s (%s):", hostName, inet.getHostAddress());

			Boolean result = inet.isReachable(timeout);

			Log.debug(result ? "Host is reachable" : "Host is NOT reachable");

			return result;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static Boolean ping(String hostName) {

		return ping(hostName, 2000);
	}
}
