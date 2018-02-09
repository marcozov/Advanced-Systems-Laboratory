package ch.ethz.operations;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import ch.ethz.communication.HostWrapper;

public class MultiGetNormal extends Get {

	public MultiGetNormal(String message, HostWrapper client) {
		super(message, client);
	}

	@Override
	public void execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		super.executeGet(servers);		
	}
	
	@Override
	public String getType() {
		return "multiget";
	}
}
