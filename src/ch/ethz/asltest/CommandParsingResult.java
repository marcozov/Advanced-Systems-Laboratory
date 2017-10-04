package ch.ethz.asltest;

import java.util.Arrays;
import java.util.List;

public class CommandParsingResult {
	String command;
	List<String> keys;
	Short flags = null;
	Integer exptime = null;
	Integer bytes = null;
	String noreply = null;
	String value = null;
	
	public CommandParsingResult(String command, List<String> arguments, Short flags, Integer exptime, Integer bytes, String noreply, String value) {
		this.command = command;
		this.keys = arguments;
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
}
