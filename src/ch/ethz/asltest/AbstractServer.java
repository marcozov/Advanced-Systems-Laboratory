package ch.ethz.asltest;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractServer {
	List<InetSocketAddress> mcAddresses;
	private AtomicInteger getCounter;
	
	public AbstractServer(List<InetSocketAddress> mcAddresses) {
		this.mcAddresses = mcAddresses;
		this.getCounter = new AtomicInteger(0);
	}
	
	public List<InetSocketAddress> getAllServers() {
		return this.mcAddresses;
	}
	
	//TODO: implement round robin policy for GET operations
	// concurrency should be handled as well
	public InetSocketAddress getNextServer() {
		InetSocketAddress mcAddress = this.getAllServers().get((this.getCounter()));
		return mcAddress;
	}

	// round-robin load balancer for servers
	private int getCounter() {
		return 1;
		/*
		if (this.getCounter.get() < this.getAllServers().size() - 1) {
			//this.getCounter.set(this.getCounter.get() + 1);
			return this.getCounter.addAndGet(1);
		} else {
			this.getCounter.set(0);
			//return this.getCounter.
		}
		return this.getCounter.get();
		*/
	}
}
