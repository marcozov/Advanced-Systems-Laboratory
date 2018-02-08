package ch.ethz.asl;

import java.net.InetSocketAddress;

public class HostWrapper {
	InetSocketAddress hostAddress;
	SocketStreamsHandler ch;
	
	public HostWrapper(InetSocketAddress hostAddress, SocketStreamsHandler ch) {
		this.hostAddress = hostAddress;
		this.ch = ch;
	}

	public InetSocketAddress getHostAddress() {
		return hostAddress;
	}

	public SocketStreamsHandler getCh() {
		return ch;
	}
	
	
}
