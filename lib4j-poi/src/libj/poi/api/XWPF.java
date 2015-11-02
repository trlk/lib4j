package libj.poi.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.xml.sax.InputSource;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import freemarker.ext.dom.NodeModel;
import libj.debug.Trace;

public class XWPF {

	public static String getText(XWPFRun r) {

		return r.getText(0);
	}

	public static String getText(XWPFParagraph p) {

		StringBuilder sb = new StringBuilder();

		for (XWPFRun r : p.getRuns()) {

			String runText = getText(r);

			if (runText != null)
				sb.append(runText);
		}

		return sb.toString();
	}

	public static String getText(XWPFDocument doc) {

		StringBuilder sb = new StringBuilder();

		for (XWPFParagraph p : doc.getParagraphs()) {
			sb.append(getText(p));
		}

		for (XWPFTable tbl : doc.getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph p : cell.getParagraphs()) {
						sb.append(getText(p));
					}
				}
			}
		}

		return sb.toString();
	}

	public static void replace(XWPFParagraph p, String searchText,
			String replaceText) {

		String text = getText(p);

		if (text.contains(searchText)) {

			if (replaceText != null)
				text = text.replace(searchText, replaceText);
			else
				text = text.replace(searchText, new String());

			for (XWPFRun r : p.getRuns()) {
				r.setText(text, 0);
				text = new String(); // костыль
			}
		}
	}

	public static void replace(XWPFDocument doc, String searchText,
			String replaceText) throws Exception {

		if (searchText != null) {

			for (XWPFParagraph p : doc.getParagraphs()) {
				replace(p, searchText, replaceText);
			}

			for (XWPFTable tbl : doc.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							replace(p, searchText, replaceText);
						}
					}
				}
			}
		}
	}

	public static XWPFDocument openDocument(InputStream documentStream) {

		try {

			return new XWPFDocument(documentStream);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static XWPFDocument openDocument(String fileName) {

		try {

			FileInputStream fileStream = new FileInputStream(fileName);

			return openDocument(fileStream);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void createReport(InputStream templateStream,
			InputStream dataStream, OutputStream reportStream,
			TemplateEngineKind engineKind) {

		try {

			// Load Docx file by filling Freemarker template engine and cache
			// it to the registry
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
					templateStream, engineKind);

			// Create fields metadata to manage lazy loop (#forech velocity)
			// for table row.
			// FieldsMetadata metadata = report.createFieldsMetadata();
			// metadata.addFieldAsList( "doc.project.developer.@name" );
			// metadata.addFieldAsList( "developers.lastName" );
			// metadata.addFieldAsList( "developers.mail" );

			// Data source
			InputSource dataInputSource = new InputSource(dataStream);
			NodeModel model = NodeModel.parse(dataInputSource);

			// Create context Java model
			IContext context = report.createContext();

			// Bind root node
			NodeModel root = (NodeModel) model.getChildNodes().get(0);
			context.put(root.getNodeName(), root);

			// Generate report by merging Java model with the Docx
			report.process(context, reportStream);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void createVelocityReport(InputStream templateStream,
			InputStream dataStream, OutputStream reportStream) {

		Trace.point();

		createReport(templateStream, dataStream, reportStream,
				TemplateEngineKind.Velocity);
	}

	public static void createFreemarkerReport(InputStream templateStream,
			InputStream dataStream, OutputStream reportStream) {

		Trace.point();

		createReport(templateStream, dataStream, reportStream,
				TemplateEngineKind.Freemarker);
	}

	public static void documentToPDF(XWPFDocument document,
			OutputStream pdfStream) {

		try {

			// Convert POI XWPFDocument 2 PDF with iText
			PdfOptions options = PdfOptions.create();

			// options.fontEncoding("Identity-H");
			// options.fontEncoding("windows-1251");

			PdfConverter.getInstance().convert(document, pdfStream, options);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void convertToPDF(InputStream documentStream,
			OutputStream pdfStream) {

		XWPFDocument document = openDocument(documentStream);
		documentToPDF(document, pdfStream);
	}

}
