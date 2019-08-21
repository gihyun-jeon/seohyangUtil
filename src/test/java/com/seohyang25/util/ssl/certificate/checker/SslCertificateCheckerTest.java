package com.seohyang25.util.ssl.certificate.checker;

import org.junit.Test;

public class SslCertificateCheckerTest {
    private SslCertificateChecker sut = new SslCertificateChecker();

    @Test
    public void test_printDomainCertificateInfo() {

        sut.printDomainCertificateInfo("google.com", 443, 1000);
        sut.printDomainCertificateInfo("google.com", 443, 1000);
    }

}
