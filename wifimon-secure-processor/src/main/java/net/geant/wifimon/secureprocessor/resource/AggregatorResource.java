package net.geant.wifimon.secureprocessor.resource;

import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.floragunn.searchguard.ssl.util.SSLConfigConstants;
import net.geant.wifimon.model.dto.AggregatedMeasurement;
import net.geant.wifimon.model.dto.NetTestMeasurement;
import net.geant.wifimon.model.entity.Accesspoint;
import net.geant.wifimon.model.entity.CorrelationMethod;
import net.geant.wifimon.model.entity.RadiusStripped;
import net.geant.wifimon.model.entity.Subnet;
import net.geant.wifimon.secureprocessor.repository.AccesspointsRepository;
import net.geant.wifimon.secureprocessor.repository.SubnetRepository;
import net.geant.wifimon.secureprocessor.repository.VisualOptionsRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.util.SubnetUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kokkinos on 12/02/16.
 */
@Component
@Path("/wifimon")
public class AggregatorResource {

    @Autowired
    SubnetRepository subnetRepository;

    @Autowired
    AccesspointsRepository accesspointsRepository;

    @Autowired
    VisualOptionsRepository visualOptionsRepository;

    @Autowired
    Environment environment;

    private static final String ES_CLUSTERNAME = "elasticsearch.clustername";
    private static final String ES_HOST = "elasticsearch.host";
    private static final String ES_PORT = "elasticsearch.port";
    private static final String ES_INDEXNAME_MEASUREMENT = "elasticsearch.indexnamemeasurement";
    private static final String ES_TYPE_MEASUREMENT = "elasticsearch.typenamemeasurement";
    private static final String ES_INDEXNAME_RADIUS = "elasticsearch.indexnameradius";
    private static final String ES_TYPE_RADIUS = "elasticsearch.typenameradius";
    private static final String ES_INDEXNAME_DHCP = "elasticsearch.indexnamedhcp";
    private static final String ES_TYPE_DHCP = "elasticsearch.typenamedhcp";
    private static final String SG_SSL_ENABLED = "sg.ssl.enabled";
    private static final String SG_SSL_KEYSTORE_FILEPATH = "sg.ssl.transport.keystore.filepath";
    private static final String SG_SSL_KEYSTORE_PASSWORD= "sg.ssl.transport.keystore.password";
    private static final String SG_SSL_TRUSTSTORE_FILEPATH= "sg.ssl.transport.truststore.filepath";
    private static final String SG_SSL_TRUSTSTORE_PASSWORD= "sg.ssl.transport.truststore.password";
    private static final String SG_SSL_USER_USERNAME= "sg.ssl.transport.user.username";
    private static final String SG_SSL_USER_PASSWORD= "sg.ssl.transport.user.password";


    @POST
    @Path("/subnet")
    public Response correlate(@Context HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        List<Subnet> subnets = subnetRepository.findAll();
        if (subnets == null || subnets.isEmpty()) return Response.ok(false).build();

        List<SubnetUtils.SubnetInfo> s = subnets.stream().
                map(it -> it.fromSubnetString()).collect(Collectors.toList());

        for (SubnetUtils.SubnetInfo si : s) {
            if (si.isInRange(ip)) return Response.ok(true).build();
        }
        return Response.ok(false).build();
    }

    @POST
    @Path("/add")
    public Response correlate(final NetTestMeasurement measurement, @Context HttpServletRequest request) {
        String agent = request.getHeader("User-Agent") != null || !request.getHeader("User-Agent").isEmpty() ?  request.getHeader("User-Agent") : "N/A";
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) return Response.serverError().build();

        String correlationmethod = visualOptionsRepository.findCorrelationmethod();
        if (correlationmethod == null || correlationmethod.isEmpty()) {
            correlationmethod = "Radius_only";
        }

        RadiusStripped r = new RadiusStripped();
        Accesspoint ap = new Accesspoint();

        if (correlationmethod.equals(CorrelationMethod.DHCP_and_Radius.toString())){
            //TODO Complete the else for correlation with DHCP and Radius
            String callingStationIdTemp = "A1:b2-cc-33-d0-da".substring(0,17);
            r = retrieveLastRadiusEntryByMac(callingStationIdTemp);
        }else{
            r = retrieveLastRadiusEntryByIp(ip);
        }

