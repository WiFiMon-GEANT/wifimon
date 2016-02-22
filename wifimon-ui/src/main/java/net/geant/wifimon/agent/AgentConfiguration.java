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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        Map<String, Object> properties = clientConfig.getProperties();
        HTTPSProperties httpsProperties = new HTTPSProperties((s, sslSession) -> true, sslcontext);
        properties.put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);

        return Client.create(clientConfig);
    }

    public static void main(String... args) {
        SpringApplication.run(AgentConfiguration.class, args);
    }

}
