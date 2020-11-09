package net.geant.wifimon.model.dto;

import java.io.Serializable;

/**
 * Created by nkostopoulos on 10/29/2019
 */

public class ProbesMeasurement implements Serializable {

    private String accesspoint;
    private Long bitRate;
    private Long txPower;
    private Long linkQuality;
    private Long signalLevel;
    private Long probeNo;
    private String monitor;

    public String getAccesspoint() {
	    return accesspoint;
    }

    public void setAccesspoint(String accesspoint) {
	    this.accesspoint = accesspoint;
    }

    public Long getBitRate() {
        return bitRate;
    }

    public void setBitRate(Long bitRate) {
        this.bitRate = bitRate;
    }

    public Long getTxPower() {
        return txPower;
    }

    public void setTxPower(Long txPower) {
        this.txPower = txPower;
    }

    public Long getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(Long linkQuality) {
        this.linkQuality = linkQuality;
    }

    public Long getSignalLevel() {
        return signalLevel;
    }

    public void setSignalLevel(Long signalLevel) {
        this.signalLevel = signalLevel;
    }

    public Long getProbeNo() {
        return probeNo;
    }

    public void setProbeNo(Long probeNo) {
        this.probeNo = probeNo;
    }

    public String getMonitor() {
	    return monitor;
    }

    public void setMonitor(String monitor) {
	    this.monitor = monitor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProbesMeasurement that = (ProbesMeasurement) o;

	if (accesspoint != null ? !accesspoint.equals(that.accesspoint) : that.accesspoint != null)
	    return false;
        if (bitRate != null ? !bitRate.equals(that.bitRate) : that.bitRate != null)
            return false;
        if (txPower != null ? !txPower.equals(that.txPower) : that.txPower != null)
            return false;
        if (linkQuality != null ? !linkQuality.equals(that.linkQuality) : that.linkQuality != null)
            return false;
        if (signalLevel != null ? !signalLevel.equals(that.signalLevel) : that.signalLevel != null)
            return false;
        if (probeNo != null ? !probeNo.equals(that.probeNo) : that.probeNo != null)
            return false;
        return monitor != null ? monitor.equals(that.monitor) : that.monitor == null;
    }

    @Override
    public int hashCode() {
        int result = accesspoint != null ? accesspoint.hashCode() : 0;
        result = 31 * result + (bitRate != null ? bitRate.hashCode() : 0);
        result = 31 * result + (txPower != null ? txPower.hashCode() : 0);
        result = 31 * result + (linkQuality != null ? linkQuality.hashCode() : 0);
        result = 31 * result + (signalLevel != null ? signalLevel.hashCode() : 0);
        result = 31 * result + (probeNo != null ? probeNo.hashCode() : 0);
	result = 31 * result + (monitor != null ? monitor.hashCode() : 0);
        return result;
    }
}
