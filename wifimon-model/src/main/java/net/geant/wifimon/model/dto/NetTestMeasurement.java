package net.geant.wifimon.model.dto;

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
    private String testTool;

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

    public String getTestTool() {
        return testTool;
    }

    public void setTestTool(String testTool) {
        this.testTool = testTool;
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
        return testTool != null ? testTool.equals(that.testTool) : that.testTool == null;

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
        result = 31 * result + (testTool != null ? testTool.hashCode() : 0);
        return result;
    }

}