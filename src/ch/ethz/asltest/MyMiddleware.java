package ch.ethz.asltest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

import ch.ethz.operations.CommandParser;
import ch.ethz.operations.DataTransfer;
import ch.ethz.operations.Operation;

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
	AbstractServer servers;
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
		this.servers = new AbstractServer(this.mcAddresses);
		this.numThreadsPTP = numThreadsPTP;
		this.readSharded = readSharded;
	}
	
	public void run() {
		// TODO: add a library for logs (e.g. log4j) and replace all the useful prints with log statements
		System.out.println("hello middleware! (new!)");
		System.out.format("my network socket: %s:%d\n", ip, port);
		
		System.out.println(Arrays.toString(mcAddresses.toArray()));
		
		try {
			ServerSocket socket = new ServerSocket(this.port);
			Socket clientSocket = socket.accept();
			while (true) {
				System.out.println("before receiving a message (new implementation)");
				//Socket clientSocket = socket.accept();
				String message = DataTransfer.receiveTextLine(clientSocket);
				Operation operation = CommandParser.getOperation(message, clientSocket, this.servers);
				operation.execute();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
