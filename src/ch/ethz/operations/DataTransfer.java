package ch.ethz.operations;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public final class DataTransfer {
	public static String receiveTextLine(Socket socket) throws IOException {
		InputStream is = new DataInputStream(socket.getInputStream());
		System.out.println("after receiving a message");
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		int i=0;
		while(readByte > -1) {
			if (readByte == '\r') {
				System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
				b[i] = (byte)readByte;
				readByte = is.read();
				i++;
				if (readByte == '\n') {
					System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
					b[i] = (byte)readByte;
					break;
				} else {
					continue;
				}
			}
			
			b[i] = (byte)readByte;
			System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
			
			readByte = is.read();
			i++;
		}
		String message = new String(b, 0, i+1);
		System.out.println("number of characters read: " + i); // read characters
		return message;
	}
	
	public static String receiveUnstructuredData(Socket socket, int len) throws IOException {
		System.out.println("receiving unstructured data");
		InputStream is = new DataInputStream(socket.getInputStream());
		byte[] b = new byte[4096];
		
		is.read(b, 0, len);
		return new String(b, 0, len);
	}
}
