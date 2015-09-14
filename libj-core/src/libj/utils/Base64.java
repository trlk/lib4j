package libj.utils;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64 {

	public static byte[] decode(String text) {
		return DatatypeConverter.parseBase64Binary(text);
	}

	public static String encode(byte[] binary) {
		return DatatypeConverter.printBase64Binary(binary);
	}

	public static ByteArrayInputStream decodeToStream(String text) {
		return new ByteArrayInputStream(decode(text));
	}

	public static String encodeFromStream(ByteArrayOutputStream stream) {

		return encode(stream.toByteArray());
	}

}
