package libj.poi.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import libj.debug.Debug;
import libj.debug.Log;
import libj.error.RuntimeError;
import libj.poi.Engine;
import libj.poi.api.XWPF;
import libj.poi.tag.Part;
import libj.poi.tag.Tag;
import libj.utils.Stream;
import libj.utils.Text;
import libj.utils.Xml;

public class WordEngine extends Engine {

	public static enum DataMode {
		XPATH, COMMA, MIXED
	}

	private XWPFDocument doc;
	private DataMode dataMode = DataMode.MIXED; // default dataMode 

	public WordEngine(InputStream templateStream, InputStream dataStream, DataMode dataMode) {

		try {

			// create document from template
			setDoc(new XWPFDocument(templateStream));

			// parse xml data stream
			setData(Xml.parse(dataStream), dataMode);

		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	public WordEngine(InputStream templateStream, InputStream dataStream) {

		this(templateStream, dataStream, DataMode.MIXED);
	}

	public XWPFDocument getDoc() {
		return doc;
	}

	public void setDoc(XWPFDocument doc) {
		this.doc = doc;
	}

	public void setData(Document data, DataMode dataMode) {
		this.data = data;
		this.dataMode = dataMode;
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
			throw new RuntimeError(e);
		}
	}

	public ArrayList<String> getDocTags() {

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

	public List<Tag> getTagList() {

		List<Tag> tagList = new ArrayList<Tag>();
		Map<String, Tag> tagMap = new HashMap<String, Tag>();

		ArrayList<String> list = getDocTags();

		for (int i = 0; i < list.size(); i++) {

			String brutto = list.get(i);
			tagMap.put(brutto, new Tag(this, brutto));
		}

		// store into list
		for (Tag tag : tagMap.values()) {
			tagList.add(tag);
		}

		return tagList;
	}

	protected void processData() {

		try {

			List<Tag> taglist = getTagList();
			Map<String, Object> data = new HashMap<String, Object>();

			// comma separated tags
			if (this.dataMode == DataMode.COMMA || this.dataMode == DataMode.MIXED) {

				Map<String, Object> dataMap = Xml.createMap(this.data, Debug.isEnabled());

				// fetch data
				for (Tag tag : taglist) {

					String[] exprArray = tag.getExprArray();

					for (String expr : exprArray) {

						// like tag support
						if (expr.startsWith(likeTag)) {

							// search..
							for (String key : dataMap.keySet()) {

								if (key.endsWith(expr.substring(likeTag.length()))) {

									data.put(expr, dataMap.get(key));
									break;
								}
							}

						} else {

							if (dataMap.containsKey(expr)) {
								data.put(expr, dataMap.get(expr));
							}
						}
					}
				}
			}

			// xpath tags
			if (this.dataMode == DataMode.XPATH || this.dataMode == DataMode.MIXED) {

				// fetch data
				for (Tag tag : taglist) {

					String[] exprArray = tag.getExprArray();

					for (String expr : exprArray) {

						try {

							if (!data.containsKey(expr)) {
								data.put(expr, Xml.getString(this.data, expr));
							}

						} catch (Exception e) {
							Log.warn(e.getMessage());
						}
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
			for (Tag tag : taglist) {

				String brutto = tag.getBrutto(); // #{data.key}	
				String netto = tag.getNetto(); // data.key

				String value = null;

				if (tag.isFunc()) {

					List<Part> parts = tag.getParts();

					for (Part part : parts) {

						if (part.isExpr() && data.containsKey(part.getExpr())) {
							part.setData(data.get(part.getExpr()));
						}
					}

					value = callFunc(tag.getFuncName(), tag.getDataArray());

				} else {

					String expr = tag.getExpr(); // data.key

					if (data.containsKey(expr)) {

						value = data.get(expr).toString();
					}
				}

				if (value != null) {

					Log.debug("%s=%s", netto, value);
					XWPF.replace(doc, brutto, value);

				} else {

					Log.warn("Data not found: %s", netto);

					if (Debug.isDisabled()) {
						XWPF.replace(doc, brutto, Text.EMPTY_STRING);
					}
				}
			}

		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	private void createReport() {

		try {

			// have simple syntax?
			if (isSimpleSyntax()) {
				processData();
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
			throw new RuntimeError(e);
		}
	}

	public void createReport(OutputStream resultStream) {

		createReport();

		try {

			doc.write(resultStream);

		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

	public void createReportPDF(OutputStream pdfStream) {

		createReport();

		try {

			XWPF.documentToPDF(doc, pdfStream);

		} catch (Exception e) {
			throw new RuntimeError(e);
		}
	}

}
