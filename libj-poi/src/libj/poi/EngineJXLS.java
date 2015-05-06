package libj.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import libj.debug.Debug;
import libj.debug.Log;
import libj.error.Throw;
import libj.utils.Stream;
import libj.utils.Xml;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;

public class EngineJXLS {

	private Workbook doc;
	private Document data;

	public EngineJXLS(InputStream templateStream, InputStream dataStream) {

		try {

			// create workbook from template
			this.doc = WorkbookFactory.create(templateStream);

			// data model
			this.data = Xml.parse(dataStream);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

	public Workbook getDoc() {
		return doc;
	}

	public void setDoc(Workbook doc) {
		this.doc = doc;
	}

	public Document getData() {
		return data;
	}

	public void setData(Document data) {
		this.data = data;
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

	@SuppressWarnings("all")
	private void processDocument() {

		try {

			ArrayList<Map> items = Xml.createMapList(data, Debug.isEnabled());

			Log.printMapList(items);

			Map beans = new HashMap();
			beans.put(Xml.TAG_NAME_ITEMS, items);

			XLSTransformer transformer = new XLSTransformer();
			transformer.transformWorkbook(doc, beans);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}

	public void createReport(OutputStream resultStream) {

		try {

			processDocument();
			doc.write(resultStream);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}
}
