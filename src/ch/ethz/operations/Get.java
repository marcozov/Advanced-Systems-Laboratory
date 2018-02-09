package ch.ethz.operations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.communication.DataTransfer;
import ch.ethz.communication.HostWrapper;

public class Get extends Operation {
	public Get(String message, HostWrapper client) {
		super(message, client);
	}

	@Override
	public void execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		executeGet(servers);
	}

	public void executeGet(List<HostWrapper> servers) throws UnknownHostException, IOException {
		int serverIndex = 0;

		RoundRobinToken rrToken = RoundRobinToken.getInstance();
		serverIndex = rrToken.getValue();

		HostWrapper chosenServer = servers.get(serverIndex);
		
		sendGetRequest(chosenServer, this.getMessage());
		String fullReply = receiveGetResponse(chosenServer);

		OutputStream os = this.getClient().getOutputStream();
		os.write(fullReply.getBytes());
	}

	public void sendGetRequest(HostWrapper chosenServer, String message) throws IOException {
		OutputStream out = chosenServer.getOutputStream();
		out.write(message.getBytes());
	}

	public String receiveGetResponse(HostWrapper chosenServer) throws IOException {
		String reply = DataTransfer.receiveTextLine(chosenServer);
		String fullReply = reply;

		while (true) {
			if (reply.equals("END" + '\r' + '\n')) {
				break;
			}

			Pattern getReplyPattern = Pattern.compile(CommandParser.getReplyRegex);
			Matcher getReplyMatcher = getReplyPattern.matcher(reply);
			while(getReplyMatcher.find()) {
				super.setNumberOfBytes(Integer.parseInt(getReplyMatcher.group(4)));
			}

			String valueRetrieved = DataTransfer.receiveUnstructuredData(chosenServer, super.getNumberOfBytes());
			fullReply += valueRetrieved;

			reply = DataTransfer.receiveTextLine(chosenServer);
			fullReply += reply;
		}

		return fullReply;
	}
	
	@Override
	public String getType() {
		return "get";
	}
}
