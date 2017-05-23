package org.nybatis.core.util;

public class StopWatcher {

	private long initTime = System.nanoTime();

	public long elapsedNanoSeconds() {
		return System.nanoTime() - initTime;
	}

	public long elapsedMiliSeconds() {
		return elapsedNanoSeconds() / 1_000_000;
	}

	public double elapsedSeconds() {
		return elapsedNanoSeconds() / 1_000_000_000.0;
	}

	public StopWatcher reset() {
	    initTime = System.nanoTime();
	    return this;
	}
}
