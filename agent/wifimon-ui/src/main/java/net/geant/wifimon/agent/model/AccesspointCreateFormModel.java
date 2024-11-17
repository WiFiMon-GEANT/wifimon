package net.geant.wifimon.agent.model;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kokkinos on 8/3/2017.
 */
public class AccesspointCreateFormModel implements Serializable {
    @NotNull
    private String mac;

    private String latitude;
    private String longitude;
    private String building;
    private String floor;
    private String notes;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac.toUpperCase().replace("-", ":");
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
