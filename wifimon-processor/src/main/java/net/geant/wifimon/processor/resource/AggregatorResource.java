package net.geant.wifimon.processor.resource;

import net.geant.wifimon.model.dto.NetTestMeasurement;
import net.geant.wifimon.model.entity.Accesspoint;
import net.geant.wifimon.model.entity.GenericMeasurement;
import net.geant.wifimon.model.entity.Radius;
import net.geant.wifimon.model.entity.RadiusStripped;
import net.geant.wifimon.model.entity.Subnet;
import net.geant.wifimon.processor.repository.AccesspointsRepository;
import net.geant.wifimon.processor.repository.GenericMeasurementRepository;
import net.geant.wifimon.processor.repository.RadiusRepository;
import net.geant.wifimon.processor.repository.SubnetRepository;
import net.geant.wifimon.processor.repository.VisualOptionsRepository;
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
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kanakisn on 12/02/16.
 */
@Component
@Path("/wifimon")
public class AggregatorResource {

    @Autowired
    private InfluxDB influxDB;

    @Autowired
    GenericMeasurementRepository measurementRepository;

    @Autowired
    RadiusRepository radiusRepository;

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


    @POST
    @Path("/add")
    public Response correlate(final NetTestMeasurement measurement, @Context HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) return Response.serverError().build();
        Radius r = radiusRepository.find(ip, new Date());
        String grafanasupport= visualOptionsRepository.findGrafanasupport();
        if (grafanasupport == null || grafanasupport.isEmpty()) {
            grafanasupport = "False";
        }
        String elasticsearchsupport= visualOptionsRepository.findElasticsearchsupport();
        if (elasticsearchsupport == null || elasticsearchsupport.isEmpty()) {
            elasticsearchsupport = "False";
        }
        Integer radiuslife = visualOptionsRepository.findRadiuslife();
        if (radiuslife != null){
            if (radiuslife == 1) {
                Integer integer = radiusRepository.deleteOldRecords1();
            } else if (radiuslife == 2){
                Integer integer = radiusRepository.deleteOldRecords2();
            } else if (radiuslife == 3){
                Integer integer = radiusRepository.deleteOldRecords3();
            } else if (radiuslife == 4){
                Integer integer = radiusRepository.deleteOldRecords4();
            } else if (radiuslife == 5){
                Integer integer = radiusRepository.deleteOldRecords5();
            } else if (radiuslife == 6){
                Integer integer = radiusRepository.deleteOldRecords6();
            } else if (radiuslife == 7){
                Integer integer = radiusRepository.deleteOldRecords7();
            } else if (radiuslife == 8){
                Integer integer = radiusRepository.deleteOldRecords8();
            } else if (radiuslife == 9){
                Integer integer = radiusRepository.deleteOldRecords9();
            } else if (radiuslife == 10){
                Integer integer = radiusRepository.deleteOldRecords10();
            } else if (radiuslife == 11){
                Integer integer = radiusRepository.deleteOldRecords11();
            } else if (radiuslife == 12){
                Integer integer = radiusRepository.deleteOldRecords12();
            } else if (radiuslife == 13){
                Integer integer = radiusRepository.deleteOldRecords13();
            } else if (radiuslife == 14){
                Integer integer = radiusRepository.deleteOldRecords14();
            } else if (radiuslife == 15){
                Integer integer = radiusRepository.deleteOldRecords15();
            } else if (radiuslife == 16){
                Integer integer = radiusRepository.deleteOldRecords16();
            } else if (radiuslife == 17){
                Integer integer = radiusRepository.deleteOldRecords17();
            } else if (radiuslife == 18){
                Integer integer = radiusRepository.deleteOldRecords18();
            } else if (radiuslife == 19){
                Integer integer = radiusRepository.deleteOldRecords19();
            } else if (radiuslife == 20){
                Integer integer = radiusRepository.deleteOldRecords20();
            } else if (radiuslife == 21){
                Integer integer = radiusRepository.deleteOldRecords21();
            } else if (radiuslife == 22){
                Integer integer = radiusRepository.deleteOldRecords22();
            } else if (radiuslife == 23){
                Integer integer = radiusRepository.deleteOldRecords23();
            } else if (radiuslife == 24){
                Integer integer = radiusRepository.deleteOldRecords24();
            } else {
                //By default delete radius entries after 6 hours
                Integer integer = radiusRepository.deleteOldRecords6();
            }
        }else {
            //By default delete radius entries after 6 hours
            Integer integer = radiusRepository.deleteOldRecords6();
        }

