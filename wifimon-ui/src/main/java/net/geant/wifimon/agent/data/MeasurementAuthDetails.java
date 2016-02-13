package net.geant.wifimon.agent.data;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by kanakisn on 8/10/15.
 */

@Entity
@Table(name = "measurements_auth_detail")
public class MeasurementAuthDetails implements Serializable {

    private Integer id;
    private Date date_of_entry;
    private String packet_type;
    private String user_name;
    private String acct_session_id;
    private String calling_station_id;
    private String called_station_id;
    private String symbol_current_essid;
    private String nas_port;
    private String nas_port_type;
    private String framed_mtu;
    private String service_type;
    private String nas_identifier;
    private String nas_port_id;
    private String symbol_attr18;
    private String symbol_attr17;
    private String symbol_attr19;
    private String symbol_attr23;
    private String connect_info;
    private String proxy_state;
    private String state;
    private String eap_message;
    private String nas_ip_address;
    private String message_authenticator;
    private String free_radius_proxied_to;
    private String airespace_wlan_id;
    private String tunnel_type0;
    private String tunnel_medium_type0;
    private String vendor9048_attr0;
    private String location_capable;
    private String tunnel_private_group_id0;
    private String chargeable_user_identity;
    private String cisco_avpair;
    private String operator_name;
    private String siemens_ap_serial;
    private String siemens_ap_name;
    private String siemens_vns_name;
    private String siemens_ssid;
    private String siemens_bss_mac;
    private String vendor1076_attr2;
    private String vendor1076_attr3;
    private String extreme_attr4;
    private String extreme_attr5;
    private String aruba_attr10;
    private String aruba_attr12;
    private String ascend_dialout_allowed;
    private String aruba_essid_name;
    private String aruba_location_id;
    private String framed_compression;
    private String extreme_attr32;

