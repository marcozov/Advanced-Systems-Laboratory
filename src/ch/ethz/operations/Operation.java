package ch.ethz.operations;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.asltest.SocketStreamsHandler;
import ch.ethz.asltest.HostWrapper;

public abstract class Operation {
	final static String BYTES = "bytes";
	String message;
	SocketStreamsHandler client;
	Map<String, Object> parameters;
	
	public Operation(String message, SocketStreamsHandler client) {
		this.message = message;
		this.client = client;
		this.parameters = new HashMap<String, Object>();
	}
	
	public abstract String execute(List<HostWrapper> servers) throws UnknownHostException, IOException;
	
	public String getMessage() {
		return this.message;
	}
	
	public SocketStreamsHandler getClient() {
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
}
