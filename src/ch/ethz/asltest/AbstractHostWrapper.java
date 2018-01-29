package ch.ethz.asltest;

import java.net.InetSocketAddress;
import java.util.List;

public class AbstractHostWrapper {
	List<HostWrapper> servers;
	
	public AbstractHostWrapper(List<HostWrapper> servers) {
		this.servers = servers;
	}
	
	public List<HostWrapper> getAllServers() {
		return this.servers;
	}
	
	//TODO: implement round robin policy for GET operations
	// concurrency should be handled as well
	public HostWrapper getNextServer() {
		HostWrapper mcAddress = this.getAllServers().get((this.getCounter()));
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
