package libj.poi;


public interface Plugin {

	public boolean hasFunc(String funcName);

	public String call(String funcName, Object[] args) throws Exception;

}
