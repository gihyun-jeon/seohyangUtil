package com.seohyang25.util.performanceTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class PerformanceTestManager {
	private List<PerformanceTestRunner> threadList = new ArrayList<PerformanceTestRunner>();
	private int jobCount;
	private String targetName;
	private final Stopwatch stopwatch = Stopwatch.createUnstarted();

	public PerformanceTestManager(int taskCount, int workingCount, PerformanceTestable target) {
		this.jobCount = workingCount;
		for (int i = 0; i < taskCount; i++) {
			threadList.add(new PerformanceTestRunner(workingCount, target));
		}
		this.targetName = target.getClass().getName();
	}

	public void report() {
		System.out.println("");
		System.out.println("==== PerfomanceTest Report ====");
		new Reporter().report();

	}

	class Reporter {
		public void report() {
			System.out.println("targetName          = " + targetName);
			System.out.println("Concurrent count    = " + threadList.size());
			System.out.println("Request per task    = " + jobCount);
			System.out.println("Total request count = " + getRequestCount());
			System.out.println("Total response time = " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " msec");
			System.out.println("TPS                 = " + getTps());
			System.out.println("");
		}

		public int getRequestCount() {
			return threadList.size() * jobCount;
		}

		public float getTps() {
			return (float)(getRequestCount()) / stopwatch.elapsed(TimeUnit.MILLISECONDS) * 1000;
		}
	}

	public void start() {
		stopwatch.start();
		for (PerformanceTestRunner task : threadList) {
			task.start();
		}
	}

	public List<PerformanceTestRunner> getTasks() {
		return threadList;
	}

	public boolean completed() {
		for (PerformanceTestRunner testRunner : threadList) {
			if (false == testRunner.isCompleted()) {
				return false;
			}
		}
		stopwatch.stop();
		return true;
	}

}
