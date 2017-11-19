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
	public Get(String message, Socket clientSocket, AbstractServer server) {
		super(message, clientSocket, server);
	}
	
	@Override
	public String execute() throws UnknownHostException, IOException {
		InetSocketAddress server = this.getServer();
		Socket serverSocket = new Socket(server.getAddress().getHostAddress().toString(), server.getPort());
		OutputStream out = new DataOutputStream(serverSocket.getOutputStream());
		out.write(this.getMessage().getBytes());
		
		OutputStream os = new DataOutputStream(this.getClient().getOutputStream());
		String reply = DataTransfer.receiveTextLine(serverSocket);
		while (true) {
			if (reply.equals("END" + '\r' + '\n')) {
				os.write(reply.getBytes());
				break;
			}
		
			//String reply = DataTransfer.receiveTextLine(serverSocket);
			parseAndCheckGetReply(reply);
			String valueRetrieved = DataTransfer.receiveUnstructuredData(serverSocket, super.getNumberOfBytes());
			os.write((reply + valueRetrieved).getBytes());

			
			//String end = DataTransfer.receiveTextLine(serverSocket);
			
			System.out.println("reply: " + reply + "len: " + reply.length());
			//System.out.println("end: " + end + "len: " + end.length());
			System.out.println("valueRetrieved: " + valueRetrieved + "len: " + valueRetrieved.length());
			System.out.println("sending back to the client: " + reply + valueRetrieved);
			//os.write((reply + valueRetrieved + end).getBytes());

			reply = DataTransfer.receiveTextLine(serverSocket);
		}
		
		return null;
	}

	private void parseAndCheckGetReply(String reply) {
		String getReplyRegex = "^(VALUE)\\s(\\w+)\\s(\\d+)\\s(\\d+)\r\n";
		Pattern getReplyPattern = Pattern.compile(getReplyRegex);
		Matcher getReplyMatcher = getReplyPattern.matcher(reply);
		while(getReplyMatcher.find()) {
			System.out.println("get reply ciao da nadia.");
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
