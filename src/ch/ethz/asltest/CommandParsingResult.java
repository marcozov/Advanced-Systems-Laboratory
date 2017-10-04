package ch.ethz.asltest;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParsingResult {
	String command;
	List<String> keys;
	Short flags = null;
	Integer exptime = null;
	Integer bytes = null;
	String noreply = null;
	String value = null;
	
	public CommandParsingResult(String fullCommand) {
		// convert these to constants
		// why $ is not accepted in the regex?
		String getRegex = "^(get)\\s((\\w+)(\\s\\w+)*)\r\n";
		String setRegex = "^(set)\\s(\\w+)\\s(\\d+)\\s(-?\\d+)\\s(\\d+)(\\s(noreply))?\r\n(.+)";
				
		String command = null;
		List<String> keys = null;
			
		System.out.println("message to parse: " + fullCommand);
		Pattern getPattern = Pattern.compile(getRegex);
		Matcher getMatcher = getPattern.matcher(fullCommand);
		while (getMatcher.find()) {
			System.out.println("get match!");
			// handle get
			command = getMatcher.group(1);
			String argumentsParsed = getMatcher.group(2);
			keys = Arrays.asList(argumentsParsed.split("\\s"));
		} 
		
		Short flags = null;
		Integer exptime = null;
		Integer bytes = null;
		String noreply = null;
		String value = null;
		Pattern setPattern = Pattern.compile(setRegex);
		Matcher setMatcher = setPattern.matcher(fullCommand);
		while (setMatcher.find()) {
			System.out.println("set match!");
			// handle set
			command = setMatcher.group(1);
			keys = Arrays.asList(setMatcher.group(2));
			flags = Short.parseShort(setMatcher.group(3));
			exptime = Integer.parseInt(setMatcher.group(4));
			bytes = Integer.parseInt(setMatcher.group(5));
			noreply = setMatcher.group(7);
				
			value = setMatcher.group(8);
		}

		this.command = command;
		this.keys = keys;
		this.flags = flags;
		this.exptime = exptime;
		this.bytes = bytes;
		this.noreply = noreply;
		this.value = value;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public List<String> getKeys() {
		return this.keys;
	}
	
	public Short getFlags() {
		return flags;
	}

	public Integer getExptime() {
		return exptime;
	}

	public Integer getBytes() {
		return bytes;
	}

	public String getNoreply() {
		return noreply;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		String result = "Command: " + this.command + "\nkeys: ";
		if (this.getKeys() != null) {
			result = result + Arrays.toString(this.getKeys().toArray());
		} else {
			result = result + "null";
		}
		result = result + "\nFlags: " + this.getFlags() + "\nExpire time: " + this.getExptime() + "\nBytes: " + this.getBytes() + "\nNoreply: " + this.getNoreply() + "\nValue: " + this.getValue();
		return result;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
