package ch.ethz.measures;

import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.operations.Operation;

public class PeriodicMeasurer extends TimerTask {
	private BlockingQueue<Operation> requests;
	private AtomicInteger queueLength;
	private AtomicInteger measurements;
	
	public PeriodicMeasurer(BlockingQueue<Operation> requests) {
		this.requests = requests;
		this.queueLength = new AtomicInteger(0);
		this.measurements = new AtomicInteger(0);
	}

	@Override
	public void run() {
		this.queueLength.addAndGet(this.requests.size());
		this.measurements.incrementAndGet();
	}

	public int getAverageQueueLength() {
		return this.queueLength.get()/this.measurements.get();
	}
}
