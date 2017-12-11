package com.seohyang25.util.ssl.certificate.checker;

import com.google.common.collect.Lists;
import org.slf4j.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SslCertificateChecker {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SslCertificateChecker.class);

    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
    private static final SocketFactory SOCKET_FACTORY = SSLSocketFactory.getDefault();


    void printDomainCertificateInfo(String domain, int port, int timeoutMsec) {
        getCertificatesBySSLSocket(domain, port, timeoutMsec)
                .ifPresent(certificateList -> certificateList
                        .forEach(certificate -> {
                            if (!(certificate instanceof X509Certificate)) {
                                log.warn("find not instanceof X509Certificate! certificate=" + certificate.toString());
                                return;
                            }

                            X509Certificate x509Certificate = (X509Certificate) certificate;
                            if (-1 != x509Certificate.getBasicConstraints()) { // subject of the certificate is a CA, pass.
                                return;
                            }

                            System.out.println(x509Certificate);
                        })
                );
    }


    private static Optional<List<Certificate>> getCertificatesBySSLSocket(String domain, int port, int timeoutMsec) {
        SSLSocket socket = null;
        try {
            socket = (SSLSocket) SOCKET_FACTORY.createSocket();
            SSLParameters sslParams = socket.getSSLParameters();
            sslParams.setServerNames(Lists.newArrayList(new SNIHostName(domain)));
            socket.setSSLParameters(sslParams);
            socket.connect(new InetSocketAddress(domain, port), timeoutMsec);
            socket.setSoTimeout(timeoutMsec);
            socket.startHandshake();
            socket.close();
            return Optional.of(Arrays.stream(socket.getSession().getPeerCertificates()).collect(Collectors.toList()));

        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            log.info("getCertificatesBySSLSocket fail! domain=" + domain + " port=" + port + " timeoutMsec=" + timeoutMsec);

        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
        return Optional.empty();
    }
}
