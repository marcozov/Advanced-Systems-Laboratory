package ch.ethz.asltest;

import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import ch.ethz.operations.Operation;
import ch.ethz.operations.RoundRobinToken;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.logging.log4j.*;

// run test: memtier_benchmark --protocol=memcache_text -t 4 -c 5 -s 127.0.0.1 -p 11212

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
		RoundRobinToken rrToken = new RoundRobinToken(this.mcAddresses.size());
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
				new ClientRequestsHandler(clientSocket, requests, this.readSharded).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
