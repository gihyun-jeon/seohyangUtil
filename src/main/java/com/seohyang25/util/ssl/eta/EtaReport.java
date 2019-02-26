package com.seohyang25.util.ssl.eta;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class EtaReport {
    float progressRate;
    long elapsedMsec;
    long estimatedRemainMsec;

}
