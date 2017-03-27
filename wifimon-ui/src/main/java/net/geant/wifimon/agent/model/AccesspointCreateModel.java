package net.geant.wifimon.agent.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kokkinos on 21/3/2017.
 */
public class AccesspointCreateModel implements Serializable {
    @NotNull
    private Long apid;

    @NotNull
    private String mac;

    private String latitude;
    private String longitude;
    private String building;
    private String floor;
    private String notes;
    private Integer measurementscount;
    private Double downloadavg;
    private Double downloadmin;
    private Double downloadmax;
    private Double uploadmin;
    private Double uploadmax;
    private Double uploadavg;
    private Double pingavg;
    private Double pingmin;
    private Double pingmax;


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

    public Double getUploadavg() {
        return uploadavg;
    }

    public void setUploadavg(Double uploadavg) {
        this.uploadavg = uploadavg;
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

    public Long getApid() {
        return apid;
    }

    public void setApid(Long apid) {
        this.apid = apid;
    }
}
