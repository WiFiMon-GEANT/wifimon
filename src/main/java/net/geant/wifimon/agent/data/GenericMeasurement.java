package net.geant.wifimon.agent.data;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
    private String clientMac;
    private String mac;
    private String dhcpEntry;
    private String authEntry;
    private Set<MeasurementAuthDetails> measurementAuthDetails;
    private String auth_user_name;
    private String auth_packet_type;
    private String auth_called_station_id;
    private String auth_connect_info;
    private String auth_nas_idenifier;
    private String auth_nas_ip_address;

    @Id
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

    @Formula("(select distinct md.from_var from measurements_dhcp as md where md.type_var like 'DHCPREQUEST' and md.date_of_entry < measurement_date and md.for_var like '%' || client_ip || '%' limit 1)")
    public String getClientMac() {
        return clientMac;
    }

    public void setClientMac(String clientMac) {
        this.clientMac = clientMac;
    }

    @Formula("(select distinct md.date_of_entry from measurements_dhcp as md where md.type_var like 'DHCPREQUEST' and md.for_var like '%' || client_ip || '%' and md.date_of_entry < measurement_date limit 1)")
    public String getDhcpEntry() {
        return dhcpEntry;
    }

    public void setDhcpEntry(String dhcpEntry) {
        this.dhcpEntry = dhcpEntry;
    }

    @Column(name = "mac")
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "calling_station_id", referencedColumnName = "mac")
    //@Formula("(select * from measurements_auth_detail where calling_station_id like mac)")
    public Set<MeasurementAuthDetails> getMeasurementAuthDetails() {
        return measurementAuthDetails;
    }

    public void setMeasurementAuthDetails(Set<MeasurementAuthDetails> measurementAuthDetails) {
        this.measurementAuthDetails = measurementAuthDetails;
    }

    @Formula("(select distinct mad.user_name from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuth_user_name() {
        return auth_user_name;
    }

    public void setAuth_user_name(String auth_user_name) {
        this.auth_user_name = auth_user_name;
    }

    @Formula("(select distinct mad.date_of_entry from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuthEntry() {
        return authEntry;
    }

    public void setAuthEntry(String authEntry) {
        this.authEntry = authEntry;
    }

    @Formula("(select distinct mad.packet_type from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuth_packet_type() {
        return auth_packet_type;
    }

    public void setAuth_packet_type(String auth_packet_type) {
        this.auth_packet_type = auth_packet_type;
    }

    @Formula("(select distinct mad.called_station_id from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuth_called_station_id() {
        return auth_called_station_id;
    }

    public void setAuth_called_station_id(String auth_called_station_id) {
        this.auth_called_station_id = auth_called_station_id;
    }


    @Formula("(select distinct mad.connect_info from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuth_connect_info() {
        return auth_connect_info;
    }

    public void setAuth_connect_info(String auth_connect_info) {
        this.auth_connect_info = auth_connect_info;
    }

    @Formula("(select distinct mad.nas_identifier from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuth_nas_idenifier() {
        return auth_nas_idenifier;
    }

    public void setAuth_nas_idenifier(String auth_nas_idenifier) {
        this.auth_nas_idenifier = auth_nas_idenifier;
    }

    @Formula("(select distinct mad.nas_ip_address from measurements_auth_detail as mad where mad.calling_station_id like mac and mad.date_of_entry < measurement_date limit 1)")
    public String getAuth_nas_ip_address() {
        return auth_nas_ip_address;
    }

    public void setAuth_nas_ip_address(String auth_nas_ip_address) {
        this.auth_nas_ip_address = auth_nas_ip_address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GenericMeasurement that = (GenericMeasurement) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
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
        if (clientMac != null ? !clientMac.equals(that.clientMac) : that.clientMac != null)
            return false;
        return !(mac != null ? !mac.equals(that.mac) : that.mac != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (downloadRate != null ? downloadRate.hashCode() : 0);
        result = 31 * result + (uploadRate != null ? uploadRate.hashCode() : 0);
        result = 31 * result + (localPing != null ? localPing.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (locationMethod != null ? locationMethod.hashCode() : 0);
        result = 31 * result + (clientIp != null ? clientIp.hashCode() : 0);
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (clientMac != null ? clientMac.hashCode() : 0);
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        return result;
    }
}


