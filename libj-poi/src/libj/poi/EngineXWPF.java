package libj.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import libj.debug.Debug;
import libj.debug.Log;
import libj.error.Throw;
import libj.utils.Stream;
import libj.utils.Xml;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

public class EngineXWPF {

	public static enum DataMode {
		COMMA, XPATH, MIXED
	}

	private final String TAG_VELOCITY = "$";
	private final String TAG_FREEMARKER = "${";

	private String openTag = "#{";
	private String closeTag = "}";

	private XWPFDocument doc;
	private Document data;
	private DataMode dataMode;

	public EngineXWPF(InputStream templateStream, InputStream dataStream, DataMode dataMode) {

		try {

			// create document from template
			setDoc(new XWPFDocument(templateStream));

			// parse xml data stream
			setData(Xml.parse(dataStream), dataMode);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

	public EngineXWPF(InputStream templateStream, InputStream dataStream) {

		this(templateStream, dataStream, DataMode.MIXED);
	}

	public XWPFDocument getDoc() {
		return doc;
	}

	public void setDoc(XWPFDocument doc) {
		this.doc = doc;
	}

	public Document getData() {
		return data;
	}

	public void setData(Document data) {
		this.data = data;
	}

	public void setData(Document data, DataMode dataMode) {
		this.data = data;
		this.dataMode = dataMode;
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

	public boolean isSimpleSyntax() {
		return XWPF.getText(doc).contains(openTag);
	}

	public boolean isVelocitySyntax() {

		if (!isFreemarkerSyntax()) {
			return XWPF.getText(doc).contains(TAG_VELOCITY);
		} else {
			return false;
		}
	}

	public boolean isFreemarkerSyntax() {
		return XWPF.getText(doc).contains(TAG_FREEMARKER);
	}

	public ByteArrayInputStream getDocInputStream() {
		return Stream.newInputStream(getDocOutputStream());
	}

	public ByteArrayOutputStream getDocOutputStream() {

		try {

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			doc.write(outputStream);

			return outputStream;

		} catch (Exception e) {
			Throw.runtimeException(e);
		}

		return null;
	}

	public InputStream getDataInputStream() {

		return Xml.serializeToInputStream(data);
	}

	public OutputStream getDataOutputStream() {

		return Xml.serializeToOutputStream(data);
	}

	public ArrayList<String> getTagList() {

		ArrayList<String> list = new ArrayList<String>();

		String text = XWPF.getText(doc);

		if (Debug.isEnabled()) {
			Log.debug("document.getText:\n%s", text);
		}

		int x = 0;

		do {
			x = text.indexOf(openTag, x);

			if (x >= 0) {

				int y = text.indexOf(closeTag, x);

				if (y >= 0) {

					y = y + closeTag.length();
					String tag = text.substring(x, y);

					if (!list.contains(tag)) {
						list.add(tag);
					}

					x = y;

				} else {
					x = -1;
				}
			}
		} while (x >= 0);

		return list;
	}

	public Map<String, String> getTagMap() {

		Map<String, String> map = new HashMap<String, String>();

		ArrayList<String> list = getTagList();

		for (int i = 0; i < list.size(); i++) {

			String tag = list.get(i);
			String dataPath = tag.substring(openTag.length(), tag.length() - closeTag.length()).trim();

			map.put(tag, dataPath);
		}

		return map;
	}

	public void processDocument() {

		try {

			Map<String, String> tags = getTagMap();
			Map<String, String> data = new HashMap<String, String>();

			if (this.dataMode == DataMode.COMMA || this.dataMode == DataMode.MIXED) {

				data.putAll(Xml.createMap(this.data, Debug.isEnabled()));
			}

			if (this.dataMode == DataMode.XPATH || this.dataMode == DataMode.MIXED) {

				// fetch data
				for (String dataPath : tags.values()) {

					try {

						if (!data.containsKey(dataPath)) {
							data.put(dataPath, Xml.getText(this.data, dataPath));
						}

					} catch (Exception e) {
						Log.warn(e.getMessage());
					}
				}
			}

			// print data map
			if (Debug.isEnabled()) {

				Log.debug("Report data:");

				// sort data map
				SortedSet<String> sortedKeys = new TreeSet<String>(data.keySet());

				for (String key : sortedKeys) {
					Log.debug("%s=%s", key, data.get(key));
				}
			}

			Log.debug("Processing report tags:");

			// process all keys in template
			for (Map.Entry<String, String> entry : tags.entrySet()) {

				String tag = entry.getKey(); // #{data.key}
				String key = entry.getValue(); // data.key

				if (data.containsKey(key)) {

					String keyValue = data.get(key);

					Log.debug("%s=%s", key, keyValue);
					XWPF.replace(doc, tag, keyValue);

				} else {

					Log.warn("Key not found: %s", key);

					if (Debug.isDisabled()) {
						XWPF.replace(doc, tag, new String());
					}
				}
			}

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

	private void createReport() {

		try {

			// have simple syntax?
			if (isSimpleSyntax()) {
				processDocument();
			}

			// have freemarker syntax?
			if (isFreemarkerSyntax()) {

				// resultStream
				ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

				XWPF.createFreemarkerReport(getDocInputStream(), getDataInputStream(), resultStream);

				setDoc(new XWPFDocument(Stream.newInputStream(resultStream)));
			}

			if (isVelocitySyntax()) {
				Log.info("Velocity syntax not supported yet");
			}

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

	public void createReport(OutputStream resultStream) {

		createReport();

		try {

			doc.write(resultStream);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

	public void createReportPDF(OutputStream pdfStream) {

		createReport();

		try {

			XWPF.documentToPDF(doc, pdfStream);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

}
