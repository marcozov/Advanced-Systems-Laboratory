package ch.ethz.operations;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.asltest.AbstractServer;

public class Get extends Operation {
	InetSocketAddress chosenServer;
	public Get(String message, Socket clientSocket, AbstractServer server) {
		super(message, clientSocket, server);
		this.chosenServer = this.getServer();
	}
	
	@Override
	public String execute() throws UnknownHostException, IOException {
		Socket serverSocket = new Socket(this.chosenServer.getAddress().getHostAddress().toString(), this.chosenServer.getPort());
		OutputStream out = new DataOutputStream(serverSocket.getOutputStream());
		out.write(this.getMessage().getBytes());
		
		OutputStream os = new DataOutputStream(this.getClient().getOutputStream());
		String reply = DataTransfer.receiveTextLine(serverSocket);
		while (true) {
			if (reply.equals("END" + '\r' + '\n')) {
				os.write(reply.getBytes());
				break;
			}
		
			parseAndCheckGetReply(reply);
			String valueRetrieved = DataTransfer.receiveUnstructuredData(serverSocket, super.getNumberOfBytes());
			os.write((reply + valueRetrieved).getBytes());

			reply = DataTransfer.receiveTextLine(serverSocket);
		}
		
		return null;
	}

	private void parseAndCheckGetReply(String reply) {
		Pattern getReplyPattern = Pattern.compile(CommandParser.getReplyRegex);
		Matcher getReplyMatcher = getReplyPattern.matcher(reply);
		while(getReplyMatcher.find()) {
			this.setNumberOfBytes(Integer.parseInt(getReplyMatcher.group(4)));
		}
	}

	@Override
	protected void parseParametersAndCheckFormat(String message) {
	}
	
	public InetSocketAddress getServer() {
		AbstractServer servers = super.getAbstractServer();
		return servers.getNextServer();
	}
}
