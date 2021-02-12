package icu.cyclone.avigilon.utils;

import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class HttpsTrustModifier {
    private static final TrustingHostnameVerifier
            TRUSTING_HOSTNAME_VERIFIER = new TrustingHostnameVerifier();

    private static final class TrustingHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class AlwaysTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public static void discardChecking(HttpURLConnection connection) throws KeyManagementException, NoSuchAlgorithmException {
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            SSLSocketFactory factory = getFactory();
            httpsConnection.setSSLSocketFactory(factory);
            httpsConnection.setHostnameVerifier(TRUSTING_HOSTNAME_VERIFIER);
        }
    }

    private static SSLSocketFactory getFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(null, new TrustManager[]{new AlwaysTrustManager()}, null);
        return ctx.getSocketFactory();
    }

}
