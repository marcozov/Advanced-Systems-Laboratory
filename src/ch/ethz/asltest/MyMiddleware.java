package ch.ethz.asltest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;
import java.util.List;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;;

public class MyMiddleware {
	String ip;
	int port;
	List<InetSocketAddress> mcAddresses;
	int numThreadsPTP;
	boolean readSharded;
	public MyMiddleware(String ip, int port, List<String> mcAddresses, int numThreadsPTP, boolean readSharded) throws UnknownHostException {
		this.ip = ip;
		this.port = port;
		this.mcAddresses = new ArrayList<InetSocketAddress>();
		for (String mcAddress : mcAddresses) {
			String[] parts = mcAddress.split(":");
			int portNumber = Integer.parseInt(parts[1]);
			InetAddress ipAddress = InetAddress.getByName(parts[0]);
			InetSocketAddress socket = new InetSocketAddress(ipAddress, portNumber);
			this.mcAddresses.add(socket);
			System.out.println("Sending stuff to " + socket.getAddress().getHostAddress().toString() + ":" + socket.getPort());
		}
		this.numThreadsPTP = numThreadsPTP;
		this.readSharded = readSharded;
	}
	
	public void run() {
		// TODO: add a library for logs (e.g. log4j) and replace all the useful prints with log statements
		System.out.println("hello middleware!");
		System.out.format("my network socket: %s:%d\n", ip, port);
		
		System.out.println(Arrays.toString(mcAddresses.toArray()));
		
		try {
			ServerSocket socket = new ServerSocket(this.port);
			
			while (true) {
				System.out.println("before receiving a message");

				Socket clientSocket = socket.accept();
				InetSocketAddress clientAddress = (InetSocketAddress)clientSocket.getRemoteSocketAddress();
				
				System.out.println("wat");
				//clientSocket.setSoTimeout(1000);
				
				//String message = receiveMessage(clientSocket);
				String message = receiveTextLine(clientSocket);
				
				System.out.println("wtf");
				CommandParsingResult parsedMessage = new CommandParsingResult(message);
				String command = parsedMessage.getCommand();
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
						if (keys.size() == 1) {
							String value = this.get(parsedMessage);
						}
						
						//List<String> value = this.get(parsedMessage, clientAddress);
						break;
					}
					case "set": {
						System.out.println("This is a set!");
						if (keys.size() != 1) {
							System.out.println("Too many keys for a set.." + Arrays.toString(keys.toArray()));
							continue;
						}
						int bytes = parsedMessage.getBytes();
						String value = parsedMessage.getValue();
						
						// now read the number of bytes defined by the "bytes" variable:
						// that is the amount of data that forms the data block
						String dataBlock = value.substring(0, bytes);
						parsedMessage.setValue(dataBlock);
						
						set(parsedMessage, clientAddress);
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

	public String get(CommandParsingResult command) throws UnknownHostException, IOException {
		InetSocketAddress server = this.chooseServer();
		Socket kkSocket = new Socket(server.getAddress().getHostAddress().toString(), server.getPort());
		OutputStream out = new DataOutputStream(kkSocket.getOutputStream());
		String commandToSend = command.getCommand() + " " + String.join(" ", command.getKeys()) + '\r' + '\n';
		out.write(commandToSend.getBytes());
		
		//String reply = receiveMessageFromServer(kkSocket);
		//String reply = receiveMessage(kkSocket);
		//String reply = receiveUnstructuredData(kkSocket, command.getBytes());
		String reply = receiveTextLine(kkSocket);
		CommandParsingResult replyParser = new CommandParsingResult (reply);
		System.out.println("reply from the server (GET):" + replyParser);
		int len = replyParser.getBytes();
		
		String value = receiveUnstructuredData(kkSocket, len);
		System.out.println(value);
		return null;
	}
	
	public List<String> get(CommandParsingResult command, InetSocketAddress client) throws UnknownHostException, IOException {
		/*
		List<String> values = new ArrayList<String>();
		for (String key : keys) {
			String value = get(key);
			Socket clientSocket = new Socket(client.getAddress().getHostAddress().toString(), client.getPort());
			OutputStream os = new DataOutputStream(clientSocket.getOutputStream());
			os.write(value.getBytes());
			values.add(value);
		}
		return values;
		*/
		return null;
	}
	
	private InetSocketAddress chooseServer() {
		// TODO Auto-generated method stub
		return mcAddresses.get(0);
	}

	public void set(CommandParsingResult command, InetSocketAddress client) throws UnknownHostException, IOException {
		for (InetSocketAddress server : this.mcAddresses) {
			writeValue(command, server, client);
		}
	}
	
	// TODO: handle all this with an IP_ADDRESS specific type (net library or something like that)
	private void writeValue(CommandParsingResult command, InetSocketAddress server, InetSocketAddress client) throws UnknownHostException, IOException  {
		System.out.println("Sending stuff to " + server.getAddress().toString() + ":" + server.getPort());
		
		Socket kkSocket = new Socket(server.getAddress().getHostAddress().toString(), server.getPort());
		OutputStream out = new DataOutputStream(kkSocket.getOutputStream());
		
		String noReply = command.getNoreply();

		StringBuilder sb = new StringBuilder(command.getCommand() + " " + command.getKeys().get(0) + " " +
				command.getFlags() + " " + command.getExptime() + " " + command.getBytes());
		if (noReply != null) {
			sb.append(" " + noReply);
		}

		sb.append(new String("" + '\r' + '\n'));
		sb.append(new String(command.getValue() + '\r' + '\n'));
		String commandToSend = sb.toString();
		
		System.out.println("sending the following command: " + commandToSend);
		out.write(commandToSend.getBytes());
		
		// also handle the case when "noreply" is not set: we should expect something to come back from the server (and maybe we have to send something back to the client?)
		if (noReply == null) {
			String reply = receiveMessageFromServer(kkSocket);
			System.out.println("this is the reply: " + reply);
			Socket clientSocket = new Socket(client.getAddress().getHostAddress().toString(), client.getPort());
			OutputStream os = new DataOutputStream(clientSocket.getOutputStream());
			os.write(reply.getBytes());
		}
	}
	
	private String receiveTextLine(Socket clientSocket) throws IOException {
		InputStream is = new DataInputStream(clientSocket.getInputStream());
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		int i=0;
		while(readByte > -1) {
			if (readByte == '\r') {
				b[i] = (byte)readByte;
				readByte = is.read();
				i++;
				if (readByte == '\n') {
					b[i] = (byte)readByte;
					break;
				} else {
					continue;
				}
			}
			
			b[i] = (byte)readByte;
			System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
			
			readByte = is.read();
			i++;
		}
		String message = new String(b);
		System.out.println("number of characters read: " + i); // read characters
		return message;
	}
	
	public String receiveUnstructuredData(Socket socket, int len) throws IOException {
		InputStream is = new DataInputStream(socket.getInputStream());
		byte[] b = new byte[4096];
		
		is.read(b, 0, len);
		return new String(b);
	}
	
	public String receiveMessage(Socket clientSocket) throws IOException {
		InputStream is = new DataInputStream(clientSocket.getInputStream());
		System.out.println("after receiving a message");
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		int i=0;
		while(readByte > -1) {
			b[i] = (byte)readByte;
			System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
			try {
				readByte = is.read();
			} catch (SocketTimeoutException e) {
				break;
			}
			i++;
		}
		String message = new String(b);
		System.out.println("number of characters read: " + i); // read characters
		return message;
	}
	
	public String receiveMessageFromServer(Socket clientSocket) throws IOException {
		InputStream is = new DataInputStream(clientSocket.getInputStream());
		System.out.println("after receiving a message");
		byte[] b = new byte[4096];
		
		int readByte = is.read();
		int i=0;
		while(readByte > -1) {
			if (readByte == '\r') {
				b[i] = (byte)readByte;
				readByte = is.read();
				i++;
				if (readByte == '\n') {
					b[i] = (byte)readByte;
					break;
				} else {
					continue;
				}
			}
			
			b[i] = (byte)readByte;
			System.out.format("read byte: %c. Int: %d\n", readByte, readByte);
			
			readByte = is.read();
			i++;
		}
		String message = new String(b);
		System.out.println("number of characters read: " + i); // read characters
		return message;
	}
}
