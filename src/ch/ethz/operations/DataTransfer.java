package ch.ethz.operations;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import ch.ethz.asltest.CommunicationHandler;

public final class DataTransfer {
	//public static String receiveTextLine(Socket socket) throws IOException {
	public static String receiveTextLine(CommunicationHandler ch) throws IOException {
		
		
		//InputStream is = new DataInputStream(socket.getInputStream());
		
		//InputStream is = socket.getInputStream();
		InputStream is = ch.getInputStream();
		
		System.out.println("input stream: " + is);
		//System.out.println("after receiving a message - wat");
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		System.out.println("wat");
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
					i++;
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
		String message = new String(b, 0, i);
		System.out.println("number of characters read: " + i); // read characters
		System.out.println("message: " + message);
		return message;
	}
	
	//public static String receiveUnstructuredData(Socket socket, int len) throws IOException {
	public static String receiveUnstructuredData(CommunicationHandler ch, int len) throws IOException {
		//System.out.println("receiving unstructured data");
		//InputStream is = new DataInputStream(socket.getInputStream());
		//InputStream is = socket.getInputStream();
		InputStream is = ch.getInputStream();
		byte[] b = new byte[4096];
		
		is.read(b, 0, len+2);
		return new String(b, 0, len+2);
	}
}
