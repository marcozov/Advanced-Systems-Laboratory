package ch.ethz.asltest;

import java.net.Socket;
import java.util.AbstractQueue;

// create N istances of this class at the beginning of execution
// They will be waiting for requests coming
public class WorkerThreads {
	// all the threads are going to access the same queue ==> it's a concurrent resource, everyone will simply
	// take the first available request, when it's free
	AbstractQueue<Socket> requests;
	AbstractServer servers;
	
	public WorkerThreads(AbstractQueue<Socket> requests, AbstractServer servers) {
		this.requests = requests;
		this.servers = servers;
	}
	
	public void run() {
		while (true) {
			Socket request = requests.poll();
			// actually need to wait for the end of this operation before taking a new one 
			// --> does it make sense to run another thread? NO
			// easy to change: just make sure that what is called is not a thread :)
			new OperationThread(request, this.servers).start();
		}
	}
}
