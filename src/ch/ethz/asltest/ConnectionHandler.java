package ch.ethz.asltest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import ch.ethz.operations.CommandParser;
import ch.ethz.operations.DataTransfer;
import ch.ethz.operations.Operation;

public class ConnectionHandler extends Thread{
	Socket clientSocket;
	BlockingQueue<Operation> requests;
	
	public ConnectionHandler(Socket clientSocket, BlockingQueue<Operation> requests) {
		this.clientSocket = clientSocket;
		this.requests = requests;
	}
	
	public void run() {
		CommunicationHandler client = new CommunicationHandler(this.clientSocket);
		String message = null;
		while (true) {
			try {
				//message = DataTransfer.receiveTextLine(clientSocket);
				message = DataTransfer.receiveTextLine(client);
				if(message.length() == 0) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("message to parse inside connection handler: " + message + ". clientSocket: " + clientSocket);
			// TODO: decide whether the parsing should be done here or from the worker threads (it makes sense to do it there)
			Operation operation = null;
			try {
				//operation = CommandParser.getOperation(message, clientSocket, this.servers);
				operation = CommandParser.getOperation(message, client);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.requests.add(operation);
		}
	}
}
