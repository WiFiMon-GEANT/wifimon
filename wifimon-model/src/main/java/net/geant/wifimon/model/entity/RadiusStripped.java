package net.geant.wifimon.model.entity;

import java.io.Serializable;

/**
 * Created by kokkinos on 20/10/2017.
 */

public class RadiusStripped implements Serializable {

    private String userName;
    private String timestamp;
    private String nasPort;
    private String sourceHost;
    private String callingStationId;
    private String result;
    private String traceId;
    private String nasIdentifier;
    private String calledStationId;
    private String framedIpAddress;
    private String nasIpAddress;
    private String acctStatusType;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNasPort() {
        return nasPort;
    }

    public void setNasPort(String nasPort) {
        this.nasPort = nasPort;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public String getCallingStationId() {
        return callingStationId;
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getNasIdentifier() {
        return nasIdentifier;
    }

    public void setNasIdentifier(String nasIdentifier) {
        this.nasIdentifier = nasIdentifier;
    }

    public String getCalledStationId() {
        return calledStationId;
    }

    public void setCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
    }

    public String getFramedIpAddress() {
        return framedIpAddress;
    }

    public void setFramedIpAddress(String framedIpAddress) {
        this.framedIpAddress = framedIpAddress;
    }

    public String getNasIpAddress() {
        return nasIpAddress;
    }

    public void setNasIpAddress(String nasIpAddress) {
        this.nasIpAddress = nasIpAddress;
    }

    public String getAcctStatusType() {
        return acctStatusType;
    }

    public void setAcctStatusType(String acctStatusType) {
        this.acctStatusType = acctStatusType;
    }
}

