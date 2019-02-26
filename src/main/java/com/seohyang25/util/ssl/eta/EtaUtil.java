package com.seohyang25.util.ssl.eta;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

import com.google.common.base.Stopwatch;

public class EtaUtil {
    public void briefing(Stopwatch stopwatch, long currentCount, long targetCount) {
        Assert.notNull(stopwatch);
        Assert.state(currentCount >= 0);
        Assert.state(targetCount > 0);

        float progressRate = (currentCount / (float) targetCount) * 100;
        long elapsedMsec = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        long estimatedRemainMsec = (elapsedMsec * targetCount) / currentCount;

        System.out.println(progressRate);
        System.out.println(elapsedMsec);
        System.out.println(estimatedRemainMsec);

        System.out.println(String.format(
                "progressInfo = %d/%d (%.2f%%), elapsed %s, remainEstimated %s, ETA=%s",
                currentCount, targetCount, progressRate,
                Duration.ofMillis(elapsedMsec), Duration.ofMillis(estimatedRemainMsec - elapsedMsec),
                LocalDateTime.now().plusSeconds((estimatedRemainMsec - elapsedMsec) / 1000)
        ));

    }
}