    @Id
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "measurement_date")
//    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public Date getDate_of_entry() {
        return date_of_entry;
    }

    public void setDate_of_entry(Date date_of_entry) {
        this.date_of_entry = date_of_entry;
    }

    @Column(name = "packet_type")
    public String getPacket_type() {
        return packet_type;
    }

    public void setPacket_type(String packet_type) {
        this.packet_type = packet_type;
    }

    @Column(name = "user_name")
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Column(name = "acct_session_id")
    public String getAcct_session_id() {
        return acct_session_id;
    }

    public void setAcct_session_id(String acct_session_id) {
        this.acct_session_id = acct_session_id;
    }

    @Column(name = "calling_station_id")
    public String getCalling_station_id() {
        return calling_station_id;
    }

    public void setCalling_station_id(String calling_station_id) {
        this.calling_station_id = calling_station_id;
    }

    @Column(name = "called_station_id")
    public String getCalled_station_id() {
        return called_station_id;
    }

    public void setCalled_station_id(String called_station_id) {
        this.called_station_id = called_station_id;
    }

    @Column(name = "symbol_current_essid")
    public String getSymbol_current_essid() {
        return symbol_current_essid;
    }

    public void setSymbol_current_essid(String symbol_current_essid) {
        this.symbol_current_essid = symbol_current_essid;
    }

    @Column(name = "nas_port")
    public String getNas_port() {
        return nas_port;
    }

    public void setNas_port(String nas_port) {
        this.nas_port = nas_port;
    }

    @Column(name = "nas_port_type")
    public String getNas_port_type() {
        return nas_port_type;
    }

    public void setNas_port_type(String nas_port_type) {
        this.nas_port_type = nas_port_type;
    }

    @Column(name = "framed_mtu")
    public String getFramed_mtu() {
        return framed_mtu;
    }

    public void setFramed_mtu(String framed_mtu) {
        this.framed_mtu = framed_mtu;
    }

    @Column(name = "service_type")
    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    @Column(name = "nas_identifier")
    public String getNas_identifier() {
        return nas_identifier;
    }

    public void setNas_identifier(String nas_identifier) {
        this.nas_identifier = nas_identifier;
    }

    @Column(name = "nas_port_id")
    public String getNas_port_id() {
        return nas_port_id;
    }

    public void setNas_port_id(String nas_port_id) {
        this.nas_port_id = nas_port_id;
    }

    @Column(name = "symbol_attr18")
    public String getSymbol_attr18() {
        return symbol_attr18;
    }

    public void setSymbol_attr18(String symbol_attr18) {
        this.symbol_attr18 = symbol_attr18;
    }

    @Column(name = "symbol_attr17")
    public String getSymbol_attr17() {
        return symbol_attr17;
    }

    public void setSymbol_attr17(String symbol_attr17) {
        this.symbol_attr17 = symbol_attr17;
    }

    @Column(name = "symbol_attr19")
    public String getSymbol_attr19() {
        return symbol_attr19;
    }

    public void setSymbol_attr19(String symbol_attr19) {
        this.symbol_attr19 = symbol_attr19;
    }

    @Column(name = "symbol_attr23")
    public String getSymbol_attr23() {
        return symbol_attr23;
    }

    public void setSymbol_attr23(String symbol_attr23) {
        this.symbol_attr23 = symbol_attr23;
    }

    @Column(name = "connect_info")
    public String getConnect_info() {
        return connect_info;
    }

    public void setConnect_info(String connect_info) {
        this.connect_info = connect_info;
    }

    @Column(name = "proxy_state")
    public String getProxy_state() {
        return proxy_state;
    }

    public void setProxy_state(String proxy_state) {
        this.proxy_state = proxy_state;
    }

    @Column(name = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "eap_message")
    public String getEap_message() {
        return eap_message;
    }

    public void setEap_message(String eap_message) {
        this.eap_message = eap_message;
    }

    @Column(name = "nas_ip_address")
    public String getNas_ip_address() {
        return nas_ip_address;
    }

    public void setNas_ip_address(String nas_ip_address) {
        this.nas_ip_address = nas_ip_address;
    }

    @Column(name = "message_authenticator")
    public String getMessage_authenticator() {
        return message_authenticator;
    }

    public void setMessage_authenticator(String message_authenticator) {
        this.message_authenticator = message_authenticator;
    }

    @Column(name = "free_radius_proxied_to")
    public String getFree_radius_proxied_to() {
        return free_radius_proxied_to;
    }

    public void setFree_radius_proxied_to(String free_radius_proxied_to) {
        this.free_radius_proxied_to = free_radius_proxied_to;
    }

    @Column(name = "airespace_wlan_id")
    public String getAirespace_wlan_id() {
        return airespace_wlan_id;
    }

    public void setAirespace_wlan_id(String airespace_wlan_id) {
        this.airespace_wlan_id = airespace_wlan_id;
    }

    @Column(name = "tunnel_type0")
    public String getTunnel_type0() {
        return tunnel_type0;
    }

    public void setTunnel_type0(String tunnel_type0) {
        this.tunnel_type0 = tunnel_type0;
    }

    @Column(name = "tunnel_medium_type0")
    public String getTunnel_medium_type0() {
        return tunnel_medium_type0;
    }

    public void setTunnel_medium_type0(String tunnel_medium_type0) {
        this.tunnel_medium_type0 = tunnel_medium_type0;
    }

    @Column(name = "vendor9048_attr0")
    public String getVendor9048_attr0() {
        return vendor9048_attr0;
    }

    public void setVendor9048_attr0(String vendor9048_attr0) {
        this.vendor9048_attr0 = vendor9048_attr0;
    }

    @Column(name = "location_capable")
    public String getLocation_capable() {
        return location_capable;
    }

    public void setLocation_capable(String location_capable) {
        this.location_capable = location_capable;
    }

    @Column(name = "tunnel_private_group_id0")
    public String getTunnel_private_group_id0() {
        return tunnel_private_group_id0;
    }

    public void setTunnel_private_group_id0(String tunnel_private_group_id0) {
        this.tunnel_private_group_id0 = tunnel_private_group_id0;
    }

    @Column(name = "chargeable_user_identity")
    public String getChargeable_user_identity() {
        return chargeable_user_identity;
    }

    public void setChargeable_user_identity(String chargeable_user_identity) {
        this.chargeable_user_identity = chargeable_user_identity;
    }

    @Column(name = "cisco_avpair")
    public String getCisco_avpair() {
        return cisco_avpair;
    }

    public void setCisco_avpair(String cisco_avpair) {
        this.cisco_avpair = cisco_avpair;
    }

    @Column(name = "operator_name")
    public String getOperator_name() {
        return operator_name;
    }

    public void setOperator_name(String operator_name) {
        this.operator_name = operator_name;
    }

    @Column(name = "siemens_ap_serial")
    public String getSiemens_ap_serial() {
        return siemens_ap_serial;
    }

    public void setSiemens_ap_serial(String siemens_ap_serial) {
        this.siemens_ap_serial = siemens_ap_serial;
    }

    @Column(name = "siemens_ap_name")
    public String getSiemens_ap_name() {
        return siemens_ap_name;
    }

    public void setSiemens_ap_name(String siemens_ap_name) {
        this.siemens_ap_name = siemens_ap_name;
    }

    @Column(name = "siemens_vns_name")
    public String getSiemens_vns_name() {
        return siemens_vns_name;
    }

    public void setSiemens_vns_name(String siemens_vns_name) {
        this.siemens_vns_name = siemens_vns_name;
    }

    @Column(name = "siemens_ssid")
    public String getSiemens_ssid() {
        return siemens_ssid;
    }

    public void setSiemens_ssid(String siemens_ssid) {
        this.siemens_ssid = siemens_ssid;
    }

    @Column(name = "siemens_bss_mac")
    public String getSiemens_bss_mac() {
        return siemens_bss_mac;
    }

    public void setSiemens_bss_mac(String siemens_bss_mac) {
        this.siemens_bss_mac = siemens_bss_mac;
    }

    @Column(name = "vendor1076_attr2")
    public String getVendor1076_attr2() {
        return vendor1076_attr2;
    }

    public void setVendor1076_attr2(String vendor1076_attr2) {
        this.vendor1076_attr2 = vendor1076_attr2;
    }

    @Column(name = "vendor1076_attr3")
    public String getVendor1076_attr3() {
        return vendor1076_attr3;
    }

    public void setVendor1076_attr3(String vendor1076_attr3) {
        this.vendor1076_attr3 = vendor1076_attr3;
    }

    @Column(name = "extreme_attr4")
    public String getExtreme_attr4() {
        return extreme_attr4;
    }

    public void setExtreme_attr4(String extreme_attr4) {
        this.extreme_attr4 = extreme_attr4;
    }

    @Column(name = "extreme_attr5")
    public String getExtreme_attr5() {
        return extreme_attr5;
    }

    public void setExtreme_attr5(String extreme_attr5) {
        this.extreme_attr5 = extreme_attr5;
    }

    @Column(name = "aruba_attr10")
    public String getAruba_attr10() {
        return aruba_attr10;
    }

    public void setAruba_attr10(String aruba_attr10) {
        this.aruba_attr10 = aruba_attr10;
    }

    @Column(name = "aruba_attr12")
    public String getAruba_attr12() {
        return aruba_attr12;
    }

    public void setAruba_attr12(String aruba_attr12) {
        this.aruba_attr12 = aruba_attr12;
    }

    @Column(name = "ascend_dialout_allowed")
    public String getAscend_dialout_allowed() {
        return ascend_dialout_allowed;
    }

    public void setAscend_dialout_allowed(String ascend_dialout_allowed) {
        this.ascend_dialout_allowed = ascend_dialout_allowed;
    }

    @Column(name = "aruba_essid_name")
    public String getAruba_essid_name() {
        return aruba_essid_name;
    }

    public void setAruba_essid_name(String aruba_essid_name) {
        this.aruba_essid_name = aruba_essid_name;
    }

    @Column(name = "aruba_location_id")
    public String getAruba_location_id() {
        return aruba_location_id;
    }

    public void setAruba_location_id(String aruba_location_id) {
        this.aruba_location_id = aruba_location_id;
    }

    @Column(name = "framed_compression")
    public String getFramed_compression() {
        return framed_compression;
    }

    public void setFramed_compression(String framed_compression) {
        this.framed_compression = framed_compression;
    }

    @Column(name = "extreme_attr32")
    public String getExtreme_attr32() {
        return extreme_attr32;
    }

    public void setExtreme_attr32(String extreme_attr32) {
        this.extreme_attr32 = extreme_attr32;
    }

}
