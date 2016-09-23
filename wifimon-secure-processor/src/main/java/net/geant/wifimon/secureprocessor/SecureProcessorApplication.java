package net.geant.wifimon.secureprocessor;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by kokkinos on 2/9/2016.
 */

@SpringBootApplication
//@EnableJpaRepositories(ProcessorApplication.BASE_PACKAGE)
@EntityScan(basePackages = SecureProcessorApplication.BASE_PACKAGE)
public class SecureProcessorApplication {
    public static final String BASE_PACKAGE = "net.geant.wifimon";

    @Autowired
    private Environment env;

    @Bean
    public Client client() {
        TrustManager[ ] certs = new TrustManager[ ] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                }
        };
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, certs, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getProperties().
                put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                    new HTTPSProperties((hostname, session) -> true, sslcontext));

        return Client.create(clientConfig);
    }


    public static void main(String... args) {
        SpringApplication.run(SecureProcessorApplication.class, args);
    }
}
