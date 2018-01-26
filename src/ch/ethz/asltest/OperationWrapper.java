package ch.ethz.asltest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ch.ethz.operations.CommandParser;
import ch.ethz.operations.DataTransfer;
import ch.ethz.operations.Operation;

public class OperationWrapper {
	Socket clientSocket;
	AbstractServer servers;
	
	public OperationWrapper (Socket socket, AbstractServer servers) {
		this.clientSocket = socket;
		this.servers = servers;
	}
	
	public void run() {
		String message = null;
		while (true) {
			try {
				message = DataTransfer.receiveTextLine(clientSocket);
				if(message.length() == 0) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Operation operation = CommandParser.getOperation(message, clientSocket, this.servers);			
			try {
				operation.execute();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
