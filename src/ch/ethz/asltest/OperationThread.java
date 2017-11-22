package ch.ethz.asltest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ch.ethz.operations.CommandParser;
import ch.ethz.operations.DataTransfer;
import ch.ethz.operations.Operation;

public class OperationThread extends Thread {	
	Socket clientSocket;
	AbstractServer servers;
	
	public OperationThread (Socket socket, AbstractServer servers) {
		this.clientSocket = socket;
		this.servers = servers;
	}
	
	public void run() {
		String message = null;
		try {
			message = DataTransfer.receiveTextLine(clientSocket);
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
