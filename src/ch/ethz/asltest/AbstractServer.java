package ch.ethz.asltest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractServer {
	//List<InetSocketAddress> mcAddresses;
	List<HostWrapper> servers;
	private AtomicInteger getCounter;
	
	public AbstractServer(List<HostWrapper> servers) {
		this.servers = servers;
		this.getCounter = new AtomicInteger(0);
	}
	
	//public List<InetSocketAddress> getAllServers() {
	public List<HostWrapper> getAllServers() {
		return this.servers;
	}
	
	//TODO: implement round robin policy for GET operations
	// concurrency should be handled as well
	//public InetSocketAddress getNextServer() {
	public HostWrapper getNextServer() {
		HostWrapper server = this.getAllServers().get((this.getCounter()));
		return server;
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
