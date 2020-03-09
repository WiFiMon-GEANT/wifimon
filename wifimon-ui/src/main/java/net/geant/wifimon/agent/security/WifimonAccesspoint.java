package net.geant.wifimon.agent.security;

import net.geant.wifimon.model.entity.Accesspoint;

/**
 * Created by kokkinos on 9/3/2017.
 */
public class WifimonAccesspoint {

    private Accesspoint accesspoint;

    public WifimonAccesspoint(final Accesspoint accesspoint) {
        this.accesspoint = accesspoint;
    }

    public Accesspoint getAccesspoint() {
        return accesspoint;
    }

    public Long getApid() {
        return accesspoint.getApid();
    }

    public String getMac() {
        return accesspoint.getMac();
    }

    public String getLatitude() {
        return accesspoint.getLatitude();
    }

    public String getLongitude() {
        return accesspoint.getLongitude();
    }

    public String getBuilding() {
        return accesspoint.getBuilding();
    }

    public String getFloor() {
        return accesspoint.getFloor();
    }

    public String getNotes() {
        return accesspoint.getNotes();
    }
}
