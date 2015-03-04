package libj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Stream {

	public static byte[] getBytes(ByteArrayOutputStream stream) {
		return stream.toByteArray();
	}

	public static byte[] getBytes(ByteArrayInputStream stream)
			throws IOException {

		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);

		return bytes;

	}

	public static ByteArrayInputStream newInputStream(byte[] bytes) {
		return new ByteArrayInputStream(bytes);
	}

	public static ByteArrayInputStream newInputStream(
			ByteArrayOutputStream outputStream) {

		return newInputStream(outputStream.toByteArray());
	}

}
