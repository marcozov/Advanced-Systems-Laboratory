package ch.ethz.measures;

import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import ch.ethz.operations.Operation;

public class QueueLengthMeasurer extends Thread {
	private BlockingQueue<Operation> requests;
	PeriodicMeasurer measurer;
	
	public QueueLengthMeasurer(BlockingQueue<Operation> requests) {
		this.requests = requests;
	}
	
	public void run() {
		java.util.Timer t = new java.util.Timer();
		this.measurer = new PeriodicMeasurer(this.requests);
		t.schedule(this.measurer, 100, 100);
		
	}
	
	public int getAverageQueueLength() {
		return measurer.getAverageQueueLength();
	}
}