        if (r != null) {
            Accesspoint ap = accesspointsRepository.find(r.getCalledStationId());
            if (grafanasupport.equals("True") && elasticsearchsupport.equals("False")) {
                return addGrafanaMeasurement(addMeasurement(measurement, r, ap, ip, agent));
            }else if (grafanasupport.equals("False") && elasticsearchsupport.equals("True")){
                return addElasticMeasurement(addMeasurement(measurement, r, ap, ip, agent));
            }else if (grafanasupport.equals("True") && elasticsearchsupport.equals("True")){
                GenericMeasurement genericMeasurement = addMeasurement(measurement, r, ap, ip, agent);
                addGrafanaMeasurement(genericMeasurement);
                return addElasticMeasurement(genericMeasurement);
            }else{
                addMeasurement(measurement, r, ap, ip, agent);
                return Response.ok(true).build();
            }
        }else {
            if (grafanasupport.equals("True") && elasticsearchsupport.equals("False")) {
                return addGrafanaMeasurement(addMeasurement(measurement, r, ip, agent));
            }else if (grafanasupport.equals("False") && elasticsearchsupport.equals("True")){
                return addElasticMeasurement(addMeasurement(measurement, r, ip, agent));
            }else if (grafanasupport.equals("True") && elasticsearchsupport.equals("True")){
                GenericMeasurement genericMeasurement = addMeasurement(measurement, r, ip, agent);
                addGrafanaMeasurement(genericMeasurement);
                return addElasticMeasurement(genericMeasurement);
            }else{
                GenericMeasurement genericMeasurement = addMeasurement(measurement, r, ip, agent);
                return Response.ok(true).build();
            }
        }
//        if (r == null) return Response.serverError().build();

    }

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

    private Response addGrafanaMeasurement(GenericMeasurement measurement) {
        try {
            Point point = Point.measurement("nettest")
                    .tag("ip", measurement.getClientIp() != null ? measurement.getClientIp() : "N/A")
                    .tag("UserAgent", measurement.getUserAgent() != null ? measurement.getUserAgent() : "N/A")
                    .tag("longitude",
                         measurement.getLongitude() != null ? String.valueOf(measurement.getLongitude()) : "N/A")
                    .tag("latitude",
                         measurement.getLatitude() != null ? String.valueOf(measurement.getLatitude()) : "N/A")
                    .tag("locationMethod",
                         measurement.getLocationMethod() != null ? measurement.getLocationMethod() : "N/A")
                    .tag("username", measurement.getUsername() != null ? measurement.getUsername() : "N/A")
                    .tag("callingStationId",
                         measurement.getCallingStationId() != null ? measurement.getCallingStationId() : "N/A")
                    .tag("calledStationId",
                         measurement.getCalledStationId() != null ? measurement.getCalledStationId() : "N/A")
                    .tag("nasPortType", measurement.getNasPortType() != null ? measurement.getNasPortType() : "N/A")
                    .tag("nasIpAddress", measurement.getNasIpAddress() != null ? measurement.getNasIpAddress() : "N/A")
                    .field("DownloadThroughtput",
                           measurement.getDownloadRate() == -1 ? -1d : measurement.getDownloadRate() * 8 * 1000)
                    .field("UploadThroughtput",
                           measurement.getUploadRate() == -1 ? -1d : measurement.getUploadRate() * 8 * 1000)
                    .field("ping", measurement.getLocalPing() == -1 ? -1d : measurement.getLocalPing())
                    .tag("testTool", measurement.getTestTool() != null ? measurement.getTestTool() : "N/A")
                    .build();
            influxDB.write("wifimon", "default", point);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private GenericMeasurement addMeasurement(NetTestMeasurement measurement, Radius radius, Accesspoint accesspoint, String ip, String agent) {
        GenericMeasurement m = new GenericMeasurement();
        m.setDate(new Date());
        m.setDownloadRate(measurement.getDownloadThroughput());
        m.setUploadRate(measurement.getUploadThroughput());
        m.setLocalPing(measurement.getLocalPing());
        m.setLatitude(Double.valueOf(measurement.getLatitude()));
        m.setLongitude(Double.valueOf(measurement.getLongitude()));
        m.setLocationMethod(measurement.getLocationMethod());
        m.setClientIp(ip);
        m.setUserAgent(agent);
        m.setStartTime(radius != null ? radius.getStartTime() : null);
        m.setUsername(radius != null ? radius.getUsername() : null);
        m.setFramedIpAddress(radius != null ? radius.getFramedIpAddress() : null);
        m.setSessionId(radius != null ? radius.getSessionId() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId().replace(":","-").toUpperCase() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId().replace(":","-").toUpperCase() : null);
        m.setNasPortId(radius != null ? radius.getNasPortId() : null);
        m.setNasPortType(radius != null ? radius.getNasPortType() : null);
        m.setNasIpAddress(radius != null ? radius.getNasIpAddress() : null);
        m.setTestTool(measurement.getTestTool());
        m.setApMac(accesspoint != null ? accesspoint.getMac().replace(":","-").toUpperCase() : null);
        m.setApLatitude(accesspoint != null ? accesspoint.getLatitude() : null);
        m.setApLongitude(accesspoint != null ? accesspoint.getLongitude() : null);
        m.setApBuilding(accesspoint != null ? accesspoint.getBuilding() : null);
        m.setApFloor(accesspoint != null ? accesspoint.getFloor() : null);
        m.setApNotes(accesspoint != null ? accesspoint.getNotes() : null);

        RadiusStripped radiusStrippedIp = retrieveLastRadiusEntryByIp(ip);
        RadiusStripped radiusStrippedMac = retrieveLastRadiusEntryByMac("A1:b2-cc-33-d0-da");

        return measurementRepository.save(m);
    }

    private GenericMeasurement addMeasurement(NetTestMeasurement measurement, Radius radius, String ip, String agent) {
        GenericMeasurement m = new GenericMeasurement();
        m.setDate(new Date());
        m.setDownloadRate(measurement.getDownloadThroughput());
        m.setUploadRate(measurement.getUploadThroughput());
        m.setLocalPing(measurement.getLocalPing());
        m.setLatitude(Double.valueOf(measurement.getLatitude()));
        m.setLongitude(Double.valueOf(measurement.getLongitude()));
        m.setLocationMethod(measurement.getLocationMethod());
        m.setClientIp(ip);
        m.setUserAgent(agent);
        m.setStartTime(radius != null ? radius.getStartTime() : null);
        m.setUsername(radius != null ? radius.getUsername() : null);
        m.setFramedIpAddress(radius != null ? radius.getFramedIpAddress() : null);
        m.setSessionId(radius != null ? radius.getSessionId() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId().replace(":","-").toUpperCase() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId().replace(":","-").toUpperCase() : null);
        m.setNasPortId(radius != null ? radius.getNasPortId() : null);
        m.setNasPortType(radius != null ? radius.getNasPortType() : null);
        m.setNasIpAddress(radius != null ? radius.getNasIpAddress() : null);
        m.setTestTool(measurement.getTestTool());

        RadiusStripped radiusStrippedIp = retrieveLastRadiusEntryByIp(ip);
        RadiusStripped radiusStrippedMac = retrieveLastRadiusEntryByMac("A1:b2-cc-33-d0-da");

        return measurementRepository.save(m);
    }

    private Response addElasticMeasurement(GenericMeasurement measurement) {
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

        Long timestamp = System.currentTimeMillis();

        String jsonString = "{" +
                "\"timestamp\" : " + timestamp + "," +
                "\"downloadThroughput\" : " + measurement.getDownloadRate() + "," +
                "\"uploadThroughput\" : " + measurement.getUploadRate() + "," +
                "\"localPing\" : " + measurement.getLocalPing() + "," +
                "\"location\" : \"" + measurement.getLatitude() + "," + measurement.getLongitude() + "\"," +
                "\"locationMethod\" : \"" + measurement.getLocationMethod() + "\"," +
                "\"clientIp\" : \"" + measurement.getClientIp() + "\"," +
                "\"userAgent\" : \"" + measurement.getUserAgent() + "\"," +
                "\"userBrowser\" : \"" + userBrowser + "\"," +
                "\"userOS\" : \"" + userOS + "\"," +
                "\"username\" : \"" + measurement.getUsername() + "\"," +
                "\"callingStationId\" : \"" + measurement.getCallingStationId() + "\"," +
                "\"calledStationId\" : \"" + measurement.getCalledStationId() + "\"," +
                "\"nasPortType\" : \"" + measurement.getNasPortType() + "\"," +
                "\"nasIpAddress\" : \"" + measurement.getNasIpAddress() + "\"," +
                "\"testTool\" : \"" + measurement.getTestTool() + "\"" +
                "}";

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
            r.setUserName("N/A");
            r.setTimestamp("N/A");
            r.setNasPort("N/A");
            r.setSourceHost("N/A");
            r.setCallingStationId("N/A");
            r.setResult("N/A");
            r.setTraceId("N/A");
            r.setNasIdentifier("N/A");
            r.setCalledStationId("N/A");
            r.setNasIpAddress("N/A");
            r.setFramedIpAddress("N/A");
            r.setAcctStatusType("N/A");
        }


        client.close();
        return r;
    }

    private RadiusStripped retrieveLastRadiusEntryByMac(String mac) {

        RadiusStripped r = new RadiusStripped();

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty(ES_HOST)), Integer.parseInt(environment.getProperty(ES_PORT))));
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
            r.setUserName("N/A");
            r.setTimestamp("N/A");
            r.setNasPort("N/A");
            r.setSourceHost("N/A");
            r.setCallingStationId("N/A");
            r.setResult("N/A");
            r.setTraceId("N/A");
            r.setNasIdentifier("N/A");
            r.setCalledStationId("N/A");
            r.setNasIpAddress("N/A");
            r.setFramedIpAddress("N/A");
            r.setAcctStatusType("N/A");
        }

        client.close();
        return r;
    }

    private SubnetUtils.SubnetInfo fromSubnetString(String subnet) {
        return new SubnetUtils(subnet).getInfo();
    }

}