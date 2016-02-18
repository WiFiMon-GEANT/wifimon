package net.geant.wifimon.processor.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by kanakisn on 09/11/15.
 */
@XmlRootElement
public class NetTestMeasurement implements Serializable {

    @XmlElement
    private Long date;
    @XmlElement
    private Double downloadThroughput;
    @XmlElement
    private Double uploadThroughput;
    @XmlElement
    private Double localPing;
    @XmlElement
    private String latitude;
    @XmlElement
    private String longitude;
    @XmlElement
    private String locationMethod;
    @XmlElement
    private String clientIp;
    @XmlElement
    private String userAgent;
    @XmlElement
    private String username;
    @XmlElement
    private String callingStationId;
    @XmlElement
    private String calledStationId;
    @XmlElement
    private String nasPortType;
    @XmlElement
    private String nasIpAddress;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getNasPortType() {
        return nasPortType;
    }

    public void setNasPortType(String nasPortType) {
        this.nasPortType = nasPortType;
    }

    public String getNasIpAddress() {
        return nasIpAddress;
    }

    public void setNasIpAddress(String nasIpAddress) {
        this.nasIpAddress = nasIpAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NetTestMeasurement that = (NetTestMeasurement) o;

        if (date != null ? !date.equals(that.date) : that.date != null)
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
        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (callingStationId != null ? !callingStationId.equals(that.callingStationId) : that.callingStationId != null)
            return false;
        if (calledStationId != null ? !calledStationId.equals(that.calledStationId) : that.calledStationId != null)
            return false;
        if (nasPortType != null ? !nasPortType.equals(that.nasPortType) : that.nasPortType != null)
            return false;
        return nasIpAddress != null ? nasIpAddress.equals(that.nasIpAddress) : that.nasIpAddress == null;

    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (downloadThroughput != null ? downloadThroughput.hashCode() : 0);
        result = 31 * result + (uploadThroughput != null ? uploadThroughput.hashCode() : 0);
        result = 31 * result + (localPing != null ? localPing.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (locationMethod != null ? locationMethod.hashCode() : 0);
        result = 31 * result + (clientIp != null ? clientIp.hashCode() : 0);
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (callingStationId != null ? callingStationId.hashCode() : 0);
        result = 31 * result + (calledStationId != null ? calledStationId.hashCode() : 0);
        result = 31 * result + (nasPortType != null ? nasPortType.hashCode() : 0);
        result = 31 * result + (nasIpAddress != null ? nasIpAddress.hashCode() : 0);
        return result;
    }

}