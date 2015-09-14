package libj.java;


public class JUtils {

	public static String getClassSimpleName(String className) {

		try {
			return Class.forName(className).getSimpleName();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
