package ch.ethz.operations;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ethz.asltest.SocketStreamsHandler;
import ch.ethz.operations.Operation;

public final class CommandParser {
	final static String getRegex = "^(get)\\s(([\\w-]+)(\\s\\w+)*)\r\n";
	final static String getReplyRegex = "^(VALUE)\\s([\\w-]+)\\s(\\d+)\\s(\\d+)\r\n";
	final static String setRegex = "^(set)\\s([\\w-]+)\\s(\\d+)\\s(-?\\d+)\\s(\\d+)(\\s(noreply))?\r\n";
	
	public static Operation getOperation(String message, SocketStreamsHandler client) throws IOException {
		Pattern getPattern = Pattern.compile(getRegex);
		Matcher getMatcher = getPattern.matcher(message);
		while (getMatcher.find()) {
			return new Get(message, client);
		}
		Pattern setPattern = Pattern.compile(setRegex);
		Matcher setMatcher = setPattern.matcher(message);
		while (setMatcher.find()) {
			return new Set(message, client);
		}
		return null;
	}
}
