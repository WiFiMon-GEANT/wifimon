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
    private String radiusTimestamp;
    private String serviceType;
    private String nasPortId;
    private String nasPortType;
    private String acctSessionId;
    private String acctMultiSessionId;
    private String callingStationId;
    private String calledStationId;
    private String acctAuthentic;
    private String acctStatusType;
    private String nasIdentifier;
    private String acctDelayTime;
    private String nasIpAddress;
    private String framedIpAddress;
    private String acctUniqueSessionId;
    private String realm;
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

    public String getTestTool() {
        return testTool;
    }

    public void setTestTool(String testTool) {
        this.testTool = testTool;
    }
   
    public String getRadiusTimestamp() {
	    return radiusTimestamp;
    }

    public void setRadiusTimestamp(String radiusTimestamp) {
	    this.radiusTimestamp = radiusTimestamp;
    }

    public String getServiceType() {
	    return serviceType;
    }

    public void setServiceType(String serviceType) {
	    this.serviceType = serviceType;
    }

    public String getNasPortId() {
	    return nasPortId;
    }

    public void setNasPortId(String nasPortId) {
	    this.nasPortId = nasPortId;
    }

    public String getNasPortType() {
	    return nasPortType;
    }

    public void setNasPortType(String nasPortType) {
	    this.nasPortType = nasPortType;
    }

    public String getAcctSessionId() {
	    return acctSessionId;
    }
   
    public void setAcctSessionId(String acctSessionId) {
	    this.acctSessionId = acctSessionId;
    }

    public String getAcctMultiSessionId() {
	    return acctMultiSessionId;
    }

    public void setAcctMultiSessionId(String acctMultiSessionId) {
	    this.acctMultiSessionId = acctMultiSessionId;
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

    public String getAcctAuthentic() {
	    return acctAuthentic;
    }

    public void setAcctAuthentic(String acctAuthentic) {
	    this.acctAuthentic = acctAuthentic;
    }

    public String getAcctStatusType() {
	    return acctStatusType;
    }

    public void setAcctStatusType(String acctStatusType) {
	    this.acctStatusType = acctStatusType;
    }

    public String getNasIdentifier() {
	    return nasIdentifier;
    }

    public void setNasIdentifier(String nasIdentifier) {
	    this.nasIdentifier = nasIdentifier;
    }

    public String getAcctDelayTime() {
	    return acctDelayTime;
    }

    public void setAcctDelayTime(String acctDelayTime) {
	    this.acctDelayTime = acctDelayTime;
    }

    public String getNasIpAddress() {
        return nasIpAddress;
    }

    public void setNasIpAddress(String nasIpAddress) {
        this.nasIpAddress = nasIpAddress;
    }

    public String getFramedIpAddress() {
	    return framedIpAddress;
    }

    public void setFramedIpAddress(String framedIpAddress) {
	    this.framedIpAddress = framedIpAddress;
    }

    public String getAcctUniqueSessionId() {
	    return acctUniqueSessionId;
    }

    public void setAcctUniqueSessionId(String acctUniqueSessionId) {
	    this.acctUniqueSessionId = acctUniqueSessionId;
    }

    public String getRealm() {
	    return realm;
    }

    public void setRealm(String realm) {
	    this.realm = realm;
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
        if (radiusTimestamp != null ? !radiusTimestamp.equals(that.radiusTimestamp) : that.radiusTimestamp != null)
	    return false;
        if (serviceType != null ? !serviceType.equals(that.serviceType) : that.serviceType != null)
	    return false;
        if (nasPortId != null ? !nasPortId.equals(that.nasPortId) : that.nasPortId != null)
	    return false;
        if (nasPortType != null ? !nasPortType.equals(that.nasPortType) : that.nasPortType != null)
	    return false;
        if (acctSessionId != null ? !acctSessionId.equals(that.acctSessionId) : that.acctSessionId != null)
	    return false;
        if (acctMultiSessionId != null ? !acctMultiSessionId.equals(that.acctMultiSessionId) : that.acctMultiSessionId != null)
	    return false;
        if (callingStationId != null ? !callingStationId.equals(that.callingStationId) : that.callingStationId != null)
            return false;
        if (calledStationId != null ? !calledStationId.equals(that.calledStationId) : that.calledStationId != null)
            return false;
        if (acctAuthentic != null ? !acctAuthentic.equals(that.acctAuthentic) : that.acctAuthentic != null)
            return false;
        if (acctStatusType != null ? !acctStatusType.equals(that.acctStatusType) : that.acctStatusType != null)
            return false;
        if (nasIdentifier != null ? !nasIdentifier.equals(that.nasIdentifier) : that.nasIdentifier != null)
            return false;
        if (acctDelayTime != null ? !acctDelayTime.equals(that.acctDelayTime) : that.acctDelayTime != null)
            return false;
        if (framedIpAddress != null ? !framedIpAddress.equals(that.framedIpAddress) : that.framedIpAddress != null)
            return false;
        if (acctUniqueSessionId != null ? !acctUniqueSessionId.equals(that.acctUniqueSessionId) : that.acctUniqueSessionId != null)
            return false;
        if (realm != null ? !realm.equals(that.realm) : that.realm != null)
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
        result = 31 * result + (radiusTimestamp != null ? radiusTimestamp.hashCode() : 0);
        result = 31 * result + (serviceType != null ? serviceType.hashCode() : 0);
        result = 31 * result + (nasPortId != null ? nasPortId.hashCode() : 0);
        result = 31 * result + (nasPortType != null ? nasPortType.hashCode() : 0);
        result = 31 * result + (acctSessionId != null ? acctSessionId.hashCode() : 0);
        result = 31 * result + (acctMultiSessionId != null ? acctMultiSessionId.hashCode() : 0);
        result = 31 * result + (callingStationId != null ? callingStationId.hashCode() : 0);
        result = 31 * result + (calledStationId != null ? calledStationId.hashCode() : 0);
        result = 31 * result + (acctAuthentic != null ? acctAuthentic.hashCode() : 0);
        result = 31 * result + (acctStatusType != null ? acctStatusType.hashCode() : 0);
        result = 31 * result + (nasIdentifier != null ? nasIdentifier.hashCode() : 0);
        result = 31 * result + (acctDelayTime != null ? acctDelayTime.hashCode() : 0);
        result = 31 * result + (nasIpAddress != null ? nasIpAddress.hashCode() : 0);
        result = 31 * result + (framedIpAddress != null ? framedIpAddress.hashCode() : 0);
        result = 31 * result + (acctUniqueSessionId != null ? acctUniqueSessionId.hashCode() : 0);
	result = 31 * result + (realm != null ? realm.hashCode() : 0);
        result = 31 * result + (apBuilding != null ? apBuilding.hashCode() : 0);
        result = 31 * result + (apFloor != null ? apFloor.hashCode() : 0);
        result = 31 * result + (apLatitude != null ? apLatitude.hashCode() : 0);
        result = 31 * result + (apLongitude != null ? apLongitude.hashCode() : 0);
        result = 31 * result + (apNotes != null ? apNotes.hashCode() : 0);
        return result;
    }
}
