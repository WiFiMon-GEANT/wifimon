package net.geant.wifimon.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "measurementscount", nullable = true)
    private Integer measurementscount;

    @Column(name = "downloadavg", nullable = true)
    private Double downloadavg;

    @Column(name = "downloadmin", nullable = true)
    private Double downloadmin;

    @Column(name = "downloadmax", nullable = true)
    private Double downloadmax;

    @Column(name = "uploadavg", nullable = true)
    private Double uploadavg;

    @Column(name = "uploadmin", nullable = true)
    private Double uploadmin;

    @Column(name = "uploadmax", nullable = true)
    private Double uploadmax;

    @Column(name = "pingavg", nullable = true)
    private Double pingavg;

    @Column(name = "pingmin", nullable = true)
    private Double pingmin;

    @Column(name = "pingmax", nullable = true)
    private Double pingmax;


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

    public Integer getMeasurementscount() {
        return measurementscount;
    }

    public void setMeasurementscount(Integer measurementscount) {
        this.measurementscount = measurementscount;
    }

    public Double getDownloadavg() {
        return downloadavg;
    }

    public void setDownloadavg(Double downloadavg) {
        this.downloadavg = downloadavg;
    }

    public Double getDownloadmin() {
        return downloadmin;
    }

    public void setDownloadmin(Double downloadmin) {
        this.downloadmin = downloadmin;
    }

    public Double getDownloadmax() {
        return downloadmax;
    }

    public void setDownloadmax(Double downloadmax) {
        this.downloadmax = downloadmax;
    }

    public Double getUploadavg() {
        return uploadavg;
    }

    public void setUploadavg(Double uploadavg) {
        this.uploadavg = uploadavg;
    }

    public Double getUploadmin() {
        return uploadmin;
    }

    public void setUploadmin(Double uploadmin) {
        this.uploadmin = uploadmin;
    }

    public Double getUploadmax() {
        return uploadmax;
    }

    public void setUploadmax(Double uploadmax) {
        this.uploadmax = uploadmax;
    }

    public Double getPingavg() {
        return pingavg;
    }

    public void setPingavg(Double pingavg) {
        this.pingavg = pingavg;
    }

    public Double getPingmin() {
        return pingmin;
    }

    public void setPingmin(Double pingmin) {
        this.pingmin = pingmin;
    }

    public Double getPingmax() {
        return pingmax;
    }

    public void setPingmax(Double pingmax) {
        this.pingmax = pingmax;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Accesspoint accesspoint = (Accesspoint) o;

        if (mac != null ? !mac.equals(accesspoint.mac) : accesspoint.mac!= null) return false;
        return mac == accesspoint.mac;

    }

    @Override
    public int hashCode() {
        return mac != null ? mac.hashCode() : 0;
    }
}
