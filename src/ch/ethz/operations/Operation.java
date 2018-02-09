package ch.ethz.operations;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.communication.HostWrapper;

public abstract class Operation {
	final static String BYTES = "bytes";
	String message;
	HostWrapper client;
	Map<String, Object> parameters;
	long waitingTime;
	long serviceTime;
	
	public Operation(String message, HostWrapper client) {
		this.message = message;
		this.client = client;
		this.parameters = new HashMap<String, Object>();
	}
	
	public abstract void execute(List<HostWrapper> servers) throws UnknownHostException, IOException;
	
	public String getMessage() {
		return this.message;
	}
	
	public HostWrapper getClient() {
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
	
	public void startWaitingTimer() {
		this.waitingTime = System.nanoTime();
	}
	
	public void stopWaitingTimer() {
		this.waitingTime = System.nanoTime() - this.waitingTime;
	}
	
	public void startServiceTimer() {
		this.serviceTime = System.nanoTime();
	}
	
	public void stopServiceTimer() {
		this.serviceTime = System.nanoTime() - this.serviceTime;
	}
	
	public long getWaitingTime() {
		return this.waitingTime;
	}
	
	public long getServiceTime() {
		return this.serviceTime;
	}
	
	public abstract String getType();
}
