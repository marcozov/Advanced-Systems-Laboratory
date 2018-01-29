package ch.ethz.asltest;

import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

import ch.ethz.operations.CommandParser;
import ch.ethz.operations.DataTransfer;
import ch.ethz.operations.Operation;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.apache.logging.log4j.*;


public class MyMiddleware {
	String ip;
	int port;
	List<InetSocketAddress> mcAddresses;
	//AbstractServer servers;
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
			System.out.println("Sending stuff to " + socket.getAddress().getHostAddress().toString() + ":" + socket.getPort());
		}
		//this.servers = new AbstractServer(this.mcAddresses);
		this.numThreadsPTP = numThreadsPTP;
		this.readSharded = readSharded;
	}
	
	public void run() {
		// TODO: add a library for logs (e.g. log4j) and replace all the useful prints with log statements
		System.out.println("hello middleware! (new!)");
		System.out.format("my network socket: %s:%d\n", ip, port);
		
		System.out.println(Arrays.toString(mcAddresses.toArray()));
		ServerSocket socket = null;
		try {
			// TODO: close the socket when an interrupt signal is sent
			socket = new ServerSocket(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
				System.out.println("before socket.accept");
				clientSocket = socket.accept();
				
				// this is for taking all the requests from each client and put them in the queue
				//new ConnectionHandler(clientSocket, requests, this.servers).start();
				new ConnectionHandler(clientSocket, requests).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