        if (r != null) {
            String calledStationIdTemp = r.getCalledStationId().substring(0,17).toUpperCase().replace("-",":");
            ap = accesspointsRepository.find(calledStationIdTemp);
            return addElasticMeasurement(joinMeasurement(measurement, r, ap, ip, agent));
            }
        else {
            return addElasticMeasurement(joinMeasurement(measurement, r, null, ip, agent));
        }
    }

    private AggregatedMeasurement joinMeasurement(NetTestMeasurement measurement, RadiusStripped radius, Accesspoint accesspoint, String ip, String agent) {
        AggregatedMeasurement m = new AggregatedMeasurement();
        m.setTimestamp(System.currentTimeMillis());
        m.setDownloadThroughput(measurement.getDownloadThroughput());
        m.setUploadThroughput(measurement.getUploadThroughput());
        m.setLocalPing(measurement.getLocalPing());
        m.setLatitude(measurement.getLatitude() != null ? Double.valueOf(measurement.getLatitude()) : null);
        m.setLongitude(measurement.getLongitude() != null ? Double.valueOf(measurement.getLongitude()): null);
        m.setLocationMethod(measurement.getLocationMethod());
        m.setClientIp(ip);
        m.setUserAgent(agent);
        m.setTestTool(measurement.getTestTool());
        m.setUserName(radius != null ? radius.getUserName() : null);
        m.setNasPort(radius != null ? radius.getNasPort() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId() : null);
        m.setNasIdentifier(radius != null ? radius.getNasIdentifier() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId() : null);
        m.setNasIpAddress(radius != null  ? radius.getNasIpAddress() : null);
        m.setApBuilding(accesspoint != null ? accesspoint.getBuilding() : null);
        m.setApFloor(accesspoint != null ? accesspoint.getFloor() : null);
        m.setApLatitude(accesspoint != null ? Double.valueOf(accesspoint.getLatitude()) : null);
        m.setApLongitude(accesspoint != null ? Double.valueOf(accesspoint.getLongitude()) : null);
        m.setApNotes(accesspoint != null ? accesspoint.getNotes() : null);
        return m;
    }

    private Response addElasticMeasurement(AggregatedMeasurement measurement) {
        // addElasticMeasurement for Kibana 4.1.2
        String UserAgent = measurement.getUserAgent();
        String userOS = new String();
        if (UserAgent.toUpperCase().contains("WINDOWS")){
            userOS = "Windows";
        }else if (UserAgent.toUpperCase().contains("MAC")){
            userOS = "Mac OS X and iOS";
        }else if (UserAgent.toUpperCase().contains("X11")){
            userOS = "Linux";
        }else if (UserAgent.toUpperCase().contains("ANDROID")){
            userOS = "Android";
        }else{
            userOS = "N/A";
        }

        String userBrowser = new String();
        if (UserAgent.toUpperCase().contains("CHROME") && !UserAgent.toUpperCase().contains("EDGE")){
            userBrowser = "Chrome";
        }else if (UserAgent.toUpperCase().contains("SAFARI") && !UserAgent.toUpperCase().contains("CHROME")){
            userBrowser = "Safari";
        }else if (UserAgent.toUpperCase().contains("FIREFOX")){
            userBrowser = "Firefox";
        }else if (UserAgent.toUpperCase().contains("MSIE")){
            userBrowser = "Internet Explorer";
        }else if (UserAgent.toUpperCase().contains("EDGE")){
            userBrowser = "Microsoft Edge";
        }else{
            userBrowser = "N/A";
        }

        String downloadThroughputJson = measurement.getDownloadThroughput() != null ? "\"downloadThroughput\" : " + measurement.getDownloadThroughput() + ", " : "";
        String uploadThroughputJson = measurement.getUploadThroughput() != null ? "\"uploadThroughput\" : " + measurement.getUploadThroughput() + ", " : "";
        String localPingJson = measurement.getLocalPing() != null ? "\"localPing\" : " + measurement.getLocalPing() + ", " : "";
        String locationJson = measurement.getLatitude() != null ? "\"location\" : \"" + measurement.getLatitude() + "," + measurement.getLongitude() + "\", " : "";
        String locationMethodJson = measurement.getLocationMethod() != null ? "\"locationMethod\" : \"" + measurement.getLocationMethod() + "\", " : "";
        String clientIpJson = measurement.getClientIp() != null ? "\"clientIp\" : \"" + measurement.getClientIp() + "\", " : "";
        String userAgentJson =measurement.getUserAgent() != null ? "\"userAgent\" : \"" + measurement.getUserAgent() + "\", " : "";
        String userBrowserJson = userBrowser != null ? "\"userBrowser\" : \"" + userBrowser + "\", " : "";
        String userOSJson = userOS != null ? "\"userOS\" : \"" + userOS + "\", " : "";
        String testToolJson = measurement.getTestTool() != null ? "\"testTool\" : \"" + measurement.getTestTool() + "\", " : "";
        String usernameJson = measurement.getUserName() != null ? "\"username\" : \"" + measurement.getUserName() + "\", " : "";
        String nasPortJson = measurement.getNasPort() != null ? "\"nasPort\" : \"" + measurement.getNasPort() + "\", " : "";
        String callingStationIdJson = measurement.getCallingStationId() != null ? "\"callingStationId\" : \"" + measurement.getCallingStationId() + "\", " : "";
        String nasIdentifierJson = measurement.getNasIdentifier() != null ? "\"nasIdentifier\" : \"" + measurement.getNasIdentifier() + "\", " : "";
        String calledStationIdJson = measurement.getCalledStationId() != null ? "\"calledStationId\" : \"" + measurement.getCalledStationId() + "\", " : "";
        String nasIpAddressJson = measurement.getNasIpAddress() != null ? "\"nasIpAddress\" : \"" + measurement.getNasIpAddress() + "\", " : "";
        String apBuildingJson = measurement.getApBuilding() != null ? "\"apBuilding\" : \"" + measurement.getApBuilding() + "\", " : "";
        String apFloorJson = measurement.getApFloor() != null ? "\"apFloor\" : \"" + measurement.getApFloor() + "\", " : "";
        String apLocationJson = measurement.getApLatitude() != null ? "\"apLocation\" : \"" + measurement.getApLatitude() + "," + measurement.getApLongitude() + "\", " : "";
        String apNotesJson = measurement.getApNotes() != null ? "\"apNotes\" : \"" + measurement.getApNotes() + "\"" : "";

        String jsonStringDraft = "{" +
                "\"timestamp\" : " + measurement.getTimestamp() + ", " +
                downloadThroughputJson + uploadThroughputJson + localPingJson +
                locationJson + locationMethodJson + clientIpJson +
                userAgentJson + userBrowserJson + userOSJson +
                testToolJson + usernameJson + nasPortJson +
                callingStationIdJson + nasIdentifierJson + calledStationIdJson +
                nasIpAddressJson + apBuildingJson + apFloorJson + apLocationJson +
                apNotesJson + "}";

        String jsonString = jsonStringDraft.replace("\", }", "\"}");
        /*String jsonString = "{" +
                "\"timestamp\" : " + measurement.getTimestamp() + "," +
                "\"downloadThroughput\" : " + measurement.getDownloadThroughput() + "," +
                "\"uploadThroughput\" : " + measurement.getUploadThroughput() + "," +
                "\"localPing\" : " + measurement.getLocalPing() + "," +
                "\"location\" : \"" + measurement.getLatitude() + "," + measurement.getLongitude() + "\"," +
                "\"locationMethod\" : \"" + measurement.getLocationMethod() + "\"," +
                "\"clientIp\" : \"" + measurement.getClientIp() + "\"," +
                "\"userAgent\" : \"" + measurement.getUserAgent() + "\"," +
                "\"userBrowser\" : \"" + userBrowser + "\"," +
                "\"userOS\" : \"" + userOS + "\"," +
                "\"testTool\" : \"" + measurement.getTestTool() + "\"," +
                "\"username\" : \"" + measurement.getUserName() + "\"," +
                "\"nasPort\" : \"" + measurement.getNasPort() + "\"," +
                "\"callingStationId\" : \"" + measurement.getCallingStationId() + "\"," +
                "\"nasIdentifier\" : \"" + measurement.getNasIdentifier() + "\"," +
                "\"calledStationId\" : \"" + measurement.getCalledStationId() + "\"," +
                "\"nasIpAddress\" : \"" + measurement.getNasIpAddress() + "\"," +
                "\"apBuilding\" : \"" + measurement.getApBuilding() + "\"," +
                "\"apFloor\" : \"" + measurement.getApFloor() + "\"," +
                "\"apLocation\" : \"" + measurement.getApLatitude() + "," + measurement.getApLongitude() + "\"," +
                "\"apNotes\" : \"" + measurement.getApNotes() + "\"" +
                "}";*/

        TransportClient client = null;

        if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            Settings.Builder settingsBuilder =
                    Settings.builder()
                            .put("cluster.name", environment.getProperty(ES_CLUSTERNAME))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_FILEPATH, environment.getProperty(SG_SSL_KEYSTORE_FILEPATH))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_FILEPATH, environment.getProperty(SG_SSL_TRUSTSTORE_FILEPATH))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_PASSWORD, environment.getProperty(SG_SSL_KEYSTORE_PASSWORD))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_PASSWORD, environment.getProperty(SG_SSL_TRUSTSTORE_PASSWORD));
            Settings settings = settingsBuilder.build();
            try {
                client = new PreBuiltTransportClient(settings, SearchGuardSSLPlugin.class)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            client.threadPool().getThreadContext().putHeader("Authorization", "Basic "+ Base64.encodeBase64((environment.getProperty(SG_SSL_USER_USERNAME) + ":" + environment.getProperty(SG_SSL_USER_PASSWORD)).getBytes()));
        }else {
            Settings.Builder settingsBuilder =
                    Settings.builder()
                            .put("cluster.name", environment.getProperty(ES_CLUSTERNAME));
            Settings settings = settingsBuilder.build();
            try {
                client = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        IndexResponse indexResponse = client.prepareIndex(environment.getProperty(ES_INDEXNAME_MEASUREMENT), environment.getProperty(ES_TYPE_MEASUREMENT))
                .setSource(jsonString, XContentType.JSON)
                .get();

        client.close();

        return Response.ok().build();
    }

    private RadiusStripped retrieveLastRadiusEntryByIp(String ip) {

        RadiusStripped r = new RadiusStripped();

        TransportClient client = null;

        if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            Settings.Builder settingsBuilder =
                    Settings.builder()
                            .put("cluster.name", environment.getProperty(ES_CLUSTERNAME))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_FILEPATH, environment.getProperty(SG_SSL_KEYSTORE_FILEPATH))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_FILEPATH, environment.getProperty(SG_SSL_TRUSTSTORE_FILEPATH))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_PASSWORD, environment.getProperty(SG_SSL_KEYSTORE_PASSWORD))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_PASSWORD, environment.getProperty(SG_SSL_TRUSTSTORE_PASSWORD));
            Settings settings = settingsBuilder.build();
            try {
                client = new PreBuiltTransportClient(settings, SearchGuardSSLPlugin.class)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            client.threadPool().getThreadContext().putHeader("Authorization", "Basic "+ Base64.encodeBase64((environment.getProperty(SG_SSL_USER_USERNAME) + ":" + environment.getProperty(SG_SSL_USER_PASSWORD)).getBytes()));
        }else {
            Settings.Builder settingsBuilder =
                    Settings.builder()
                            .put("cluster.name", environment.getProperty(ES_CLUSTERNAME));
            Settings settings = settingsBuilder.build();
            try {
                client = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        SearchResponse response = client.prepareSearch(environment.getProperty(ES_INDEXNAME_RADIUS))
                .setTypes(environment.getProperty(ES_TYPE_RADIUS))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addSort("timestamp", SortOrder.DESC)
                .setFrom(0)
                .setFetchSource(new String[]{"username", "timestamp",
                        "nas_port", "source_host", "calling_station_id", "result",
                        "trace_id", "nas_identifier", "called_station_id", "nas_ip_address",
                        "framed_ip_address", "acct_status_type"}, null)
                .setPostFilter(QueryBuilders.termQuery("framed_ip_address", ip))
                .setQuery(QueryBuilders.termQuery("acct_status_type", "Start"))
                .setSize(1).setExplain(true)
                .get();

        if (response.getHits().getTotalHits() > 0) {
            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSource();
                r.setUserName(((map.get("username") != null) ? map.get("username").toString() : "N/A"));
                r.setTimestamp(((map.get("timestamp") != null) ? map.get("timestamp").toString() : "N/A"));
                r.setNasPort(((map.get("nas_port") != null) ? map.get("nas_port").toString() : "N/A"));
                r.setSourceHost(((map.get("source_host") != null) ? map.get("source_host").toString() : "N/A"));
                r.setCallingStationId(((map.get("calling_station_id") != null) ? map.get("calling_station_id").toString() : "N/A"));
                r.setResult(((map.get("result") != null) ? map.get("result").toString() : "N/A"));
                r.setTraceId(((map.get("trace_id") != null) ? map.get("trace_id").toString() : "N/A"));
                r.setNasIdentifier(((map.get("nas_identifier") != null) ? map.get("nas_identifier").toString() : "N/A"));
                r.setCalledStationId(((map.get("called_station_id") != null) ? map.get("called_station_id").toString() : "N/A"));
                r.setNasIpAddress(((map.get("nas_ip_address") != null) ? map.get("nas_ip_address").toString() : "N/A"));
                r.setFramedIpAddress(((map.get("framed_ip_address") != null) ? map.get("framed_ip_address").toString() : "N/A"));
                r.setAcctStatusType(((map.get("acct_status_type") != null) ? map.get("acct_status_type").toString() : "N/A"));
                break;
            }
        }else {
            r = null;
        }

        client.close();
        return r;
    }

    private RadiusStripped retrieveLastRadiusEntryByMac(String mac) {

        RadiusStripped r = new RadiusStripped();

        TransportClient client = null;

        if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            Settings.Builder settingsBuilder =
                    Settings.builder()
                            .put("cluster.name", environment.getProperty(ES_CLUSTERNAME))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_FILEPATH, environment.getProperty(SG_SSL_KEYSTORE_FILEPATH))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_FILEPATH, environment.getProperty(SG_SSL_TRUSTSTORE_FILEPATH))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_PASSWORD, environment.getProperty(SG_SSL_KEYSTORE_PASSWORD))
                            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_PASSWORD, environment.getProperty(SG_SSL_TRUSTSTORE_PASSWORD));
            Settings settings = settingsBuilder.build();
            try {
                client = new PreBuiltTransportClient(settings, SearchGuardSSLPlugin.class)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            client.threadPool().getThreadContext().putHeader("Authorization", "Basic "+ Base64.encodeBase64((environment.getProperty(SG_SSL_USER_USERNAME) + ":" + environment.getProperty(SG_SSL_USER_PASSWORD)).getBytes()));
        }else {
            Settings.Builder settingsBuilder =
                    Settings.builder()
                            .put("cluster.name", environment.getProperty(ES_CLUSTERNAME));
            Settings settings = settingsBuilder.build();
            try {
                client = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        SearchResponse response = client.prepareSearch(environment.getProperty(ES_INDEXNAME_RADIUS))
                .setTypes(environment.getProperty(ES_TYPE_RADIUS))
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .addSort("timestamp", SortOrder.DESC)
                .setFrom(0)
                .setFetchSource(new String[]{"username", "timestamp",
                        "nas_port", "source_host", "calling_station_id", "result",
                        "trace_id", "nas_identifier", "called_station_id", "nas_ip_address",
                        "framed_ip_address", "acct_status_type"}, null)
                .setPostFilter(QueryBuilders.wildcardQuery("calling_station_id", "*" + mac.replace(":","-").toLowerCase() + "*"))
                .setSize(1).setExplain(true)
                .get();

        if (response.getHits().getTotalHits() > 0) {
            for (SearchHit hit : response.getHits()) {
                Map map = hit.getSource();
                r.setUserName(((map.get("username") != null) ? map.get("username").toString() : "N/A"));
                r.setTimestamp(((map.get("timestamp") != null) ? map.get("timestamp").toString() : "N/A"));
                r.setNasPort(((map.get("nas_port") != null) ? map.get("nas_port").toString() : "N/A"));
                r.setSourceHost(((map.get("source_host") != null) ? map.get("source_host").toString() : "N/A"));
                r.setCallingStationId(((map.get("calling_station_id") != null) ? map.get("calling_station_id").toString() : "N/A"));
                r.setResult(((map.get("result") != null) ? map.get("result").toString() : "N/A"));
                r.setTraceId(((map.get("trace_id") != null) ? map.get("trace_id").toString() : "N/A"));
                r.setNasIdentifier(((map.get("nas_identifier") != null) ? map.get("nas_identifier").toString() : "N/A"));
                r.setCalledStationId(((map.get("called_station_id") != null) ? map.get("called_station_id").toString() : "N/A"));
                r.setNasIpAddress(((map.get("nas_ip_address") != null) ? map.get("nas_ip_address").toString() : "N/A"));
                r.setFramedIpAddress(((map.get("framed_ip_address") != null) ? map.get("framed_ip_address").toString() : "N/A"));
                r.setAcctStatusType(((map.get("acct_status_type") != null) ? map.get("acct_status_type").toString() : "N/A"));
                break;
            }
        }else {
            r = null;
        }

        client.close();
        return r;
    }

    private SubnetUtils.SubnetInfo fromSubnetString(String subnet) {
        return new SubnetUtils(subnet).getInfo();
    }

}