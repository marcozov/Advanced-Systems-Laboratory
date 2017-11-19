package ch.ethz.asltest;

import java.net.InetSocketAddress;
import java.util.List;

public class AbstractServer {
	List<InetSocketAddress> mcAddresses;
	
	public AbstractServer(List<InetSocketAddress> mcAddresses) {
		this.mcAddresses = mcAddresses;
	}
	
	public List<InetSocketAddress> getAllServers() {
		return this.mcAddresses;
	}
	
	//TODO: implement round robin policy for GET operations
	// concurrency should be handled as well
	public InetSocketAddress getNextServer() {
		return this.mcAddresses.get(0);
	}
}
