package net.geant.wifimon.model.dto;

import java.io.Serializable;

/**
 * Created by nkostopoulos on 10/29/2019
 */

public class ProbesMeasurement implements Serializable {

    private Long timestamp;
    private Double bitRate;
    private Double txPower;
    private String linkQuality;
    private Double signalLevel;
    private String testTool;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getBitRate() {
        return bitRate;
    }

    public void setBitRate(Double bitRate) {
        this.bitRate = bitRate;
    }

    public Double getTxPower() {
        return txPower;
    }

    public void setTxPower(Double txPower) {
        this.txPower = txPower;
    }

    public String getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(String linkQuality) {
        this.linkQuality = linkQuality;
    }

    public Double getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(Double signalLevel) {
        this.signalLevel = signalLevel;
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

        ProbesMeasurement that = (ProbesMeasurement) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (bitRate != null ? !bitRate.equals(that.bitRate) : that.bitRate != null)
            return false;
        if (txPower != null ? !txPower.equals(that.txPower) : that.txPower != null)
            return false;
        if (linkQuality != null ? !linkQuality.equals(that.linkQuality) : that.linkQuality != null)
            return false;
        if (signalLevel != null ? !signalLevel.equals(that.signalLevel) : that.signalLevel != null)
            return false;
        return testTool != null ? testTool.equals(that.testTool) : that.testTool == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (bitRate != null ? bitRate.hashCode() : 0);
        result = 31 * result + (txPower != null ? txPower.hashCode() : 0);
        result = 31 * result + (linkQuality != null ? linkQuality.hashCode() : 0);
        result = 31 * result + (signalLevel != null ? signalLevel.hashCode() : 0);
        result = 31 * result + (testTool != null ? testTool.hashCode() : 0);
        return result;
    }
}
