package ch.ethz.asltest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CommunicationHandler {
	Socket memcachedServerSocket;
	InputStream is;
	OutputStream os;
	
	public CommunicationHandler(Socket socket) {
		this.memcachedServerSocket = socket;
		try {
			this.is = socket.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.os = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream() {
		return this.is;
	}
	
	public OutputStream getOutputStream( ) {
		return this.os;
	}

	public Socket getMemcachedServerSocket() {
		return memcachedServerSocket;
	}
}