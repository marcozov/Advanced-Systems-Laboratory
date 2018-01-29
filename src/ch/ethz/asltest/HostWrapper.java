package ch.ethz.asltest;

import java.net.InetSocketAddress;

public class HostWrapper {
	InetSocketAddress hostAddress;
	CommunicationHandler ch;
	
	public HostWrapper(InetSocketAddress hostAddress, CommunicationHandler ch) {
		this.hostAddress = hostAddress;
		this.ch = ch;
	}

	public InetSocketAddress getHostAddress() {
		return hostAddress;
	}

	public CommunicationHandler getCh() {
		return ch;
	}
	
	
}
