package ch.ethz.asltest;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.operations.CommandParser;
import ch.ethz.operations.Operation;

// this class takes care of putting all the requests of a client in a queue
public class ClientRequestsHandler extends Thread {
	Socket clientSocket;
	BlockingQueue<Operation> requests;
	
	public ClientRequestsHandler(Socket clientSocket, BlockingQueue<Operation> requests) {
		this.clientSocket = clientSocket;
		this.requests = requests;
	}
	
	public void run() {
		AtomicInteger roundRobinToken = new AtomicInteger(0);
		SocketStreamsHandler client = new SocketStreamsHandler(this.clientSocket);
		String message = null;
		while (true) {
			try {
				message = DataTransfer.receiveTextLine(client);
				if(message.length() == 0) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Operation operation = null;
			try {
				operation = CommandParser.getOperation(message, client);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.requests.add(operation);
		}
	}
}
