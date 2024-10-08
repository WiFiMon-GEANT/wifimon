package net.geant.wifimon.secureprocessor.config;

import net.geant.wifimon.secureprocessor.resource.AggregatorResource;
import net.geant.wifimon.secureprocessor.resource.CORSResponseFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by kokkinos on 2/9/2016.
 */
@Configuration
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(CORSResponseFilter.class);
        register(AggregatorResource.class);
    }
}
