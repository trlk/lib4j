package libj.poi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import libj.debug.Log;
import libj.utils.Xml;

import org.w3c.dom.Document;

public abstract class Engine {

	protected final String TAG_VELOCITY = "$";
	protected final String TAG_FREEMARKER = "${";

	protected String openTag = "#{";
	protected String closeTag = "}";
	protected final String likeTag = "//";

	protected Document data;
	private List<Plugin> plugins = new ArrayList<Plugin>();

	public Document getData() {
		return data;
	}

	public void setData(Document data) {
		this.data = data;
	}

	public InputStream getDataInputStream() {
		return Xml.serializeToInputStream(data);
	}

	public OutputStream getDataOutputStream() {
		return Xml.serializeToOutputStream(data);
	}

	public String getOpenTag() {
		return openTag;
	}

	public void setOpenTag(String openTag) {
		this.openTag = openTag;
	}

	public String getCloseTag() {
		return closeTag;
	}

	public void setCloseTag(String closeTag) {
		this.closeTag = closeTag;
	}

	public void registerPlugin(Plugin plugin) {

		plugins.add(plugin);
	}

	public String callFunc(String funcName, Object[] args) {

		try {

			for (Plugin plugin : plugins) {

				if (plugin.hasFunc(funcName)) {

					return plugin.call(funcName, args);
				}
			}

		} catch (Exception e) {

			Log.error(e);
			return e.getMessage();
		}

		Log.warn("Unknown function: %s", funcName);
		return null;
	}

	protected abstract void processData();

	public abstract void createReport(OutputStream resultStream);

}
