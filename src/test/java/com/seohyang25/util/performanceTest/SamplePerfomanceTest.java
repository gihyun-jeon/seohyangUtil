package com.seohyang25.util.performanceTest;

import org.junit.Test;

public class SamplePerfomanceTest {
	private int threadCount = 10;
	private int workCountPerThread = 100;

	@Test
	public void stringBuliderTest() throws InterruptedException {
		PerformanceTestManager manager = new PerformanceTestManager(threadCount, workCountPerThread, new SampleTestTarget1());
		manager.start();

		while (false == manager.completed()) {
			Thread.sleep(10);
		}

		manager.report();
	}

	@Test
	public void stringTest() throws InterruptedException {
		PerformanceTestManager manager = new PerformanceTestManager(threadCount, workCountPerThread, new SampleTestTarget2());
		manager.start();

		while (false == manager.completed()) {
			Thread.sleep(10);
		}

		manager.report();
	}
}
