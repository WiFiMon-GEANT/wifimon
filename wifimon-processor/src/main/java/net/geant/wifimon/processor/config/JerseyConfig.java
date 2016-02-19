package net.geant.wifimon.processor.config;

import net.geant.wifimon.processor.endpoint.AggregatorProcessor;
import net.geant.wifimon.processor.endpoint.CORSResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kanakisn on 12/02/16.
 */

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(CORSResponseFilter.class);
        register(AggregatorProcessor.class);
    }

}
