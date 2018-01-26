package ch.ethz.asltest;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
//import java.util.AbstractQueue;

// create N istances of this class at the beginning of execution
// They will be waiting for requests coming
public class WorkerThread extends Thread {
	// all the threads are going to access the same queue ==> it's a concurrent resource, everyone will simply
	// take the first available request, when it's free
	BlockingQueue<Socket> requests;
	AbstractServer servers;
	int idThread;
	
	public WorkerThread(BlockingQueue<Socket> requests, AbstractServer servers, int id) {
		this.requests = requests;
		this.servers = servers;
		this.idThread = id;
	}
	
	public void run() {
		while (true) {
			// wait until a request is available
			Socket request = null;
			try {
				request = requests.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("request that will be fulfilled: " + request + ". Thread: " + this.idThread);
			new OperationWrapper(request, this.servers).run();
		}
	}
}
