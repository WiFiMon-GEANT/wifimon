package net.geant.wifimon.model.dto;

import java.io.Serializable;

/**
 * Created by kokkinos on 2/11/2017.
 */

public class AggregatedMeasurement implements Serializable {

    private Long timestamp;
    private Double downloadThroughput;
    private Double uploadThroughput;
    private Double localPing;
    private Double latitude;
    private Double longitude;
    private String locationMethod;
    private String clientIp;
    private String userAgent;
    private String testTool;
    private String nasPort;
    private String callingStationId;
    private String nasIdentifier;
    private String calledStationId;
    private String nasIpAddress;
    private String apBuilding;
    private String apFloor;
    private Double apLatitude;
    private Double apLongitude;
    private String apNotes;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDownloadThroughput() {
        return downloadThroughput;
    }

    public void setDownloadThroughput(Double downloadThroughput) {
        this.downloadThroughput = downloadThroughput;
    }

    public Double getUploadThroughput() {
        return uploadThroughput;
    }

    public void setUploadThroughput(Double uploadThroughput) {
        this.uploadThroughput = uploadThroughput;
    }

    public Double getLocalPing() {
        return localPing;
    }

    public void setLocalPing(Double localPing) {
        this.localPing = localPing;
    }

    public String getLocationMethod() {
        return locationMethod;
    }

    public void setLocationMethod(String locationMethod) {
        this.locationMethod = locationMethod;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }


    public String getCallingStationId() {
        return callingStationId;
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    public String getCalledStationId() {
        return calledStationId;
    }

    public void setCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
    }

    public String getNasIpAddress() {
        return nasIpAddress;
    }

    public void setNasIpAddress(String nasIpAddress) {
        this.nasIpAddress = nasIpAddress;
    }

    public String getTestTool() {
        return testTool;
    }

    public void setTestTool(String testTool) {
        this.testTool = testTool;
    }

    public String getNasPort() {
        return nasPort;
    }

    public void setNasPort(String nasPort) {
        this.nasPort = nasPort;
    }

    public String getNasIdentifier() {
        return nasIdentifier;
    }

    public void setNasIdentifier(String nasIdentifier) {
        this.nasIdentifier = nasIdentifier;
    }

    public Double getApLatitude() {
        return apLatitude;
    }

    public void setApLatitude(Double apLatitude) {
        this.apLatitude = apLatitude;
    }

    public Double getApLongitude() {
        return apLongitude;
    }

    public void setApLongitude(Double apLongitude) {
        this.apLongitude = apLongitude;
    }

    public String getApBuilding() {
        return apBuilding;
    }

    public void setApBuilding(String apBuilding) {
        this.apBuilding = apBuilding;
    }

    public String getApFloor() {
        return apFloor;
    }

    public void setApFloor(String apFloor) {
        this.apFloor = apFloor;
    }

    public String getApNotes() {
        return apNotes;
    }

    public void setApNotes(String apNotes) {
        this.apNotes = apNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AggregatedMeasurement that = (AggregatedMeasurement) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (downloadThroughput != null ? !downloadThroughput.equals(that.downloadThroughput) : that.downloadThroughput != null)
            return false;
        if (uploadThroughput != null ? !uploadThroughput.equals(that.uploadThroughput) : that.uploadThroughput != null)
            return false;
        if (localPing != null ? !localPing.equals(that.localPing) : that.localPing != null)
            return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null)
            return false;
        if (locationMethod != null ? !locationMethod.equals(that.locationMethod) : that.locationMethod != null)
            return false;
        if (clientIp != null ? !clientIp.equals(that.clientIp) : that.clientIp != null)
            return false;
        if (userAgent != null ? !userAgent.equals(that.userAgent) : that.userAgent != null)
            return false;
        if (testTool != null ? !testTool.equals(that.testTool) : that.testTool != null)
            return false;
        if (nasPort != null ? !nasPort.equals(that.nasPort) : that.nasPort != null)
            return false;
        if (callingStationId != null ? !callingStationId.equals(that.callingStationId) : that.callingStationId != null)
            return false;
        if (nasIdentifier != null ? !nasIdentifier.equals(that.nasIdentifier) : that.nasIdentifier != null)
            return false;
        if (calledStationId != null ? !calledStationId.equals(that.calledStationId) : that.calledStationId != null)
            return false;
        if (apBuilding != null ? !apBuilding.equals(that.apBuilding) : that.apBuilding != null)
            return false;
        if (apFloor != null ? !apFloor.equals(that.apFloor) : that.apFloor != null)
            return false;
        if (apLatitude != null ? !apLatitude.equals(that.apLatitude) : that.apLatitude != null)
            return false;
        if (apLongitude != null ? !apLongitude.equals(that.apLongitude) : that.apLongitude != null)
            return false;
        if (apNotes != null ? !apNotes.equals(that.apNotes) : that.apNotes != null)
            return false;
        return nasIpAddress != null ? nasIpAddress.equals(that.nasIpAddress) : that.nasIpAddress == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (downloadThroughput != null ? downloadThroughput.hashCode() : 0);
        result = 31 * result + (uploadThroughput != null ? uploadThroughput.hashCode() : 0);
        result = 31 * result + (localPing != null ? localPing.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (locationMethod != null ? locationMethod.hashCode() : 0);
        result = 31 * result + (clientIp != null ? clientIp.hashCode() : 0);
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (testTool != null ? testTool.hashCode() : 0);
        result = 31 * result + (nasPort != null ? nasPort.hashCode() : 0);
        result = 31 * result + (callingStationId != null ? callingStationId.hashCode() : 0);
        result = 31 * result + (nasIdentifier != null ? nasIdentifier.hashCode() : 0);
        result = 31 * result + (calledStationId != null ? calledStationId.hashCode() : 0);
        result = 31 * result + (nasIpAddress != null ? nasIpAddress.hashCode() : 0);
        result = 31 * result + (apBuilding != null ? apBuilding.hashCode() : 0);
        result = 31 * result + (apFloor != null ? apFloor.hashCode() : 0);
        result = 31 * result + (apLatitude != null ? apLatitude.hashCode() : 0);
        result = 31 * result + (apLongitude != null ? apLongitude.hashCode() : 0);
        result = 31 * result + (apNotes != null ? apNotes.hashCode() : 0);
        return result;
    }
}
