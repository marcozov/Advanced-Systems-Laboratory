package ch.ethz.main;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import ch.ethz.communication.DataTransfer;
import ch.ethz.communication.HostWrapper;
import ch.ethz.operations.CommandParser;
import ch.ethz.operations.Operation;

// this class takes care of putting all the requests of a client in a queue
public class ClientRequestsHandler extends Thread {
	Socket clientSocket;
	BlockingQueue<Operation> requests;
	boolean readSharded;
	
	public ClientRequestsHandler(Socket clientSocket, BlockingQueue<Operation> requests, boolean readSharded) {
		this.clientSocket = clientSocket;
		this.requests = requests;
		this.readSharded = readSharded;
	}
	
	public void run() {
		HostWrapper client = new HostWrapper(this.clientSocket);
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
				operation = CommandParser.getOperation(message, client, readSharded);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// 3: start timer for waiting time in the queue 
			operation.startWaitingTimer();
			this.requests.add(operation);
		}
	}
}
