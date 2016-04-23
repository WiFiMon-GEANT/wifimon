package net.geant.wifimon.model.entity;

import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by kanakisn on 17/02/16.
 */

@Entity
@Table(name = "radacct")
public class Radius implements Serializable {

    private Long radAcctId;
    private String sessionId;
    private String uniqueId;
    private String username;
    private String groupname;
    private String realm;
    private String nasIpAddress;
    private String nasPortId;
    private String nasPortType;
    private Date startTime;
    private Date stopTime;
    private Long sessionTime;
    private String authentic;
    private String connectInfoStart;
    private String connectInfoStop;
    private BigInteger inputOctets;
    private BigInteger outputOctets;
    private String calledStationId;
    private String callingStationId;
    private String terminateCause;
    private String serviceType;
    private String xascendsessionsvrKey;
    private String framedProtocol;
    private String framedIpAddress;
    private Long startDelay;
    private Long stopDelay;

    @Id
    @Column(name = "radacctid")
    public Long getRadAcctId() {
        return radAcctId;
    }

    public void setRadAcctId(Long radAcctId) {
        this.radAcctId = radAcctId;
    }

    @Column(name = "acctsessionid")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name = "acctuniqueid")
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "groupname")
    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Column(name = "realm")
    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    @Column(name="nasipaddress")
    @ColumnTransformer(read="CAST(inet AS varchar)", write="CAST(? AS inet)")
    public String getNasIpAddress() {
        return nasIpAddress;
    }

    public void setNasIpAddress(String nasIpAddress) {
        this.nasIpAddress = nasIpAddress;
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

    @Column(name = "acctstarttime")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "acctstoptime")
    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    @Column(name = "acctsessiontime")
    public Long getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(Long sessionTime) {
        this.sessionTime = sessionTime;
    }

    @Column(name = "acctauthentic")
    public String getAuthentic() {
        return authentic;
    }

    public void setAuthentic(String authentic) {
        this.authentic = authentic;
    }

    @Column(name = "connectinfo_start")
    public String getConnectInfoStart() {
        return connectInfoStart;
    }

    public void setConnectInfoStart(String connectInfoStart) {
        this.connectInfoStart = connectInfoStart;
    }

    @Column(name = "connectinfo_stop")
    public String getConnectInfoStop() {
        return connectInfoStop;
    }

    public void setConnectInfoStop(String connectInfoStop) {
        this.connectInfoStop = connectInfoStop;
    }

    @Column(name = "acctinputoctets")
    public BigInteger getInputOctets() {
        return inputOctets;
    }

    public void setInputOctets(BigInteger inputOctets) {
        this.inputOctets = inputOctets;
    }

    @Column(name = "acctoutputoctets")
    public BigInteger getOutputOctets() {
        return outputOctets;
    }

    public void setOutputOctets(BigInteger outputOctets) {
        this.outputOctets = outputOctets;
    }

    @Column(name = "calledstationid")
    public String getCalledStationId() {
        return calledStationId;
    }

    public void setCalledStationId(String calledStationId) {
        this.calledStationId = calledStationId;
    }

    @Column(name = "callingstationid")
    public String getCallingStationId() {
        return callingStationId;
    }

    public void setCallingStationId(String callingStationId) {
        this.callingStationId = callingStationId;
    }

    @Column(name = "acctterminatecause")
    public String getTerminateCause() {
        return terminateCause;
    }

    public void setTerminateCause(String terminateCause) {
        this.terminateCause = terminateCause;
    }

    @Column(name = "servicetype")
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Column(name = "xascendsessionsvrkey")
    public String getXascendsessionsvrKey() {
        return xascendsessionsvrKey;
    }

    public void setXascendsessionsvrKey(String xascendsessionsvrKey) {
        this.xascendsessionsvrKey = xascendsessionsvrKey;
    }

    @Column(name = "framedprotocol")
    public String getFramedProtocol() {
        return framedProtocol;
    }

    public void setFramedProtocol(String framedProtocol) {
        this.framedProtocol = framedProtocol;
    }

    @Column(name="framedipaddress")
    @ColumnTransformer(read="CAST(inet AS varchar)", write="CAST(? AS inet)")
    public String getFramedIpAddress() {
        return framedIpAddress;
    }

    public void setFramedIpAddress(String framedIpAddress) {
        this.framedIpAddress = framedIpAddress;
    }

    @Column(name = "acctstartdelay")
    public Long getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(Long startDelay) {
        this.startDelay = startDelay;
    }

    @Column(name = "acctstopdelay")
    public Long getStopDelay() {
        return stopDelay;
    }

    public void setStopDelay(Long stopDelay) {
        this.stopDelay = stopDelay;
    }

}
