package ch.ethz.main;

import java.util.concurrent.BlockingQueue;
import java.util.ArrayList;
import java.util.HashMap;

import ch.ethz.measures.QueueLengthMeasurer;
import ch.ethz.operations.Operation;
import ch.ethz.operations.RoundRobinToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
		logger.error("testing log 1");
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
		List<WorkerThread> workerThreads = new ArrayList<WorkerThread>();
		// start the worker threads
		for (int i=0; i<this.numThreadsPTP; i++) {
			WorkerThread workerThread = new WorkerThread(requests, this.mcAddresses, i);
			workerThreads.add(workerThread);
			workerThread.start();
		}

		Socket clientSocket = null;
		boolean measuringQueueLength = false;
		while (true) {
			try {
				// the beginning of the experiment is triggered
				clientSocket = socket.accept();
				if (!measuringQueueLength) {
					final long experimentStart = System.nanoTime();
					measuringQueueLength = true;
					QueueLengthMeasurer measurer = new QueueLengthMeasurer(requests);
					measurer.start();
					
					Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                            shutdown();
                        }

                        private void shutdown() {
                        	Map<String, int[]> operationsCounter = new HashMap<String, int[]>();
                        	operationsCounter.put("get", new int[] { 0 });
                        	operationsCounter.put("set", new int[] { 0 });
                        	operationsCounter.put("multiget", new int[] { 0 });
                        	
                        	long experimentDuration = System.nanoTime() - experimentStart;
                            long totalWaitingTime = 0;
                            long totalServiceTime = 0;
                            long completedOperations = 0;
                            for (WorkerThread workerThread : workerThreads) {
                            	for(Operation operation : workerThread.getCompletedOperations()) {
                            		totalWaitingTime += operation.getWaitingTime();
                            		totalServiceTime += operation.getServiceTime();
                            		completedOperations++;
                            		operationsCounter.get(operation.getType())[0]++;
                            	}
                            }
                            
                            System.out.println("Average queue length: " + measurer.getAverageQueueLength());
                            System.out.println("Average waiting time: " + totalWaitingTime/completedOperations);
                            System.out.println("Average service time: " + totalServiceTime/completedOperations);
                            System.out.println("Average throughput: " + (double)completedOperations/(double)experimentDuration + " operations/timeUnit");
                            System.out.println("Total number of GET operations: " + operationsCounter.get("get")[0]);
                            System.out.println("Total number of SET operations: " + operationsCounter.get("set")[0]);
                            System.out.println("Total number of MULTI-GET operations: " + operationsCounter.get("multiget")[0]);
                        }
					});
				}
				new ClientRequestsHandler(clientSocket, requests, this.readSharded).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
