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

}