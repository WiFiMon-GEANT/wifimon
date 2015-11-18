package net.geant.wifimon.agent.dto;

import java.io.Serializable;

/**
 * Created by kanakisn on 09/11/15.
 */
public class NetTestMeasurement implements Serializable {

    private Long date;
    private Double downloadThroughput;
    private Double uploadThroughput;
    private Double localPing;
    private String latitude;
    private String longitude;
    private String locationMethod;
    private String clientIp;
    private String userAgent;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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

}