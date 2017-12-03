package ch.ethz.asltest;

import java.net.InetSocketAddress;
import java.util.List;

public class AbstractServer {
	List<InetSocketAddress> mcAddresses;
	private int getCounter;
	
	public AbstractServer(List<InetSocketAddress> mcAddresses) {
		this.mcAddresses = mcAddresses;
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
		if (this.getCounter < this.getAllServers().size() - 1) {
			this.getCounter = this.getCounter + 1;
		} else {
			this.getCounter = 0;
		}
		return this.getCounter;
	}
}
