package com.seohyang25.util.ssl.certificate.checker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.net.SocketFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

public class SslCertificateChecker {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SslCertificateChecker.class);

    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
    private static final SocketFactory SOCKET_FACTORY = SSLSocketFactory.getDefault();

    void printDomainCertificateInfo(String domain, int port, int timeoutMsec) {
        getCertificatesBySSLSocket(domain, port, timeoutMsec)
                .ifPresent(certificateList -> printInfo(certificateList));getCertificatesBySSLSocket(domain, port, timeoutMsec)
                .ifPresent(certificateList -> printInfo(certificateList));
    }

    private void printInfo(List<Certificate> certificateList) {
        certificateList
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
                });
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
            log.warn("getCertificatesBySSLSocket fail! domain=" + domain + " port=" + port + " timeoutMsec=" + timeoutMsec);
            log.warn(e.getMessage(), e);

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

    private static Optional<List<Certificate>> getCertificatesByStartTls(String domain, int port, int timeoutMsec) {
        Socket socket = null;
        try {
            socket = new Socket(domain, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line;
            writer.write("STARTTLS\r\n");
            writer.flush();
            while ((line = reader.readLine()) != null) {
                if (!line.contains("Ready to start TLS")) {
                    continue;
                }

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                }, new java.security.SecureRandom());

                SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
                sslSocket.startHandshake();
                sslSocket.close();
                return Optional.of(Arrays.stream(sslSocket.getSession().getPeerCertificates()).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.warn("getCertificatesBySSLSocket fail! domain=" + domain + " port=" + port + " timeoutMsec=" + timeoutMsec);
            log.warn(e.getMessage(), e);

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
