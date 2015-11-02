package libj.j2ee;

import javax.annotation.Resource;

public class App {

	@Resource(lookup = "java:app/AppName")
	private static String appName;

	@Resource(lookup = "java:module/ModuleName")
	private static String moduleName;

	public static String getAppName() {
		return appName;
	}

	public static String getModuleName() {
		return moduleName;
	}

}
