package net.geant.wifimon.agent;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by kanakisn on 11/17/15.
 */

@SpringBootApplication
public class AgentConfiguration {

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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOrigins("*").
                        allowedHeaders("Access-Control-Allow-Origin", "Connection").
                        exposedHeaders("Access-Control-Allow-Origin", "Connection").
                        allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PUT");
            }
        };
    }

    public static void main(String... args) {
        SpringApplication.run(AgentConfiguration.class, args);
    }

}
