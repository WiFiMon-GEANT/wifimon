package net.geant.wifimon.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Created by kanakisn on 13/02/16.
 */

@SpringBootApplication
@EnableJpaRepositories(ProcessorApplication.BASE_PACKAGE)
@EntityScan(basePackages = ProcessorApplication.BASE_PACKAGE)
public class ProcessorApplication extends SpringBootServletInitializer{

    public static final String BASE_PACKAGE = "net.geant.wifimon";

    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }

}
