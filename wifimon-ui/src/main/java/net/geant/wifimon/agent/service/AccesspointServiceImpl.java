package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.AccesspointCreateFormModel;
import net.geant.wifimon.agent.repository.AccesspointRepository;
import net.geant.wifimon.model.entity.Accesspoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by kokkinos on 8/3/2017.
 */

@Service
public class AccesspointServiceImpl implements AccesspointService {

    private final AccesspointRepository accesspointRepository;

    @Autowired
    public AccesspointServiceImpl(final AccesspointRepository accesspointRepository) {
        this.accesspointRepository = accesspointRepository;
    }

    @Override
    public Optional<Accesspoint> getAccesspointByApid(long apid) {
        return Optional.ofNullable(accesspointRepository.findOne(apid));
    }

    @Override
    public Optional<Accesspoint> getAccesspointByMac(String mac) {
        return accesspointRepository.findOneByMac(mac);
    }

    @Override
    public Collection<Accesspoint> getAllAccesspoints() {
        return accesspointRepository.findAll(new Sort("mac"));
    }

    @Override
    public Accesspoint create(AccesspointCreateFormModel form) {
        Accesspoint accesspoint = new Accesspoint();
        accesspoint.setMac(form.getMac());
        accesspoint.setLatitude(form.getLatitude());
        accesspoint.setLongitude(form.getLongitude());
        accesspoint.setBuilding(form.getBuilding());
        accesspoint.setFloor(form.getFloor());
        accesspoint.setNotes(form.getNotes());
        return accesspointRepository.save(accesspoint);
    }

    @Override
    public void delete(Long apid) {
        accesspointRepository.delete(apid);
    }
}
