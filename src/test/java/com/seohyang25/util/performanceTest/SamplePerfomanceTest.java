package com.seohyang25.util.performanceTest;

import org.junit.Test;

public class SamplePerfomanceTest {
	private int threadCount = 10;
	private int workCountPerThread = 100;

	@Test
	public void test() throws InterruptedException {
		runTest(new SampleTestTarget1());
		runTest(new SampleTestTarget1());

		runTest(new SampleTestTarget2());
		runTest(new SampleTestTarget2());

	}



	private void runTest(PerformanceTestable target) throws InterruptedException {
		PerformanceTestManager manager = new PerformanceTestManager(threadCount, workCountPerThread, target);
		manager.start();

		while (false == manager.completed()) {
			Thread.sleep(10);
		}

		manager.report();
	}
}
