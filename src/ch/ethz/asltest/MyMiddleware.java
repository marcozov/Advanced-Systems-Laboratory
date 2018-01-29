package ch.ethz.asltest;

import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import ch.ethz.operations.Operation;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.logging.log4j.*;


public class MyMiddleware {
	String ip;
	int port;
	List<InetSocketAddress> mcAddresses;
	int numThreadsPTP;
	boolean readSharded;
	private static final Logger logger = LogManager.getLogger(MyMiddleware.class);
	
	public static Logger getLogger() {
		return MyMiddleware.logger;
	}
	
	public MyMiddleware(String ip, int port, List<String> mcAddresses, int numThreadsPTP, boolean readSharded) throws UnknownHostException {
		logger.error("testing log");
		logger.info("info test 2");
		this.ip = ip;
		this.port = port;
		this.mcAddresses = new ArrayList<InetSocketAddress>();
		for (String mcAddress : mcAddresses) {
			String[] parts = mcAddress.split(":");
			int portNumber = Integer.parseInt(parts[1]);
			InetAddress ipAddress = InetAddress.getByName(parts[0]);
			InetSocketAddress socket = new InetSocketAddress(ipAddress, portNumber);
			this.mcAddresses.add(socket);
		}
		this.numThreadsPTP = numThreadsPTP;
		this.readSharded = readSharded;
	}
	
	public void run() {
		ServerSocket socket = null;
		try {
			// TODO: close the socket when an interrupt signal is sent
			socket = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BlockingQueue<Operation> requests = new LinkedBlockingQueue<Operation>();
		// start the worker threads
		for (int i=0; i<this.numThreadsPTP; i++) {
			new WorkerThread(requests, this.mcAddresses, i).start();
		}

		Socket clientSocket = null;
		while (true) {
			try {
				clientSocket = socket.accept();
				
				// this is for taking all the requests from each client and put them in the queue
				// TODO: decide whether to handle this without creating different threads for each client (maybe use nio instead of io?)
				new ClientRequestsHandler(clientSocket, requests).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
