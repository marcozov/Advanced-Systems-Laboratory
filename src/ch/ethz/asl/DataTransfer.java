package ch.ethz.asl;

import java.io.IOException;
import java.io.InputStream;

public final class DataTransfer {
	public static String receiveTextLine(SocketStreamsHandler ch) throws IOException {
		InputStream is = ch.getInputStream();	
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		int i=0;
		while(readByte > -1) {
			if (readByte == '\r') {
				b[i] = (byte)readByte;
				readByte = is.read();
				i++;
				if (readByte == '\n') {
					b[i] = (byte)readByte;
					i++;
					break;
				} else {
					continue;
				}
			}
			
			b[i] = (byte)readByte;
			readByte = is.read();
			i++;
		}
		String message = new String(b, 0, i);
		return message;
	}
	
	public static String receiveUnstructuredData(SocketStreamsHandler ch, int len) throws IOException {
		InputStream is = ch.getInputStream();
		byte[] b = new byte[4096];
		
		is.read(b, 0, len+2);
		return new String(b, 0, len+2);
	}
}
