package libj.java;

public class ClassUtils {

	public static String getClassSimpleName(String className) {

		try {
			return Class.forName(className).getSimpleName();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean tryLoad(String className) {

		try {
			return Class.forName(className) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
