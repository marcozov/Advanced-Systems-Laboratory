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

public class Set extends Operation {
	final static String REPLY = "reply";
	final static String NOREPLY = "noreply";
	public Set(String message, Socket clientSocket, AbstractServer server) {
		super(message, clientSocket, server);
		parseParametersAndCheckFormat(message);
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
	public String execute() throws UnknownHostException, IOException {
		String valueToWrite = DataTransfer.receiveUnstructuredData(this.getClient(), this.getNumberOfBytes());
		String fullCommand = super.getMessage() + valueToWrite + '\r' + '\n';
		//System.out.println("fullCommand: " + fullCommand);
		Socket clientSocket = super.getClient();
		List<InetSocketAddress> servers = this.getServers();
		boolean replyExpected = this.isReplyExpected();
		String result = null;
		for (InetSocketAddress server : servers) {
			result = writeValue(fullCommand, server);
			if (result != null && isErrorMessage(result)) {
				break;
			}
		}
		
		if (replyExpected && result != null) {
			sendResult(result, clientSocket);
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

	public List<InetSocketAddress> getServers() {
		AbstractServer servers = super.getAbstractServer();
		return servers.getAllServers();
	}
	
	private void setIsReplyExpected(boolean reply) {
		super.setParameter(REPLY, reply);
	}
	
	private Boolean isReplyExpected() {
		return (Boolean) super.getParameter(REPLY);
	}
	
	private void sendResult(String result, Socket clientSocket) throws IOException {
		OutputStream os = new DataOutputStream(clientSocket.getOutputStream());
		os.write(result.getBytes());		
	}
	
	private String writeValue(String message, InetSocketAddress server) throws UnknownHostException, IOException {
		Socket kkSocket = new Socket(server.getAddress().getHostAddress().toString(), server.getPort());
		OutputStream out = new DataOutputStream(kkSocket.getOutputStream());
		out.write(message.getBytes());
		String reply = null;
		if (isReplyExpected()) {
			reply = DataTransfer.receiveTextLine(kkSocket);
		}
		return reply;
	}
}
