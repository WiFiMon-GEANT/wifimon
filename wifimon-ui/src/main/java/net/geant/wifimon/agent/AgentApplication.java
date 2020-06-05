package net.geant.wifimon.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

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
