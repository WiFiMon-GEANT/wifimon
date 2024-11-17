package net.geant.wifimon.model.entity;

import java.io.Serializable;

/**
 * Created by kokkinos on 20/10/2017.
 */

public class RadiusStripped implements Serializable {
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

}
