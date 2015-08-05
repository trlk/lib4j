package libj.j2ee;

import javax.annotation.Resource;

public class App {

	@Resource(lookup = "java:app/AppName")
	public static String appName;

	@Resource(lookup = "java:module/ModuleName")
	public static String moduleName;

}
