package ch.ethz.operations;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.asltest.AbstractServer;
import ch.ethz.asltest.CommunicationHandler;
import ch.ethz.asltest.HostWrapper;

public abstract class Operation {
	final static String BYTES = "bytes";
	String message;
	//Socket client;
	CommunicationHandler client;
	//AbstractServer server;
	Map<String, Object> parameters;
	
	//public Operation(String message, Socket clientSocket, AbstractServer server) {
	public Operation(String message, CommunicationHandler client) {
		this.message = message;
		//this.client = clientSocket;
		this.client = client;
		//this.server = server;
		this.parameters = new HashMap<String, Object>();
	}
	
	public abstract String execute(List<HostWrapper> servers) throws UnknownHostException, IOException;
	// TODO: decide whether the function should return a socket or an address
	protected abstract void parseParametersAndCheckFormat(String message);
	
	public String getMessage() {
		return this.message;
	}
	
	//public Socket getClient() {
	public CommunicationHandler getClient() {
		return this.client;
	}
	
	public Map<String, Object> getParameters() {
		return this.parameters;
	}
	
	public void setParameter(String key, Object value) {
		this.parameters.put(key, value);
	}
	
	public Object getParameter(String key) {
		return this.parameters.get(key);
	}
	
	protected void setNumberOfBytes(Integer bytes) {
		this.setParameter(BYTES, bytes);
	}
	
	public Integer getNumberOfBytes() {
		return (Integer) this.getParameter(BYTES);
	}
	
	// TODO: decide whether this method should be private or public
	//public AbstractServer getAbstractServer() {
	//	return this.server;
	//}

}
