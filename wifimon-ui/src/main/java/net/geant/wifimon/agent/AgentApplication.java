package net.geant.wifimon.agent;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.json.JSONConfiguration;
//import com.sun.jersey.client.urlconnection.HTTPSProperties;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;



import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * Created by kanakisn on 11/17/15.
 */

@SpringBootApplication
//@EnableJpaRepositories(AgentApplication.BASE_PACKAGE)
@EntityScan(basePackages = AgentApplication.BASE_PACKAGE)
public class AgentApplication extends SpringBootServletInitializer {

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

	/*
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getProperties().
                put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                    new HTTPSProperties((hostname, session) -> true, sslcontext));

        return Client.create(clientConfig);
	*/

	Client client = ClientBuilder.newBuilder().
			sslContext(sslcontext).
			hostnameVerifier((hostname, session) -> true).
			build();

	return client;

    }

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

}
