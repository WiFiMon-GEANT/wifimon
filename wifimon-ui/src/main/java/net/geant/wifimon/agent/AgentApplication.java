package net.geant.wifimon.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Created by kanakisn on 11/17/15.
 */
@SpringBootApplication
@EntityScan(basePackages = AgentApplication.BASE_PACKAGE)
public class AgentApplication extends SpringBootServletInitializer {
    public static final String BASE_PACKAGE = "net.geant.wifimon";
    private static Logger loggerAgentApplication = Logger.getLogger(AgentApplication.class.getName());

    @Autowired
    private Environment env;

    @Bean
    public Client client() {
        TrustManager[] certs = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
			    // checkServerTrusted Method
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
			    // checkClientTrusted method
                    }
                }
        };
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, certs, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
        } catch (NoSuchAlgorithmException | NullPointerException | KeyManagementException e) {
            loggerAgentApplication.log(Level.INFO, e.toString());
        }

        return  ClientBuilder.newBuilder().
                sslContext(sslcontext).
                hostnameVerifier((hostname, session) -> true).
                build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }
}
