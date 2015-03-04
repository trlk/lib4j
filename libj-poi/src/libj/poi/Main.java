package libj.poi;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import libj.debug.Debug;
import libj.debug.Log;
import libj.utils.App;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		App.printHello();
		App.printEnvInfo();
		Debug.enable();

		try {

			// inputStream
			FileInputStream inputStream = new FileInputStream(
					"c:/tmp/input.docx");

			// pdfStream
			FileOutputStream pdfStream = new FileOutputStream(
					"c:/tmp/output.pdf");

			XWPF.convertToPDF(inputStream, pdfStream);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {

			// inputStream
			FileInputStream inputStream = new FileInputStream(
					"c:/dev/projects/IBAR/rep/PerfReport#1.xlsx");

			// dataStream
			FileInputStream dataStream = new FileInputStream(
					"c:/dev/projects/IBAR/rep/dataList.xml");

			// outputStream
			FileOutputStream outputStream = new FileOutputStream(
					"c:/tmp/output.xlsx");

			EngineJXLS jxls = new EngineJXLS(inputStream, dataStream);

			jxls.createReport(outputStream);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Log.info("<terminated>");
	}
}
