package net.geant.wifimon.agent;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Created by kanakisn on 11/17/15.
 */

@SpringBootApplication
public class AgentConfiguration {
    
    @Autowired
	private Environment env;

    public static void main(String... args) {
        SpringApplication.run(AgentConfiguration.class, args);
    }

}
