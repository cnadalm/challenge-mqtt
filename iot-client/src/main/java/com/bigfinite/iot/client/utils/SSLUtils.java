package com.bigfinite.iot.client.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

public class SSLUtils {

    static final Logger LOGGER = Logger.getLogger(SSLUtils.class.getName());

    public final static TrustManager[] TRUST_ALL_CERTS = new X509TrustManager[]{};

    public static SSLSocketFactory getSocketFactory(final String crtFile,
            final String keyFile, final String password) throws Exception {

        return getSSLContext(crtFile, keyFile, password).getSocketFactory();
    }

    public static SSLContext getSSLContext(final String crtFile,
            final String keyFile, final String password) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // load client certificate
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(crtFile));
        X509Certificate cert = null;
        while (bis.available() > 0) {
            cert = (X509Certificate) cf.generateCertificate(bis);
        }

        KeyPair key;
        // load client private key
        try (PEMParser pemParser = new PEMParser(new FileReader(keyFile))) {
            Object object = pemParser.readObject();
            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
                    .build(password.toCharArray());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            if (object instanceof PEMEncryptedKeyPair) {
                LOGGER.log(Level.INFO, "Encrypted key - we will use provided password");
                key = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
            } else {
                LOGGER.log(Level.INFO, "Unencrypted key - no password needed");
                key = converter.getKeyPair((PEMKeyPair) object);
            }
        }

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("client", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), null, null);

        return context;
    }

}
