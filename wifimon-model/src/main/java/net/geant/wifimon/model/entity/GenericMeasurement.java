package net.geant.wifimon.model.entity;

import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kanakisn on 8/5/15.
 */

@Entity
@Table(name = "measurements")
public class GenericMeasurement implements Serializable {

    private Long id;
    private Date date;
    private Double downloadRate;
    private Double uploadRate;
    private Double localPing;
    private Double latitude;
    private Double longitude;
    private String locationMethod;
    private String clientIp;
    private String userAgent;
    private Date startTime;
    private String username;
    private String framedIpAddress;
    private String sessionId;
    private String callingStationId;
    private String calledStationId;
    private String nasPortId;
    private String nasPortType;
    private String nasIpAddress;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "measurement_date")
//    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "measurement_download_rate")
    public Double getDownloadRate() {
        return downloadRate;
    }

    public void setDownloadRate(Double downloadRate) {
        this.downloadRate = downloadRate;
    }

    @Column(name = "measurement_upload_rate")
    public Double getUploadRate() {
        return uploadRate;
    }

    public void setUploadRate(Double uploadRate) {
        this.uploadRate = uploadRate;
    }

    @Column(name = "measurement_local_ping")
    public Double getLocalPing() {
        return localPing;
    }

    public void setLocalPing(Double localPing) {
        this.localPing = localPing;
    }

    @Column(name = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Column(name = "location_method")
    public String getLocationMethod() {
        return locationMethod;
    }

    public void setLocationMethod(String locationMethod) {
        this.locationMethod = locationMethod;
    }

    @Column(name = "client_ip")
//    @ColumnTransformer(read="CAST(inet AS varchar)", write="CAST(? AS inet)")
    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Column(name = "user_agent")
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Column(name = "acctstarttime")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "framedipaddress")
    public String getFramedIpAddress() {
        return framedIpAddress;
    }

    public void setFramedIpAddress(String framedIpAddress) {
        this.framedIpAddress = framedIpAddress;
    }

    @Column(name = "acctsessionid")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name = "callingstationid")
    public String getCallingStationId() {
        return callingStationId;
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    @Column(name = "calledstationid")
    public String getCalledStationId() {
        return calledStationId;
    }

    public void setCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
    }

    @Column(name = "nasportid")
    public String getNasPortId() {
        return nasPortId;
    }

    public void setNasPortId(String nasPortId) {
        this.nasPortId = nasPortId;
    }

    @Column(name = "nasporttype")
    public String getNasPortType() {
        return nasPortType;
    }

    public void setNasPortType(String nasPortType) {
        this.nasPortType = nasPortType;
    }

    @Column(name = "nasipaddress")
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

        GenericMeasurement that = (GenericMeasurement) o;

        if (date != null ? !date.equals(that.date) : that.date != null)
            return false;
        if (downloadRate != null ? !downloadRate.equals(that.downloadRate) : that.downloadRate != null)
            return false;
        if (uploadRate != null ? !uploadRate.equals(that.uploadRate) : that.uploadRate != null)
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
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (framedIpAddress != null ? !framedIpAddress.equals(that.framedIpAddress) : that.framedIpAddress != null)
            return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null)
            return false;
        if (callingStationId != null ? !callingStationId.equals(that.callingStationId) : that.callingStationId != null)
            return false;
        if (calledStationId != null ? !calledStationId.equals(that.calledStationId) : that.calledStationId != null)
            return false;
        if (nasPortId != null ? !nasPortId.equals(that.nasPortId) : that.nasPortId != null)
            return false;
        if (nasPortType != null ? !nasPortType.equals(that.nasPortType) : that.nasPortType != null)
            return false;
        return nasIpAddress != null ? nasIpAddress.equals(that.nasIpAddress) : that.nasIpAddress == null;

    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (downloadRate != null ? downloadRate.hashCode() : 0);
        result = 31 * result + (uploadRate != null ? uploadRate.hashCode() : 0);
        result = 31 * result + (localPing != null ? localPing.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (locationMethod != null ? locationMethod.hashCode() : 0);
        result = 31 * result + (clientIp != null ? clientIp.hashCode() : 0);
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (framedIpAddress != null ? framedIpAddress.hashCode() : 0);
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (callingStationId != null ? callingStationId.hashCode() : 0);
        result = 31 * result + (calledStationId != null ? calledStationId.hashCode() : 0);
        result = 31 * result + (nasPortId != null ? nasPortId.hashCode() : 0);
        result = 31 * result + (nasPortType != null ? nasPortType.hashCode() : 0);
        result = 31 * result + (nasIpAddress != null ? nasIpAddress.hashCode() : 0);
        return result;
    }
}


