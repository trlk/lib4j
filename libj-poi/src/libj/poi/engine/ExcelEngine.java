package libj.poi.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libj.debug.Debug;
import libj.debug.Log;
import libj.error.Throw;
import libj.poi.Engine;
import libj.utils.Stream;
import libj.utils.Xml;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelEngine extends Engine {

	private Workbook doc;

	public ExcelEngine(InputStream templateStream, InputStream dataStream) {

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

	@SuppressWarnings("all")
	protected void processData() {

		try {

			List<Map> items = Xml.createMapList(data, Debug.isEnabled());

			Log.printMapList(Log.DEBUG, items);

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

			processData();
			doc.write(resultStream);

		} catch (Exception e) {
			Throw.runtimeException(e);
		}
	}
}
