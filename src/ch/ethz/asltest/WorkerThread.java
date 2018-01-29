package ch.ethz.asltest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
//import java.util.AbstractQueue;

import ch.ethz.operations.Operation;

// create N istances of this class at the beginning of execution
// They will be waiting for requests coming
public class WorkerThread extends Thread {
	// all the threads are going to access the same queue ==> it's a concurrent resource, everyone will simply
	// take the first available request, when it's free
	BlockingQueue<Operation> requests;
	List<HostWrapper> servers;
	int idThread;
	
	public WorkerThread(BlockingQueue<Operation> requests, List<InetSocketAddress> servers, int id) {
		this.requests = requests;
		//this.servers = servers;
		this.idThread = id;
		this.servers = new ArrayList<HostWrapper>();
		
		Socket memcachedServerSocket = null;
		// the connection with the memcached servers should be opened here
		for (InetSocketAddress memcachedServerAddress : servers) {
			try {
				memcachedServerSocket = new Socket(memcachedServerAddress.getAddress().getHostAddress().toString(), memcachedServerAddress.getPort());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			SocketStreamsHandler ch = new SocketStreamsHandler(memcachedServerSocket);
			HostWrapper server = new HostWrapper(memcachedServerAddress, ch);
			this.servers.add(server);
		}
	}
	
	public void run() {
		while (true) {
			Operation request = null;
			try {
				// wait until a request is available
				request = requests.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				request.execute(this.servers);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
