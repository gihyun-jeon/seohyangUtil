package com.seohyang25.util.performanceTest;

public class SampleTestTarget1 implements PerformanceTestable {
    public void doTest() {
        StringBuilder sb = new StringBuilder();
        String appendString = "a";
        for (int i = 0; i < 1000; i++) {
            sb.append(appendString);
        }
    }
}
