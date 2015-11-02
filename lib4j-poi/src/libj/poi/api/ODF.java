package libj.poi.api;

import java.io.InputStream;
import java.io.OutputStream;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.converter.pdf.PdfConverter;
import org.odftoolkit.odfdom.converter.pdf.PdfOptions;
import org.xml.sax.InputSource;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

public class ODF {
	
	public static void createReport(InputStream templateStream,
			InputStream dataStream, OutputStream reportStream) {

		try {

			// 1) Load Docx file by filling Freemarker template engine and cache
			// it to the registry
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(
					templateStream, TemplateEngineKind.Freemarker);

			// 2) Create fields metadata to manage lazy loop (#forech velocity)
			// for table row.
			// FieldsMetadata metadata = report.createFieldsMetadata();
			// metadata.addFieldAsList( "doc.project.developer.@name" );
			// metadata.addFieldAsList( "developers.lastName" );
			// metadata.addFieldAsList( "developers.mail" );

			// 3) Create context Java model
			IContext context = report.createContext();
			InputSource projectInputSource = new InputSource(dataStream);
			freemarker.ext.dom.NodeModel model = freemarker.ext.dom.NodeModel
					.parse(projectInputSource);

			context.put("data", model);

			// 4) Generate report by merging Java model with the Docx
			report.process(context, reportStream);

		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}	

	public static void convertToPDF(InputStream documentStream,
			OutputStream pdfStream) {

		try {

			// 1) Load odt with ODFDOM
			OdfTextDocument document = OdfTextDocument
					.loadDocument(documentStream);

			// 2) Convert POI XWPFDocument 2 PDF with iText
			PdfOptions options = PdfOptions.create(); //.fontEncoding("windows-1250");			
			PdfConverter.getInstance().convert(document, pdfStream, options);

		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}

}
