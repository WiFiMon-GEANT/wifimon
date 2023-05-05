package net.geant.wifimon.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Created by kokkinos on 8/3/2017.
 */
@Entity
@Table(name = "accesspoints")
public class Accesspoint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apid", nullable = false, updatable = false)
    private Long apid;

    @Column(name = "mac", nullable = false, unique = true)
    private String mac;

    @Column(name = "latitude", nullable = true)
    private String latitude;

    @Column(name = "longitude", nullable = true)
    private String longitude;

    @Column(name = "building", nullable = true)
    private String building;

    @Column(name = "floor", nullable = true)
    private String floor;

    @Column(name = "notes", nullable = true)
    private String notes;

    public Long getApid() {
        return apid;
    }

    public void setApid(Long apid) {
        this.apid = apid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Accesspoint accesspoint = (Accesspoint) o;

        if (mac != null ? !mac.equals(accesspoint.mac) : accesspoint.mac != null) return false;
        return mac == accesspoint.mac;

    }

    @Override
    public int hashCode() {
        return mac != null ? mac.hashCode() : 0;
    }
}
