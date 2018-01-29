package ch.ethz.operations;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class Set extends Operation {
	final static String REPLY = "reply";
	final static String NOREPLY = "noreply";
	String valueToWrite;
	
	//public Set(String message, Socket clientSocket, AbstractServer server) throws IOException {
	public Set(String message, CommunicationHandler client) throws IOException {
		//super(message, clientSocket, server);
		super(message, client);
		parseParametersAndCheckFormat(message);
		this.valueToWrite = DataTransfer.receiveUnstructuredData(this.getClient(), this.getNumberOfBytes());
		System.out.println("value to write: " + this.valueToWrite);
	}

	@Override
	protected void parseParametersAndCheckFormat(String message) {
		String setRegex = CommandParser.setRegex;
		Pattern setPattern = Pattern.compile(setRegex);
		Matcher setMatcher = setPattern.matcher(message);

		while (setMatcher.find()) {
			//System.out.println("set match!");
			// handle set
			Integer bytes = Integer.parseInt(setMatcher.group(5));
			Boolean noreply = setMatcher.group(7) != null && setMatcher.group(7).equals(NOREPLY);
				
			super.setNumberOfBytes(bytes);
			this.setIsReplyExpected(!noreply);
		}
	}

	@Override
	public String execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		//String fullCommand = super.getMessage() + this.valueToWrite + '\r' + '\n';
		String fullCommand = super.getMessage() + this.valueToWrite;
		//System.out.println("fullCommand: " + fullCommand);
		//Socket clientSocket = super.getClient();
		CommunicationHandler client = super.getClient();
		//List<InetSocketAddress> servers = this.getServers();
		boolean replyExpected = this.isReplyExpected();
		String result = null;
		//for (InetSocketAddress server : servers) {
		for (HostWrapper server : servers) {
			//result = writeValue(fullCommand, server);
			result = writeValue(fullCommand, server.getCh());
			if (result != null && isErrorMessage(result)) {
				break;
			}
		}
		
		if (replyExpected && result != null) {
			sendResult(result, client);
		}
		
		return null;
	}

	private boolean isErrorMessage(String result) {
		String STOREDregex = "^STORED\r\n";
		//String NOT_STOREDregex = "^NOT_STORED\r\n";
		
		Pattern STOREDpattern = Pattern.compile(STOREDregex);
		Matcher STOREDmatcher = STOREDpattern.matcher(result);
		
		if (result == null) {
			return false;
		}
		
		while (STOREDmatcher.find()) {
			return false;
		}
		
		return true;
	}

	//public List<InetSocketAddress> getServers() {
	//	AbstractServer servers = super.getAbstractServer();
	//	return servers.getAllServers();
	//}
	
	private void setIsReplyExpected(boolean reply) {
		super.setParameter(REPLY, reply);
	}
	
	private Boolean isReplyExpected() {
		return (Boolean) super.getParameter(REPLY);
	}
	
	//private void sendResult(String result, Socket clientSocket) throws IOException {
	private void sendResult(String result, CommunicationHandler client) throws IOException {
		//OutputStream os = new DataOutputStream(clientSocket.getOutputStream());
		OutputStream os = client.getOutputStream();
		os.write(result.getBytes());		
	}
	
	//private String writeValue(String message, InetSocketAddress server) throws UnknownHostException, IOException {
	private String writeValue(String message, CommunicationHandler server) throws UnknownHostException, IOException {
		//Socket kkSocket = new Socket(server.getAddress().getHostAddress().toString(), server.getPort());
		//OutputStream out = new DataOutputStream(kkSocket.getOutputStream());
		OutputStream out = server.getOutputStream();
		out.write(message.getBytes());
		String reply = null;
		if (isReplyExpected()) {
			//reply = DataTransfer.receiveTextLine(kkSocket);
			reply = DataTransfer.receiveTextLine(server);
		}
		return reply;
	}
}
