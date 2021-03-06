package ch.ethz.operations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.communication.DataTransfer;
import ch.ethz.communication.HostWrapper;

public class Set extends Operation {
	final static String REPLY = "reply";
	final static String NOREPLY = "noreply";
	String valueToWrite;
	
	public Set(String message, HostWrapper client) throws IOException {
		super(message, client);
		String setRegex = CommandParser.setRegex;
		Pattern setPattern = Pattern.compile(setRegex);
		Matcher setMatcher = setPattern.matcher(message);

		while (setMatcher.find()) {
			Integer bytes = Integer.parseInt(setMatcher.group(5));
			Boolean noreply = setMatcher.group(7) != null && setMatcher.group(7).equals(NOREPLY);
			super.setNumberOfBytes(bytes);
			this.setIsReplyExpected(!noreply);
		}
		
		this.valueToWrite = DataTransfer.receiveUnstructuredData(this.getClient(), this.getNumberOfBytes());
	}

	@Override
	public void execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		String fullCommand = super.getMessage() + this.valueToWrite;
		HostWrapper client = super.getClient();
		boolean replyExpected = this.isReplyExpected();
		String result = null;
		for (HostWrapper server : servers) {
			OutputStream out = server.getOutputStream();
			out.write(fullCommand.getBytes());
			if (isReplyExpected()) {
				result = DataTransfer.receiveTextLine(server);
			}
			if (result != null && isErrorMessage(result)) {
				break;
			}
		}
		
		if (replyExpected && result != null) {
			OutputStream os = client.getOutputStream();
			os.write(result.getBytes());
		}
	}

	private boolean isErrorMessage(String result) {
		String STOREDregex = "^STORED\r\n";
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

	private void setIsReplyExpected(boolean reply) {
		super.setParameter(REPLY, reply);
	}
	
	private Boolean isReplyExpected() {
		return (Boolean) super.getParameter(REPLY);
	}
	
	@Override
	public String getType() {
		return "set";
	}
}
