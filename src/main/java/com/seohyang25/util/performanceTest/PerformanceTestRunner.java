package com.seohyang25.util.performanceTest;

public class PerformanceTestRunner extends Thread {
	private PerformanceTestable target;
	private int workingCount;
	private boolean completed = false;

	public PerformanceTestRunner(int workingCount, PerformanceTestable target) {
		this.workingCount = workingCount;
		this.target = target;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void run() {
		for (int i = 0; i < workingCount; i++) {
			target.doTest();
		}
		completed = true;
	}
}