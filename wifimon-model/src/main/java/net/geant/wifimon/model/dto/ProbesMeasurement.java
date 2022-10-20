package net.geant.wifimon.model.dto;

import java.io.Serializable;

/**
 * Created by nkostopoulos on 10/29/2019
 */

public class ProbesMeasurement implements Serializable {
    private String wts;
    private Long pingPacketTransmit;
    private Long pingPacketReceive;
    private Long pingPacketLossRate;
    private Long pingPacketLossCount;
    private Long pingRttMin;
    private Long pingRttAvg;
    private Long pingRttMax;
    private Long pingRttMdev;
    private Long pingPacketDuplicateRate;
    private Long pingPacketDuplicateCount;
    private String macAddress;
    private String accesspoint;
    private String essid;
    private Long bitRate;
    private Long txPower;
    private Long linkQuality;
    private Long signalLevel;
    private Long probeNo;
    private String monitor;
    private String system;
    private String numberOfUsers;
    private String locationName;
    private String testDeviceLocationDescription;
    private String nat;

    public String getWts() {
	    return wts;
    }

    public void setWts(String wts) {
	    this.wts = wts;
    }

    public Long getPingPacketTransmit() {
	    return pingPacketTransmit;
    }

    public void setPingPacketTransmit(Long pingPacketTransmit) {
	    this.pingPacketTransmit = pingPacketTransmit;
    }

    public Long getPingPacketReceive() {
	    return pingPacketReceive;
    }

    public void setPingPacketReceive(Long pingPacketReceive) {
	    this.pingPacketReceive = pingPacketReceive;
    }

    public Long getPingPacketLossRate() {
	    return pingPacketLossRate;
    }

    public void setPingPacketLossRate(Long pingPacketLossRate) {
	    this.pingPacketLossRate = pingPacketLossRate;
    }

    public Long getPingPacketLossCount() {
	    return pingPacketLossCount;
    }

    public void setPingPacketLossCount(Long pingPacketLossCount) {
	    this.pingPacketLossCount = pingPacketLossCount;
    }

    public Long getPingRttMin() {
	    return pingRttMin;
    }

    public void setPingRttMin(Long pingRttMin) {
	    this.pingRttMin = pingRttMin;
    }

    public Long getPingRttAvg() {
	    return pingRttAvg;
    }

    public void setPingRttAvg(Long pingRttAvg) {
	    this.pingRttAvg = pingRttAvg;
    }

    public Long getPingRttMax() {
	    return pingRttMax;
    }

    public void setPingRttMax(Long pingRttMax) {
	    this.pingRttMax = pingRttMax;
    }

    public Long getPingRttMdev() {
	    return pingRttMdev;
    }

    public void setPingRttMdev(Long pingRttMdev) {
	    this.pingRttMdev = pingRttMdev;
    }

    public Long getPingPacketDuplicateRate() {
	    return pingPacketDuplicateRate;
    }

    public void setPingPacketDuplicateRate(Long pingPacketDuplicateRate) {
	    this.pingPacketDuplicateRate = pingPacketDuplicateRate;
    }

    public Long getPingPacketDuplicateCount() {
	    return pingPacketDuplicateCount;
    }

    public void setPingPacketDuplicateCount(Long pingPacketDuplicateCount) {
	    this.pingPacketDuplicateCount = pingPacketDuplicateCount;
    }

    public String getMacAddress() {
	    return macAddress;
    }

    public void setMacAddress(String macAddress) {
	    this.macAddress = macAddress;
    }

    public String getAccesspoint() {
	    return accesspoint;
    }

    public void setAccesspoint(String accesspoint) {
	    this.accesspoint = accesspoint;
    }

    public String getEssid() {
	    return essid;
    }

