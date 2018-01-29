package ch.ethz.operations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.asltest.SocketStreamsHandler;
import ch.ethz.asltest.DataTransfer;
import ch.ethz.asltest.HostWrapper;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Get extends Operation {
	public Get(String message, SocketStreamsHandler client) {
		super(message, client);
	}
	
	@Override
	public String execute(List<HostWrapper> servers) throws UnknownHostException, IOException {
		int serverIndex = 0;

		Lock lock = new ReentrantLock();
		lock.lock();
		RoundRobinToken rrToken = RoundRobinToken.getInstance();
		if (rrToken.getValue() < rrToken.getSize() - 1) {
			rrToken.setValue(rrToken.getValue()+1);
		} else {
			rrToken.setValue(0);
		}
		serverIndex = rrToken.getValue();
		lock.unlock();
		if(serverIndex >= rrToken.getSize()) {
			serverIndex = 0;
		}
		HostWrapper chosenServer = servers.get(serverIndex);
		
		OutputStream out = chosenServer.getCh().getOutputStream();
		out.write(this.getMessage().getBytes());

		OutputStream os = this.getClient().getOutputStream();
		String reply = DataTransfer.receiveTextLine(chosenServer.getCh());

		while (true) {
			if (reply.equals("END" + '\r' + '\n')) {
				os.write(reply.getBytes());
				break;
			}

			Pattern getReplyPattern = Pattern.compile(CommandParser.getReplyRegex);
			Matcher getReplyMatcher = getReplyPattern.matcher(reply);
			while(getReplyMatcher.find()) {
				super.setNumberOfBytes(Integer.parseInt(getReplyMatcher.group(4)));
			}

			String valueRetrieved = DataTransfer.receiveUnstructuredData(chosenServer.getCh(), super.getNumberOfBytes());
			os.write((reply + valueRetrieved).getBytes());

			reply = DataTransfer.receiveTextLine(chosenServer.getCh());
		}
		
		return null;
	}
}
