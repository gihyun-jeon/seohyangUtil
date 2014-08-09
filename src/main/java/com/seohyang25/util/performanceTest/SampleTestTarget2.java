package com.seohyang25.util.performanceTest;

public class SampleTestTarget2 implements PerformanceTestable {
	@Override
	public void doTest() {
		String targetString = "";
		String appendString = "a";
		for (int i = 0; i < 1000; i++) {
			targetString = targetString + appendString;
		}
	}
}
