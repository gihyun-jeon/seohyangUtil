package com.seohyang25.util.performanceTest;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTestManager {
	private List<PerformanceTestRunner> threadList = new ArrayList<PerformanceTestRunner>();
	private long startMsec;
	private int jobCount;
	private String targetName;

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
			System.out.println("Total response time = " + getRunningTime() + " msec");
			System.out.println("TPS                 = " + getTps());
			System.out.println("");
		}

		public int getRequestCount() {
			return threadList.size() * jobCount;
		}

		public float getTpms() {
			return (float)(getRequestCount()) / getRunningTime();
		}

		public float getTps() {
			return getTpms() * 1000;
		}
	}

	public long getRunningTime() {
		return System.currentTimeMillis() - getStartMsec();

	}

	public void start() {
		startMsec = System.currentTimeMillis();
		for (PerformanceTestRunner task : threadList) {
			task.start();
		}
	}

	public List<PerformanceTestRunner> getTasks() {
		return threadList;
	}

	public long getStartMsec() {
		return startMsec;
	}

	public boolean completed() {
		for (PerformanceTestRunner testRunner : threadList) {
			if (false == testRunner.isCompleted()) {
				return false;
			}
		}
		return true;
	}

}
