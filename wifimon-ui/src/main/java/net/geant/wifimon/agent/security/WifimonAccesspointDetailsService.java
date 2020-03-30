package net.geant.wifimon.agent.security;

import net.geant.wifimon.agent.service.AccesspointService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kokkinos on 9/3/2017.
 */
public class WifimonAccesspointDetailsService {

    private AccesspointService accesspointService;

    @Autowired
    public WifimonAccesspointDetailsService(AccesspointService accesspointService) {
        this.accesspointService = accesspointService;
    }

}
