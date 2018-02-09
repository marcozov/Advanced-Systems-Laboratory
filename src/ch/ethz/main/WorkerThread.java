package ch.ethz.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import ch.ethz.communication.HostWrapper;
import ch.ethz.operations.Operation;

// create N istances of this class at the beginning of execution
// They will be waiting for requests coming
public class WorkerThread extends Thread {
	// all the threads are going to access the same queue ==> it's a concurrent resource, everyone will simply
	// take the first available request, when it's free
	BlockingQueue<Operation> requests;
	List<HostWrapper> servers;
	int idThread;
	List<Operation> completedOperations;

	public WorkerThread(BlockingQueue<Operation> requests, List<InetSocketAddress> serverAddresses, int id) {
		this.requests = requests;
		this.idThread = id;
		this.servers = new ArrayList<HostWrapper>();
		this.completedOperations = new ArrayList<Operation>();

		Socket memcachedServerSocket = null;
		// the connection with the memcached servers should be opened here
		for (InetSocketAddress memcachedServerAddress : serverAddresses) {
			try {
				memcachedServerSocket = new Socket(memcachedServerAddress.getAddress().getHostAddress().toString(), memcachedServerAddress.getPort());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			HostWrapper server = new HostWrapper(memcachedServerSocket);
			this.servers.add(server);
		}
	}

	public void run() {
		while (true) {
			Operation request = null;
			try {
				// wait until a request is available
				request = requests.take();
				// 3: stop timer for waiting time in the queue
				request.stopWaitingTimer();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				// 4: start timer for service time of memcached servers
				request.startServiceTimer();
				request.execute(this.servers);
				// 4: stop timer for service time of memcached servers
				request.stopServiceTimer();
				
				this.completedOperations.add(request);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Operation> getCompletedOperations() {
		return this.completedOperations;
	}
}
