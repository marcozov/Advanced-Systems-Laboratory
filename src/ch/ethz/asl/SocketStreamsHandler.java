package ch.ethz.asl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketStreamsHandler {
	Socket hostSocket;
	InputStream is;
	OutputStream os;
	
	public SocketStreamsHandler(Socket socket) {
		this.hostSocket = socket;
		try {
			this.is = socket.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			this.os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputStream getInputStream() {
		return this.is;
	}
	
	public OutputStream getOutputStream( ) {
		return this.os;
	}

	public Socket getHostSocket() {
		return hostSocket;
	}
}