    public void setEssid(String essid) {
	    this.essid = essid;
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

    public String getSystem() {
	    return system;
    }

    public void setSystem(String system) {
	    this.system = system;
    }

    public String getLocationName() {
	    return locationName;
    }
    
    public void setLocationName(String locationName) {
	    this.locationName = locationName;
    }

    public String getTestDeviceLocationDescription() {
	    return testDeviceLocationDescription;
    }

    public void setTestDeviceLocationDescription(String testDeviceLocationDescription) {
	    this.testDeviceLocationDescription = testDeviceLocationDescription;
    }

    public String getNumberOfUsers() {
	    return numberOfUsers;
    }

    public void setNumberOfUsers(String numberOfUsers) {
	    this.numberOfUsers = numberOfUsers;
    }
    
    public String getNat() {
	    return nat;
    }

    public void setNat(String nat) {
	    this.nat = nat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProbesMeasurement that = (ProbesMeasurement) o;
	if (wts != null ? !wts.equals(that.wts) : that.wts != null)
	    return false;
	if (pingPacketTransmit != null ? !pingPacketTransmit.equals(that.pingPacketTransmit) : that.pingPacketTransmit != null)
	    return false;
	if (pingPacketReceive != null ? !pingPacketReceive.equals(that.pingPacketReceive) : that.pingPacketReceive != null)
	    return false;
	if (pingPacketLossRate != null ? !pingPacketLossRate.equals(that.pingPacketLossRate) : that.pingPacketLossRate != null)
	    return false;
	if (pingPacketLossCount != null ? !pingPacketLossCount.equals(that.pingPacketLossCount) : that.pingPacketLossCount != null)
            return false;
	if (pingRttMin != null ? !pingRttMin.equals(that.pingRttMin) : that.pingRttMin != null)
	    return false;
	if (pingRttAvg != null ? !pingRttAvg.equals(that.pingRttAvg) : that.pingRttAvg != null)
	    return false;
	if (pingRttMax != null ? !pingRttMax.equals(that.pingRttMax) : that.pingRttMax != null)
            return false;
	if (pingRttMdev != null ? !pingRttMdev.equals(that.pingRttMdev) : that.pingRttMdev != null)
	    return false;
	if (pingPacketDuplicateRate != null ? !pingPacketDuplicateRate.equals(that.pingPacketDuplicateRate) : that.pingPacketDuplicateRate != null)
	    return false;
	if (pingPacketDuplicateCount != null ? !pingPacketDuplicateCount.equals(that.pingPacketDuplicateCount) : that.pingPacketDuplicateCount != null)
	    return false;
	if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null)
	    return false;
	if (accesspoint != null ? !accesspoint.equals(that.accesspoint) : that.accesspoint != null)
	    return false;
	if (essid != null ? !essid.equals(that.essid) : that.essid != null)
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
        if (monitor != null ? !monitor.equals(that.monitor) : that.monitor != null)
	    return false;
        if (system != null ? !system.equals(that.system) : that.system != null)
	    return false;
        if (locationName != null ? !locationName.equals(that.locationName) : that.locationName != null)
	    return false;
        if (testDeviceLocationDescription != null ? !testDeviceLocationDescription.equals(that.testDeviceLocationDescription) : that.testDeviceLocationDescription != null)
	    return false;
	if (numberOfUsers != null ? !numberOfUsers.equals(that.numberOfUsers) : that.numberOfUsers != null)
	    return false;
        return nat != null ? nat.equals(that.nat) : that.nat == null;
    }

    @Override
    public int hashCode() {
        int result = wts != null ? wts.hashCode() : 0;
	result = 31 * result + (pingPacketTransmit != null ? pingPacketTransmit.hashCode() : 0);
	result = 31 * result + (pingPacketReceive != null ? pingPacketReceive.hashCode() : 0);
	result = 31 * result + (pingPacketLossRate != null ? pingPacketLossRate.hashCode() : 0);
	result = 31 * result + (pingPacketLossCount != null ? pingPacketLossCount.hashCode() : 0);
	result = 31 * result + (pingRttMin != null ? pingRttMin.hashCode() : 0);
	result = 31 * result + (pingRttAvg != null ? pingRttAvg.hashCode() : 0);
	result = 31 * result + (pingRttMax != null ? pingRttMax.hashCode() : 0);
	result = 31 * result + (pingRttMdev != null ? pingRttMdev.hashCode() : 0);
	result = 31 * result + (pingPacketDuplicateRate != null ? pingPacketDuplicateRate.hashCode() : 0);
	result = 31 * result + (pingPacketDuplicateCount != null ? pingPacketDuplicateCount.hashCode() : 0);
	result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (accesspoint != null ? accesspoint.hashCode() : 0);
	result = 31 * result + (essid != null ? essid.hashCode() : 0);
        result = 31 * result + (bitRate != null ? bitRate.hashCode() : 0);
        result = 31 * result + (txPower != null ? txPower.hashCode() : 0);
        result = 31 * result + (linkQuality != null ? linkQuality.hashCode() : 0);
        result = 31 * result + (signalLevel != null ? signalLevel.hashCode() : 0);
        result = 31 * result + (probeNo != null ? probeNo.hashCode() : 0);
	result = 31 * result + (monitor != null ? monitor.hashCode() : 0);
	result = 31 * result + (system != null ? system.hashCode() : 0);
	result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
	result = 31 * result + (testDeviceLocationDescription != null ? testDeviceLocationDescription.hashCode() : 0);
	result = 31 * result + (numberOfUsers != null ? numberOfUsers.hashCode() : 0);
	result = 31 * result + (nat != null ? nat.hashCode() : 0);
        return result;
    }
}
