package ch.ethz.operations;

public class RoundRobinToken {
	private static RoundRobinToken instance = null;
	private int counter;
	private static int size;
	
	private RoundRobinToken() {
		
	}
	public RoundRobinToken(int size) {
		RoundRobinToken.instance = new RoundRobinToken();
		instance.counter = 0;
		RoundRobinToken.size = size;
	}
	
	public static RoundRobinToken getInstance() {
		return instance;
	}
	
	public int getValue() {
		return instance.counter;
	}
	
	public void setValue(int value) {
		instance.counter = value;
	}
	
	public int getSize() {
		return RoundRobinToken.size;
	}
}
