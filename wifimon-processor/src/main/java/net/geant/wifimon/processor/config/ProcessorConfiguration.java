package net.geant.wifimon.processor.config;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * Created by kanakisn on 11/17/15.
 */

@Configuration
@ComponentScan(basePackages={"net.geant.wifimon.processor"})
public class ProcessorConfiguration {
    
    private static final String INFLUX_HOST = "influx.host";
    private static final String INFLUX_PORT = "influx.port";
    private static final String INFLUX_PROTOCOL = "influx.protocol";
    private static final String INFLUX_DB_NAME = "influx.db.name";
    private static final String INFLUX_DB_USERNAME = "influx.db.username";
    private static final String INFLUX_DB_PASSWORD = "influx.db.password";

    
    @Autowired
	private Environment env;
    
    @Bean
    public InfluxDB influxDBClient() throws Exception {
        try {
            InfluxDB influxDB = InfluxDBFactory.connect(env.getRequiredProperty(INFLUX_PROTOCOL) + "://" +
                            env.getRequiredProperty(INFLUX_HOST) + ":" + env.getRequiredProperty(INFLUX_PORT),
                    env.getRequiredProperty(INFLUX_DB_USERNAME), env.getRequiredProperty(INFLUX_DB_PASSWORD));
            influxDB.createDatabase(env.getRequiredProperty(INFLUX_DB_NAME));
            return influxDB;
        } catch (Exception e) {
            return null;
        }
    }


}