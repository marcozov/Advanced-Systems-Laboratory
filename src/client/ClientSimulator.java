package client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			String hostName = "127.0.0.1";
			int portNumber = 11212;
			Socket kkSocket = new Socket(hostName, portNumber);
			OutputStream out = new DataOutputStream(kkSocket.getOutputStream());
			
			//String setTest1 = "set some_key2 0 0 25 noreply" + '\r' + '\n' + "some_value123124jjdd" + '\r' + '\n';
			//String setTest1 = "set some_key2 0 0 10 noreply" + '\r' + '\n' + "some_value123124jjdd" + '\r' + '\n';
			String setTest1 = "set some_key2 0 0 10" + '\r' + '\n' + "some_value123124jjdd" + '\r' + '\n';
			byte[] b = setTest1.getBytes();
			out.write(b);
	/*		
			TimeUnit.SECONDS.sleep(1);
			
			portNumber++;
			kkSocket = new Socket(hostName, portNumber);
			out = new DataOutputStream(kkSocket.getOutputStream());
			String setTest2 = "set some_key2 0 0 13 noreply" + '\r' + '\n' + "some_value222" + '\r' + '\n';
			b = setTest2.getBytes();
			out.write(b);
		*/	
		/*
			TimeUnit.SECONDS.sleep(1);
			
			portNumber++;
			kkSocket = new Socket(hostName, portNumber);
			out = new DataOutputStream(kkSocket.getOutputStream());
			String setTest3 = "set some_key3 0 0 13 noreply" + '\r' + '\n' + "some_value333" + '\r' + '\n';
			b = setTest3.getBytes();
			out.write(b);
	*/
	/*		TimeUnit.SECONDS.sleep(1);
			kkSocket = new Socket(hostName, portNumber);
			out = new DataOutputStream(kkSocket.getOutputStream());
			String setTest2 = "get key1" + '\r' + '\n';
			b = setTest2.getBytes();
			out.write(b);
			
			TimeUnit.SECONDS.sleep(1);
			kkSocket = new Socket(hostName, portNumber);
			out = new DataOutputStream(kkSocket.getOutputStream());
			String setTest3 = "get key1 key2 key3" + '\r' + '\n';
			b = setTest3.getBytes();
			out.write(b);
	*/
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
