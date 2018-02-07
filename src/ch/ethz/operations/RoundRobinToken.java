package ch.ethz.operations;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinToken {
	private static RoundRobinToken instance = null;
	private AtomicInteger counter;
	private static int size;
	
	private RoundRobinToken() {
		
	}
	public RoundRobinToken(int size) {
		RoundRobinToken.instance = new RoundRobinToken();
		instance.counter = new AtomicInteger(0);
		RoundRobinToken.size = size;
	}
	
	public static RoundRobinToken getInstance() {
		return instance;
	}
	
	public int getValue() {
		return instance.counter.getAndIncrement() % instance.getSize();
	}
	
	public int getSize() {
		return RoundRobinToken.size;
	}
}
