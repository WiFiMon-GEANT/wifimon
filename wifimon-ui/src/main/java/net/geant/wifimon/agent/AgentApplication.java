package net.geant.wifimon.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

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
 * Created by kanakisn on 11/17/15.
 */
@SpringBootApplication
@EntityScan(basePackages = AgentApplication.BASE_PACKAGE)
public class AgentApplication extends SpringBootServletInitializer {
    public static final String BASE_PACKAGE = "net.geant.wifimon";

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }
}
