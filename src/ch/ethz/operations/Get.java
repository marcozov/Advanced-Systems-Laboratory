package ch.ethz.operations;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.asltest.AbstractServer;
import ch.ethz.asltest.CommunicationHandler;
import ch.ethz.asltest.HostWrapper;

public class Get extends Operation {
	//InetSocketAddress chosenServer;
	//HostWrapper chosenServer;
	//public Get(String message, Socket clientSocket, AbstractServer server) {
	public Get(String message, CommunicationHandler client) {
		super(message, client);
		//this.chosenServer = this.getServer();
	}
	
	@Override
	public String execute(List<HostWrapper> servers) throws UnknownHostException, IOException {

		//Socket serverSocket = new Socket(this.chosenServer.getAddress().getHostAddress().toString(), this.chosenServer.getPort());
		//OutputStream out = new DataOutputStream(serverSocket.getOutputStream());
		HostWrapper chosenServer = servers.get(0);
		
		OutputStream out = chosenServer.getCh().getOutputStream();
		out.write(this.getMessage().getBytes());
		
		System.out.println("execution at middle1111111111");
		//OutputStream os = new DataOutputStream(this.getClient().getOutputStream());
		OutputStream os = this.getClient().getOutputStream();
		//System.out.println("just before reading: " + serverSocket);
		//String reply = DataTransfer.receiveTextLine(serverSocket);
		String reply = DataTransfer.receiveTextLine(chosenServer.getCh());
		System.out.println("execution at middle222");
		System.out.println("message: " + this.getMessage() + ". hex: " + String.format("%x", new BigInteger(1, this.getMessage().getBytes())) + " reply: " + reply + " server socket: " + chosenServer.getCh().getMemcachedServerSocket() + " client socket: " + this.client.getMemcachedServerSocket());

		while (true) {
			if (reply.equals("END" + '\r' + '\n')) {
				os.write(reply.getBytes());
				break;
			}

			parseAndCheckGetReply(reply);
			//String valueRetrieved = DataTransfer.receiveUnstructuredData(serverSocket, super.getNumberOfBytes());
			System.out.println("ch: " + chosenServer.getCh());
			System.out.println("address: " + chosenServer.getHostAddress());
			System.out.println("bytes: " + super.getNumberOfBytes());
			String valueRetrieved = DataTransfer.receiveUnstructuredData(chosenServer.getCh(), super.getNumberOfBytes());
			os.write((reply + valueRetrieved).getBytes());

			//reply = DataTransfer.receiveTextLine(serverSocket);
			reply = DataTransfer.receiveTextLine(chosenServer.getCh());
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
	
	//public InetSocketAddress getServer() {
	//	AbstractServer servers = super.getAbstractServer();
	//	return servers.getNextServer();
	//}
}
