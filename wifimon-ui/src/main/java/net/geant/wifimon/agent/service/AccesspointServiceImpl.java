package net.geant.wifimon.agent.service;

import net.geant.wifimon.agent.model.AccesspointCreateFormModel;
import net.geant.wifimon.agent.model.AccesspointCreateModel;
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
    public Accesspoint create(AccesspointCreateModel ap) {
        Accesspoint accesspoint = new Accesspoint();
        accesspoint.setApid(ap.getApid());
        accesspoint.setMac(ap.getMac());
        accesspoint.setLatitude(ap.getLatitude());
        accesspoint.setLongitude(ap.getLongitude());
        accesspoint.setBuilding(ap.getBuilding());
        accesspoint.setFloor(ap.getFloor());
        accesspoint.setNotes(ap.getNotes());
        accesspoint.setMeasurementscount(ap.getMeasurementscount());
        accesspoint.setDownloadavg(ap.getDownloadavg());
        accesspoint.setDownloadmin(ap.getDownloadmin());
        accesspoint.setDownloadmax(ap.getDownloadmax());
        accesspoint.setUploadavg(ap.getUploadavg());
        accesspoint.setUploadmin(ap.getUploadmin());
        accesspoint.setUploadmax(ap.getUploadmax());
        accesspoint.setPingavg(ap.getPingavg());
        accesspoint.setPingmin(ap.getPingmin());
        accesspoint.setPingmax(ap.getPingmax());
        return accesspointRepository.save(accesspoint);
    }

    @Override
    public void delete(Long apid) {
        accesspointRepository.delete(apid);
    }
}
