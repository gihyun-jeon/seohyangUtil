package com.seohyang25.util.ssl.eta;

import org.junit.Test;

import com.google.common.base.Stopwatch;

public class EtaUtilTest {

    private EtaUtil etaUtil = new EtaUtil();

    @Test
    public void test() throws Exception {
        @SuppressWarnings("UnstableApiUsage")
        Stopwatch started = Stopwatch.createStarted();
        Thread.sleep(125);
        etaUtil.briefing(started, 5, 100);
    }
}
