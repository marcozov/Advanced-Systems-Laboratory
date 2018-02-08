package ch.ethz.operations;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import ch.ethz.asl.HostWrapper;
import ch.ethz.asl.SocketStreamsHandler;

public class MultiGetNormal extends Get {

	public MultiGetNormal(String message, SocketStreamsHandler client) {
		super(message, client);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		// can update the right counter
		super.executeGet(servers);		
	}
	
	@Override
	public String getType() {
		return "multiget";
	}
}
