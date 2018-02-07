package ch.ethz.operations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.asltest.HostWrapper;
import ch.ethz.asltest.SocketStreamsHandler;

public class MultiGetSharded extends Get {

	public MultiGetSharded(String message, SocketStreamsHandler client) {
		super(message, client);
	}

	@Override
	public void execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		// can update the right counter
		
		String message = this.getMessage().substring(4, this.getMessage().length()-2);
		String[] keys = message.split(" ");
		
		Map<HostWrapper, List<String>> getTuple = new HashMap<HostWrapper, List<String>>();
		
		for (HostWrapper server : servers) {
			getTuple.put(server, new ArrayList<String>());
		}
		
		int serverIndex = 0;
		for (String key : keys) {
			HostWrapper server = servers.get(serverIndex % servers.size());
			List<String> keysPerServer = getTuple.get(server);
			keysPerServer.add(key);
			
			serverIndex++;
		}
		
		// send get request
		List<HostWrapper> filteredServers = new ArrayList<HostWrapper>();
		for (HostWrapper server : servers) {
			List<String> keysPerServer = getTuple.get(server);
			if (keysPerServer.size() == 0) {
				continue;
			}
			filteredServers.add(server);
			String getRequest = "get " + String.join(" ", keysPerServer) + '\r' + '\n';
			super.sendGetRequest(server, getRequest);
		}

		// try to read the answers
		OutputStream os = this.getClient().getOutputStream();
		String fullReply = "";
		for (HostWrapper server : filteredServers) {
			String serverReply = super.receiveGetResponse(server);
			fullReply += serverReply.substring(0, serverReply.length()-5);
		}
		fullReply += "END" + '\r' + '\n';
		os.write(fullReply.getBytes());
	}
}
