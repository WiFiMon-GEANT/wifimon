package net.geant.wifimon.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;

/**
 * Created by kanakisn on 13/02/16.
 */

@SpringBootApplication
@EntityScan("net.geant.wifimon")
public class ProcessorApplication {

    public static void main(String... args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }

}
