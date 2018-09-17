package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.AccesspointCreateFormModel;
import net.geant.wifimon.model.entity.Accesspoint;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kokkinos on 8/3/2017.
 */
public interface AccesspointService {

    Optional<Accesspoint> getAccesspointByApid(long apid);

    Optional<Accesspoint> getAccesspointByMac(String mac);

    Collection<Accesspoint> getAllAccesspoints();

    Accesspoint create(AccesspointCreateFormModel form);

    void delete(Long apid);

}
