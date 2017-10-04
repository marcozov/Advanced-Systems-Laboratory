package ch.ethz.asltest;

import java.util.Arrays;
import java.util.regex.*;
import java.util.List;
import java.net.InetAddress;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;;

public class MyMiddleware {
	String ip;
	int port;
	List<String> mcAddresses;
	int numThreadsPTP;
	boolean readSharded;
	public MyMiddleware(String ip, int port, List<String> mcAddresses, int numThreadsPTP, boolean readSharded) {
		this.ip = ip;
		this.port = port;
		this.mcAddresses = mcAddresses; // servers are memcached instances
		this.numThreadsPTP = numThreadsPTP;
		this.readSharded = readSharded;
	}
	
	// current understanding: "ip" relates to where the middleware receives clients'
	// requests. Then, the mcAddresses represent the servers (ipAddress:port)
	public void run() throws IOException {
		System.out.println("hello middleware!");
		System.out.format("my network socket: %s:%d\n", ip, port);
		
		System.out.println(Arrays.toString(mcAddresses.toArray()));
		try {
			ServerSocket socket = new ServerSocket(port);
			
			while (true) {
				System.out.println("before receiving a message");

				Socket clientSocket = socket.accept();
				
				// just returns the message sent by the client
				// it simply checks whether it is a string terminated by \r\n
				String message = receiveClientMessage(clientSocket);
				// it contains the command as a string and a list containing the arguments
				// these two values are null if the format is not valid
				CommandParsingResult parsedMessage = parseCommand(message);
				String command = parsedMessage.getCommand();
				// TODO: add a library for logs (e.g. log4j)
				if (command == null) {
					System.out.println("Problem in parsing the command..");
					continue;
				}
				List<String> keys = parsedMessage.getKeys();
				if (keys == null) {
					System.out.println("Problem in parsing the arguments..");
					continue;
				}
				switch (command) {
					case "get": {
						System.out.println("This is a get!");
						String key = keys.get(0);
						String value = this.get(key);
						break;
					}
					case "set": {
						System.out.println("This is a set!");
						if (keys.size() != 1) {
							System.out.println("Too many keys for a set.." + Arrays.toString(keys.toArray()));
							continue;
						}
						String key = keys.get(0);
						Short flags = parsedMessage.getFlags();
						Integer exptime = parsedMessage.getExptime();
						int bytes = parsedMessage.getBytes();
						String noreply = parsedMessage.getNoreply();
						String value = parsedMessage.getValue();
						set(key, value);
						break; 
					}
					default:
						System.out.format("Unsupported command: %s", command);
				}

			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private CommandParsingResult parseCommand(String message) {
		// convert these to constants
		// why $ is not accepted in the regex?
		String getRegex = "^(get)\\s((\\w+)(\\s\\w+)*)\r\n";
		//String setRegex = "^(set)\\s(\\w+)\\s(\\d+)\\s(\\d+)\\s(\\d+)(\\s(noreply))?\r\n(.+)\r\n$";
		String setRegex = "^(set)\\s(\\w+)\\s(\\d+)\\s(-?\\d+)\\s(\\d+)(\\s(noreply))?\r\n(.+)";
		
		String command = null;
		List<String> keys = null;
		
		System.out.println("message to parse: " + message);
		Pattern getPattern = Pattern.compile(getRegex);
		Matcher getMatcher = getPattern.matcher(message);
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
		Matcher setMatcher = setPattern.matcher(message);
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

		CommandParsingResult parsedCommand = new CommandParsingResult(command, keys, flags, exptime, bytes, noreply, value);
		System.out.println(parsedCommand);
		return parsedCommand;
	}

	public String receiveClientMessage(Socket clientSocket) throws IOException {
		InputStream is = new DataInputStream(clientSocket.getInputStream());
		System.out.println("after receiving a message");
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		int i=0;
		while(readByte > -1) {
			b[i] = (byte)readByte;
			System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
			
			readByte = is.read();
			i++;
		}
		String message = new String(b);
		System.out.println("number of characters read: " + i); // read characters
		return message;
	}
	
	// is String the right return type?
	public String get(String key) {
		String server = this.chooseServer();
		return null;
	}
	
	// is String the right return type?
	public List<String> get(List<String> keys) {
		List<String> values = null;
		for (String key : keys) {
			String value = get(key);
			values.add(value);
		}
		return null;
	}
	
	private String chooseServer() {
		// TODO Auto-generated method stub
		return null;
	}

	public void set(String key, String value) {
		for (String server : this.mcAddresses) {
			writeValue(key, value, server);
		}
	}
	
	private void writeValue(String key, String value, String server) {
		// TODO Auto-generated method stub
		
	}
}
