package net.geant.wifimon.secureprocessor.resource;

import net.geant.wifimon.model.dto.AggregatedMeasurement;
import net.geant.wifimon.model.dto.NetTestMeasurement;
import net.geant.wifimon.model.dto.ProbesMeasurement;
import net.geant.wifimon.model.entity.Accesspoint;
import net.geant.wifimon.model.entity.CorrelationMethod;
import net.geant.wifimon.model.entity.RadiusStripped;
import net.geant.wifimon.model.entity.Subnet;
import net.geant.wifimon.secureprocessor.repository.AccesspointsRepository;
import net.geant.wifimon.secureprocessor.repository.SubnetRepository;
import net.geant.wifimon.secureprocessor.repository.VisualOptionsRepository;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import com.google.json.JsonSanitizer;

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

    private static final String ES_HOST = "elasticsearch.host";
    private static final String ES_PORT = "elasticsearch.port";
    private static final String ES_INDEXNAME_MEASUREMENT = "elasticsearch.indexnamemeasurement";
    private static final String ES_INDEXNAME_RADIUS = "elasticsearch.indexnameradius";
    private static final String ES_INDEXNAME_PROBES = "elasticsearch.indexnameprobes";
    private static final String ES_INDEXNAME_DHCP = "elasticsearch.indexnamedhcp";
    private static final String SSL_ENABLED = "xpack.security.enabled";
    private static final String SSL_USER_USERNAME = "ssl.http.user.username";
    private static final String SSL_USER_PHRASE = "ssl.http.user.phrase";
    private static final String SSL_KEYSTORE_FILEPATH = "ssl.http.keystore.filepath";
    private static final String SSL_KEYSTORE_PHRASE = "ssl.http.keystore.phrase";
    private static final String SSL_TRUSTSTORE_FILEPATH = "ssl.http.truststore.filepath";
    private static final String SSL_TRUSTSTORE_PHRASE = "ssl.http.truststore.phrase";
    private static final String SSL_KEY_PHRASE = "ssl.http.key.phrase";
    private static final String HMAC_SHA512_KEY = "sha.key";
    private static final String TIMESTAMP = "Timestamp";
    private static final String DOWNLOAD_THROUGHPUT = "Download-Throughput";
    private static final String UPLOAD_THROUGHPUT = "Upload-Throughput";
    private static final String LOCAL_PING = "Local-Ping";
    private static final String LOCATION = "Location";
    private static final String LOCATION_METHOD = "Location-Method";
    private static final String CLIENT_IP = "Client-Ip";
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_BROWSER = "User-Browser";
    private static final String USER_OS = "User-OS";
    private static final String TEST_TOOL = "Test-Tool";
    private static final String ORIGIN = "Origin";
    private static final String PROBE_NUMBER = "Probe-No";
    private static final String REQUESTER_SUBNET = "Requester-Subnet";
    private static final String ENCRYPTED_IP = "Encrypted-IP";
    private static final String AP_BUILDING = "Ap-Building";
    private static final String AP_FLOOR = "Ap-Floor";
    private static final String AP_LOCATION = "Ap-Location";
    private static final String AP_NOTES = "Ap-Notes";
    private static final String RADIUS_TIMESTAMP_KEYWORD = "RADIUS-Timestamp.keyword";
    private static final String RADIUS_TIMESTAMP = "RADIUS-Timestamp";
    private static final String DHCP_TIMESTAMP_KEYWORD = "DHCP-Timestamp.keyword";
    private static final String DHCP_TIMESTAMP = "DHCP-Timestamp";
    private static final String SERVICE_TYPE = "Service-Type";
    private static final String NAS_PORT_ID = "NAS-Port-Id";
    private static final String NAS_PORT_TYPE = "NAS-Port-Type";
    private static final String USERNAME = "User-Name";
    private static final String ACCT_SESSION_ID = "Acct-Session-Id";
    private static final String ACCT_MULTI_SESSION_ID = "Acct-Multi-Session-Id";
    private static final String CALLING_STATION_ID_KEYWORD = "Calling-Station-Id.keyword";
    private static final String CALLING_STATION_ID = "Calling-Station-Id";
    private static final String CALLED_STATION_ID = "Called-Station-Id";
    private static final String ACCT_AUTHENTIC = "Acct-Authentic";
    private static final String ACCT_STATUS_TYPE_KEYWORD = "Acct-Status-Type.keyword";
    private static final String ACCT_STATUS_TYPE = "Acct-Status-Type";
    private static final String NAS_IDENTIFIER = "NAS-Identifier";
    private static final String ACCT_DELAY_TIME = "Acct-Delay-Time";
    private static final String NAS_IP_ADDRESS = "NAS-IP-Address";
    private static final String FRAMED_IP_ADDRESS_KEYWORD = "Framed-IP-Address.keyword";
    private static final String FRAMED_IP_ADDRESS = "Framed-IP-Address";
    private static final String ACCT_UNIQUE_SESSION_ID = "Acct-Unique-Session-Id";
    private static final String REALM = "Realm";
    private static final String IP_ADDRESS = "IP-Address";
    private static final String IP_ADDRESS_KEYWORD = "IP-Address.keyword";
    private static final String MAC_ADDRESS = "MAC-Address";
    private static final String PROBE_ACCESSPOINT = "Accesspoint";
    private static final String PROBE_BIT_RATE = "Bit-Rate";
    private static final String PROBE_TX_POWER = "Tx-Power";
    private static final String PROBE_LINK_QUALITY = "Link-Quality";
    private static final String PROBE_SIGNAL_LEVEL = "Signal-Level";
    private static final String PROBE_MONITOR = "Monitor";

    private static Logger logger = Logger.getLogger(AggregatorResource.class.getName());
    private static RestHighLevelClient restHighLevelClient;

    @PostConstruct
    public void init() {
	    if (environment.getProperty(SSL_ENABLED).equals("true")) {
		    AggregatorResource.restHighLevelClient = initKeystoreClient();
            } else {
		    AggregatorResource.restHighLevelClient = initHttpClient();
            }
    }

    @POST
    @Path("/subnet")
    public Response correlate(@Context HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        List<Subnet> subnets = subnetRepository.findAll();
        if (subnets.isEmpty()) return Response.ok(false).build();

        List<SubnetUtils.SubnetInfo> s = subnets.stream().
                map(it -> it.fromSubnetString()).collect(Collectors.toList());

        for (SubnetUtils.SubnetInfo si : s) {
            if (si.isInRange(ip)) return Response.ok(true).build();
        }
        return Response.ok(false).build();
    }

    @POST
    @Path("/probes")
    public Response correlate(final ProbesMeasurement measurement, @Context HttpServletRequest request) {
	     Response response = null;

        try {
            // Get Wireless Network Performance Metrics
            String timestampJson = measurement.getTimestamp() != null ? "\"" + TIMESTAMP + "\" : " + measurement.getTimestamp() + ", " : "";
            String accesspointJson = measurement.getAccesspoint() != null ? "\"" + PROBE_ACCESSPOINT + "\" : \"" + measurement.getAccesspoint() + "\", " : "";
            String bitRateJson = measurement.getBitRate() != null ? "\"" + PROBE_BIT_RATE + "\" : " + measurement.getBitRate() + ", " : "";
            String txPowerJson = measurement.getTxPower() != null ? "\"" + PROBE_TX_POWER + "\" : " + measurement.getTxPower() + ", " : "";
            String linkQualityJson = measurement.getLinkQuality() != null ? "\"" + PROBE_LINK_QUALITY + "\" : " + measurement.getLinkQuality() + ", " : "";
            String signalLevelJson = measurement.getSignalLevel() != null ? "\"" + PROBE_SIGNAL_LEVEL + "\" : " + measurement.getSignalLevel() + ", " : "";
            String probeNoJson = measurement.getProbeNo() != null ? "\"" + PROBE_NUMBER + "\" : " + measurement.getProbeNo() + ", " : "";
	    String monitorJson = measurement.getMonitor() != null ? "\"" + PROBE_MONITOR + "\" : " + measurement.getMonitor() : "";

            // Construct JSON object that will be stored in Elasticsearch
            String jsonStringDraft = "{" +
                    timestampJson + accesspointJson + bitRateJson + txPowerJson + 
		    linkQualityJson + signalLevelJson + probeNoJson + monitorJson + "}";

            String jsonString = jsonStringDraft.replace("\", }", "\"}");

            // Store measurements in elasticsearch
	    String sanitizedJsonString = JsonSanitizer.sanitize(jsonString);
            indexMeasurementProbes(sanitizedJsonString);

            return Response.ok().build();

        } catch (Exception e) {
	    logger.info(e.toString());
            response = null;
        }
        return response;
    }

    @POST
    @Path("/add")
    public Response correlate(final NetTestMeasurement measurement, @Context HttpServletRequest request) {
        Response response;
	String agent = request.getHeader(USER_AGENT) != null || request.getHeader(USER_AGENT).isEmpty() ? request.getHeader(USER_AGENT) : "N/A";

        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) return Response.serverError().build();

        // In the following, we find the registered subnet corresponding to the requester IP
        List<Subnet> subnets = subnetRepository.findAll();
        if (subnets == null || subnets.isEmpty()) return Response.ok(false).build();
        List<SubnetUtils.SubnetInfo> s = subnets.stream().
                map(it -> it.fromSubnetString()).collect(Collectors.toList());

        String foundSubnet = "";
        for (SubnetUtils.SubnetInfo si : s) {
            if (si.isInRange(ip)) {
                foundSubnet = si.toString();
            }
        }

        // Trim excessive whitespace from received Subnet Utils information
        String subnetInfoWithoutWhitespace = foundSubnet.replaceAll("\\s+", " ");
        // Split Subnet Utils information based on remaining spaces
        String[] splitSubnetInfo = subnetInfoWithoutWhitespace.split(" ");
        // Get the third String from the list above
        String subnet3 = splitSubnetInfo[2];
        // This String is inside brackets. We will remove these brackets
        String requesterSubnet = subnet3.replace("[", "");
        requesterSubnet = requesterSubnet.replace("]", "");

        // Encrypt Requester IP using HMAC SHA512 Algorithm
        EncryptClass encryptClass = new EncryptClass();
        String encryptedIP = "";
        try {
            encryptedIP = encryptClass.encrypt(ip, environment.getProperty(HMAC_SHA512_KEY));
            encryptedIP = encryptedIP.toLowerCase();
        } catch (Exception e) {
	    logger.info(e.toString());
        }

        // What is the correlation method defined by the administrator in the WiFiMon GUI?
        String correlationmethod = visualOptionsRepository.findCorrelationmethod();
        if (correlationmethod == null || correlationmethod.isEmpty()) {
            correlationmethod = "RADIUS_ONLY";
        }

	RadiusStripped r;
	Accesspoint ap;

        // Perform correlations and insert new measurements in the elasticsearch cluster
        if (correlationmethod.equals(CorrelationMethod.DHCP_AND_RADIUS.toString())) {
            String callingStationIdTemp = retrieveLastMacEntryByIp(encryptedIP);
            r = retrieveLastRadiusEntryByMac(callingStationIdTemp);
        } else {
            r = retrieveLastRadiusEntryByIp(encryptedIP);
        }

        try {
            if (r != null) {
                // There are RADIUS Logs corresponding to the received measurement
                //String calledStationIdTemp = r.getCalledStationId().substring(0, 30).toUpperCase().replace("-", ":");
                String calledStationIdTemp = r.getCalledStationId();
                ap = accesspointsRepository.find(calledStationIdTemp);
                response = addElasticMeasurement(joinMeasurement(measurement, r, ap, agent), requesterSubnet, encryptedIP);
            } else {
                // There are not RADIUS Logs corresponding to the received measurement
                response = addElasticMeasurement(joinMeasurement(measurement, r, null, agent), requesterSubnet, encryptedIP);
            }
        } catch (Exception e) {
	    logger.info(e.toString());
            response = null;
        }

        return response;
    }

    private AggregatedMeasurement joinMeasurement(NetTestMeasurement measurement, RadiusStripped radius, Accesspoint accesspoint, String agent) {
        AggregatedMeasurement m = new AggregatedMeasurement();
        m.setTimestamp(System.currentTimeMillis());
        m.setDownloadThroughput(measurement.getDownloadThroughput());
        m.setUploadThroughput(measurement.getUploadThroughput());
        m.setLocalPing(measurement.getLocalPing());
        m.setLatitude(measurement.getLatitude() != null ? Double.valueOf(measurement.getLatitude()) : null);
        m.setLongitude(measurement.getLongitude() != null ? Double.valueOf(measurement.getLongitude()) : null);
        m.setLocationMethod(measurement.getLocationMethod());
        m.setUserAgent(agent);
        m.setTestTool(measurement.getTestTool());
        m.setRadiusTimestamp(radius != null ? radius.getRadiusTimestamp() : null);
        m.setServiceType(radius != null ? radius.getServiceType() : null);
        m.setNasPortId(radius != null ? radius.getNasPortId() : null);
        m.setNasPortType(radius != null ? radius.getNasPortType() : null);
        m.setUserName(radius != null ? radius.getUserName() : null);
        m.setAcctSessionId(radius != null ? radius.getAcctSessionId() : null);
        m.setAcctMultiSessionId(radius != null ? radius.getAcctMultiSessionId() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId() : null);
        m.setAcctAuthentic(radius != null ? radius.getAcctAuthentic() : null);
        m.setAcctStatusType(radius != null ? radius.getAcctStatusType() : null);
        m.setNasIdentifier(radius != null ? radius.getNasIdentifier() : null);
        m.setAcctDelayTime(radius != null ? radius.getAcctDelayTime() : null);
        m.setNasIpAddress(radius != null ? radius.getNasIpAddress() : null);
        m.setFramedIpAddress(radius != null ? radius.getFramedIpAddress() : null);
        m.setAcctUniqueSessionId(radius != null ? radius.getAcctUniqueSessionId() : null);
        m.setRealm(radius != null ? radius.getRealm() : null);
        m.setApBuilding(accesspoint != null ? accesspoint.getBuilding() : null);
        m.setApFloor(accesspoint != null ? accesspoint.getFloor() : null);
        m.setApLatitude(accesspoint != null ? Double.valueOf(accesspoint.getLatitude()) : null);
        m.setApLongitude(accesspoint != null ? Double.valueOf(accesspoint.getLongitude()) : null);
        m.setApNotes(accesspoint != null ? accesspoint.getNotes() : null);
        return m;
    }

    private Response addElasticMeasurement(AggregatedMeasurement measurement, String requesterSubnet, String encryptedIP) {
        String userAgent = measurement.getUserAgent();

        // Section for User Operating System
        String userOs;
        if (userAgent.toUpperCase().contains("WINDOWS")) {
            userOs = "Windows";
        } else if (userAgent.toUpperCase().contains("MAC")) {
            userOs = "Mac OS X and iOS";
        } else if (userAgent.toUpperCase().contains("X11")) {
            userOs = "Linux";
        } else if (userAgent.toUpperCase().contains("ANDROID")) {
            userOs = "Android";
        } else {
            userOs = "N/A";
        }

        // Section for User Browser
        String userBrowser;
        if (userAgent.toUpperCase().contains("CHROME") && !userAgent.toUpperCase().contains("EDGE")) {
            userBrowser = "Chrome";
        } else if (userAgent.toUpperCase().contains("SAFARI") && !userAgent.toUpperCase().contains("CHROME")) {
            userBrowser = "Safari";
        } else if (userAgent.toUpperCase().contains("FIREFOX")) {
            userBrowser = "Firefox";
        } else if (userAgent.toUpperCase().contains("MSIE")) {
            userBrowser = "Internet Explorer";
        } else if (userAgent.toUpperCase().contains("EDGE")) {
            userBrowser = "Microsoft Edge";
        } else {
            userBrowser = "N/A";
        }

	// Define if the measurement is from the end users or a particular HW Probe
	String measurementOrigin = "";
	String probeNumber = "";
	String testtoolUsed = measurement.getTestTool();
	if (testtoolUsed.indexOf("-") == -1) {
		measurementOrigin = "User";
	} else {
		measurementOrigin = "Probe";
		probeNumber = testtoolUsed.split("-")[1];
	}

        // Define Strings for the different measurement fields and results from correlation with RADIUS Logs
        String downloadThroughputJson = measurement.getDownloadThroughput() != null ? "\"" + DOWNLOAD_THROUGHPUT + "\" : " + measurement.getDownloadThroughput() + ", " : "";
        String uploadThroughputJson = measurement.getUploadThroughput() != null ? "\"" + UPLOAD_THROUGHPUT + "\" : " + measurement.getUploadThroughput() + ", " : "";
        String localPingJson = measurement.getLocalPing() != null ? "\"" + LOCAL_PING + "\" : " + measurement.getLocalPing() + ", " : "";
        String locationJson = measurement.getLatitude() != null ? "\"" + LOCATION + "\" : \"" + measurement.getLatitude() + "," + measurement.getLongitude() + "\", " : "";
        String locationMethodJson = measurement.getLocationMethod() != null ? "\"" + LOCATION_METHOD + "\" : \"" + measurement.getLocationMethod() + "\", " : "";
        String clientIpJson = measurement.getClientIp() != null ? "\"" + CLIENT_IP + "\" : \"" + measurement.getClientIp() + "\", " : "";
        String userAgentJson = measurement.getUserAgent() != null ? "\"" + USER_AGENT + "\" : \"" + measurement.getUserAgent() + "\", " : "";
        String userBrowserJson = userBrowser != null ? "\"" + USER_BROWSER + "\" : \"" + userBrowser + "\", " : "";
        String userOsJson = userOs != null ? "\"" + USER_OS + "\" : \"" + userOs + "\", " : "";
        String testToolJson = measurement.getTestTool() != null ? "\"" + TEST_TOOL + "\" : \"" + measurement.getTestTool() + "\", " : "";
        String radiusTimestampJson = measurement.getRadiusTimestamp() != null ? "\"" + RADIUS_TIMESTAMP + "\" : \"" + measurement.getRadiusTimestamp() + "\", " : "";
        String serviceTypeJson = measurement.getServiceType() != null ? "\"" + SERVICE_TYPE + "\" : \"" + measurement.getServiceType() + "\", " : "";
        String nasPortIdJson = measurement.getNasPortId() != null ? "\"" + NAS_PORT_ID + "\" : \"" + measurement.getNasPortId() + "\", " : "";
        String nasPortTypeJson = measurement.getNasPortType() != null ? "\"" + NAS_PORT_TYPE + "\" : \"" + measurement.getNasPortType() + "\", " : "";
        String usernameJson = measurement.getUserName() != null ? "\"" + USERNAME + "\" : \"" + measurement.getUserName() + "\", " : "";
        String acctSessionIdJson = measurement.getAcctSessionId() != null ? "\"" + ACCT_SESSION_ID + "\" : \"" + measurement.getAcctSessionId() + "\", " : "";
        String acctMultiSessionIdJson = measurement.getAcctMultiSessionId() != null ? "\"" + ACCT_MULTI_SESSION_ID + "\" : \"" + measurement.getAcctMultiSessionId() + "\", " : "";
        String callingStationIdJson = measurement.getCallingStationId() != null ? "\"" + CALLING_STATION_ID + "\" : \"" + measurement.getCallingStationId() + "\", " : "";
        String calledStationIdJson = measurement.getCalledStationId() != null ? "\"" + CALLED_STATION_ID + "\" : \"" + measurement.getCalledStationId() + "\", " : "";
        String acctAuthenticJson = measurement.getAcctAuthentic() != null ? "\"" + ACCT_AUTHENTIC + "\" : \"" + measurement.getAcctAuthentic() + "\", " : "";
        String acctStatusTypeJson = measurement.getAcctStatusType() != null ? "\"" + ACCT_STATUS_TYPE + "\" : \"" + measurement.getAcctStatusType() + "\", " : "";
        String nasIdentifierJson = measurement.getNasIdentifier() != null ? "\"" + NAS_IDENTIFIER + "\" : \"" + measurement.getNasIdentifier() + "\", " : "";
        String acctDelayTimeJson = measurement.getAcctDelayTime() != null ? "\"" + ACCT_DELAY_TIME + "\" : \"" + measurement.getAcctDelayTime() + "\", " : "";
        String nasIpAddressJson = measurement.getNasIpAddress() != null ? "\"" + NAS_IP_ADDRESS + "\" : \"" + measurement.getNasIpAddress() + "\", " : "";
        String framedIpAddressJson = measurement.getFramedIpAddress() != null ? "\"" + FRAMED_IP_ADDRESS + "\" : \"" + measurement.getFramedIpAddress() + "\", " : "";
        String acctUniqueSessionIdJson = measurement.getAcctUniqueSessionId() != null ? "\"" + ACCT_UNIQUE_SESSION_ID + "\" : \"" + measurement.getAcctUniqueSessionId() + "\", " : "";
        String realmJson = measurement.getRealm() != null ? "\"" + REALM + "\" : \"" + measurement.getRealm() + "\", " : "";
        String measurementOriginJson = measurementOrigin != null ? "\"" + ORIGIN + "\" : \"" + measurementOrigin + "\", " : "";
        String probeNumberJson = probeNumber != null ? "\"" + PROBE_NUMBER + "\" : \"" + probeNumber + "\", " : "";
        String requesterSubnetJson = requesterSubnet != null ? "\"" + REQUESTER_SUBNET + "\" : \"" + requesterSubnet + "\", " : "";
        String encryptedIPJson = encryptedIP != null ? "\"" + ENCRYPTED_IP + "\" : \"" + encryptedIP + "\", " : "";
        String apBuildingJson = measurement.getApBuilding() != null ? "\"" + AP_BUILDING + "\" : \"" + measurement.getApBuilding() + "\", " : "";
        String apFloorJson = measurement.getApFloor() != null ? "\"" + AP_FLOOR + "\" : \"" + measurement.getApFloor() + "\", " : "";
        String apLocationJson = measurement.getApLatitude() != null ? "\"" + AP_LOCATION + "\" : \"" + measurement.getApLatitude() + "," + measurement.getApLongitude() + "\", " : "";
        String apNotesJson = measurement.getApNotes() != null ? "\"" + AP_NOTES + "\" : \"" + measurement.getApNotes() + "\"" : "";

        // Build the Json String to store in the elasticsearch cluster
        String jsonStringDraft = "{" +
                "\"" + TIMESTAMP + "\" : " + measurement.getTimestamp() + ", " +
                downloadThroughputJson + uploadThroughputJson + localPingJson +
                locationJson + locationMethodJson + userAgentJson + userBrowserJson + 
		userOsJson + testToolJson + radiusTimestampJson + serviceTypeJson +
 		nasPortIdJson + nasPortTypeJson + usernameJson + acctSessionIdJson +
		acctMultiSessionIdJson + callingStationIdJson + calledStationIdJson +
		acctAuthenticJson + acctStatusTypeJson + nasIdentifierJson +
		acctDelayTimeJson + nasIpAddressJson + framedIpAddressJson +
		acctUniqueSessionIdJson + realmJson + clientIpJson +
                measurementOriginJson + probeNumberJson + requesterSubnetJson + 
		encryptedIPJson + apBuildingJson + apFloorJson + apLocationJson +
                apNotesJson + "}";

        String jsonString = jsonStringDraft.replace("\", }", "\"}");

        // Store measurements in elasticsearch
	String sanitizedJsonString = JsonSanitizer.sanitize(jsonString);
        indexMeasurement(sanitizedJsonString);

        return Response.ok().build();
    }

    private String retrieveLastMacEntryByIp(String ip) {
        String macAddress = "";
        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
            .query(QueryBuilders.matchAllQuery())
            .sort(new FieldSortBuilder(DHCP_TIMESTAMP_KEYWORD).order(SortOrder.DESC))
            .from(0)
            .fetchSource(new String[]{DHCP_TIMESTAMP, IP_ADDRESS, MAC_ADDRESS}, null)
            .postFilter(QueryBuilders.termQuery(IP_ADDRESS_KEYWORD, ip))
            .size(1)
            .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_DHCP))
                    .source(builder);
            final SearchResponse response = AggregatorResource.restHighLevelClient.search(request, RequestOptions.DEFAULT);

            final SearchHits hits = response.getHits();
            if (response.getHits().getTotalHits().value > 0) {
                    SearchHit hit = hits.getAt(0);
                    Map map = hit.getSourceAsMap();
                    macAddress = (map.get(MAC_ADDRESS) != null) ? map.get(MAC_ADDRESS).toString() : "N/A";
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
        return macAddress;
    }

    private RadiusStripped retrieveLastRadiusEntryByIp(String ip) {
        // Correlation with RADIUS Logs based on the Framed IP Address field

        RadiusStripped r = new RadiusStripped();

        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .query(QueryBuilders.matchAllQuery())
                    .sort(new FieldSortBuilder(RADIUS_TIMESTAMP_KEYWORD).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{RADIUS_TIMESTAMP, SERVICE_TYPE, NAS_PORT_ID,
                            NAS_PORT_TYPE, USERNAME, ACCT_SESSION_ID, ACCT_MULTI_SESSION_ID,
			    CALLING_STATION_ID, CALLED_STATION_ID, ACCT_AUTHENTIC,
                            ACCT_STATUS_TYPE, NAS_IDENTIFIER, ACCT_DELAY_TIME,
		            NAS_IP_ADDRESS, FRAMED_IP_ADDRESS, ACCT_UNIQUE_SESSION_ID, REALM}, null)
                    .postFilter(QueryBuilders.termQuery(FRAMED_IP_ADDRESS_KEYWORD, ip))
                    .query(QueryBuilders.termQuery(ACCT_STATUS_TYPE_KEYWORD, "Start"))
                    .size(1)
                    .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                    .source(builder);
            final SearchResponse response = AggregatorResource.restHighLevelClient.search(request, RequestOptions.DEFAULT);
            final SearchHits hits = response.getHits();
            if (response.getHits().getTotalHits().value > 0) {
                    SearchHit hit = hits.getAt(0);
                    Map map = hit.getSourceAsMap();
                    r.setRadiusTimestamp(((map.get(RADIUS_TIMESTAMP) != null) ? map.get(RADIUS_TIMESTAMP).toString() : "N/A"));
                    r.setServiceType(((map.get(SERVICE_TYPE) != null) ? map.get(SERVICE_TYPE).toString() : "N/A"));
                    r.setNasPortId(((map.get(NAS_PORT_ID) != null) ? map.get(NAS_PORT_ID).toString() : "N/A"));
                    r.setNasPortType(((map.get(NAS_PORT_TYPE) != null) ? map.get(NAS_PORT_TYPE).toString() : "N/A"));
                    r.setUserName(((map.get(USERNAME) != null) ? map.get(USERNAME).toString() : "N/A"));
                    r.setAcctSessionId(((map.get(ACCT_SESSION_ID) != null) ? map.get(ACCT_SESSION_ID).toString() : "N/A"));
                    r.setAcctMultiSessionId(((map.get(ACCT_MULTI_SESSION_ID) != null) ? map.get(ACCT_MULTI_SESSION_ID).toString() : "N/A"));
                    r.setCallingStationId(((map.get(CALLING_STATION_ID) != null) ? map.get(CALLING_STATION_ID).toString() : "N/A"));
                    r.setCalledStationId(((map.get(CALLED_STATION_ID) != null) ? map.get(CALLED_STATION_ID).toString() : "N/A"));
                    r.setAcctAuthentic(((map.get(ACCT_AUTHENTIC) != null) ? map.get(ACCT_AUTHENTIC).toString() : "N/A"));
                    r.setAcctStatusType(((map.get(ACCT_STATUS_TYPE) != null) ? map.get(ACCT_STATUS_TYPE).toString() : "N/A"));
                    r.setNasIdentifier(((map.get(NAS_IDENTIFIER) != null) ? map.get(NAS_IDENTIFIER).toString() : "N/A"));
                    r.setAcctDelayTime(((map.get(ACCT_DELAY_TIME) != null) ? map.get(ACCT_DELAY_TIME).toString() : "N/A"));
                    r.setNasIpAddress(((map.get(NAS_IP_ADDRESS) != null) ? map.get(NAS_IP_ADDRESS).toString() : "N/A"));
                    r.setFramedIpAddress(((map.get(FRAMED_IP_ADDRESS) != null) ? map.get(FRAMED_IP_ADDRESS).toString() : "N/A"));
                    r.setAcctUniqueSessionId(((map.get(ACCT_UNIQUE_SESSION_ID) != null) ? map.get(ACCT_UNIQUE_SESSION_ID).toString() : "N/A"));
                    r.setRealm(((map.get(REALM) != null) ? map.get(REALM).toString() : "N/A"));
            } else {
                r = null;
            }
        } catch (Exception e) {
	    logger.info(e.toString());
            r = null;
        }
        return r;
    }

    private RadiusStripped retrieveLastRadiusEntryByMac(String mac) {
        // Correlation with RADIUS Logs based on the Calling Station Id field

        RadiusStripped r = new RadiusStripped();

        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .query(QueryBuilders.matchAllQuery())
                    .sort(new FieldSortBuilder(RADIUS_TIMESTAMP_KEYWORD).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{RADIUS_TIMESTAMP, SERVICE_TYPE, NAS_PORT_ID,
			    NAS_PORT_TYPE, USERNAME, ACCT_SESSION_ID, ACCT_MULTI_SESSION_ID,
			    CALLING_STATION_ID, CALLED_STATION_ID, ACCT_AUTHENTIC, 
			    ACCT_STATUS_TYPE, NAS_IDENTIFIER, ACCT_DELAY_TIME, NAS_IP_ADDRESS,
			    FRAMED_IP_ADDRESS, ACCT_UNIQUE_SESSION_ID, REALM}, null)
                    .postFilter(QueryBuilders.wildcardQuery(CALLING_STATION_ID_KEYWORD, "*" + mac.replace(":", "-").toLowerCase() + "*"))
                    .size(1)
                    .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                    .source(builder);

            final SearchResponse response = AggregatorResource.restHighLevelClient.search(request, RequestOptions.DEFAULT);
            final SearchHits hits = response.getHits();

            if (response.getHits().getTotalHits().value > 0) {
		    SearchHit hit = hits.getAt(0); 
                    Map map = hit.getSourceAsMap();
                    r.setRadiusTimestamp(((map.get(RADIUS_TIMESTAMP) != null) ? map.get(RADIUS_TIMESTAMP).toString() : "N/A"));
                    r.setServiceType(((map.get(SERVICE_TYPE) != null) ? map.get(SERVICE_TYPE).toString() : "N/A"));
                    r.setNasPortId(((map.get(NAS_PORT_ID) != null) ? map.get(NAS_PORT_ID).toString() : "N/A"));
                    r.setNasPortType(((map.get(NAS_PORT_TYPE) != null) ? map.get(NAS_PORT_TYPE).toString() : "N/A"));
                    r.setUserName(((map.get(USERNAME) != null) ? map.get(USERNAME).toString() : "N/A"));
                    r.setAcctSessionId(((map.get(ACCT_SESSION_ID) != null) ? map.get(ACCT_SESSION_ID).toString() : "N/A"));
                    r.setAcctMultiSessionId(((map.get(ACCT_MULTI_SESSION_ID) != null) ? map.get(ACCT_MULTI_SESSION_ID).toString() : "N/A"));
                    r.setCallingStationId(((map.get(CALLING_STATION_ID) != null) ? map.get(CALLING_STATION_ID).toString() : "N/A"));
                    r.setCalledStationId(((map.get(CALLED_STATION_ID) != null) ? map.get(CALLED_STATION_ID).toString() : "N/A"));
                    r.setAcctAuthentic(((map.get(ACCT_AUTHENTIC) != null) ? map.get(ACCT_AUTHENTIC).toString() : "N/A"));
                    r.setAcctStatusType(((map.get(ACCT_STATUS_TYPE) != null) ? map.get(ACCT_STATUS_TYPE).toString() : "N/A"));
                    r.setNasIdentifier(((map.get(NAS_IDENTIFIER) != null) ? map.get(NAS_IDENTIFIER).toString() : "N/A"));
                    r.setAcctDelayTime(((map.get(ACCT_DELAY_TIME) != null) ? map.get(ACCT_DELAY_TIME).toString() : "N/A"));
                    r.setNasIpAddress(((map.get(NAS_IP_ADDRESS) != null) ? map.get(NAS_IP_ADDRESS).toString() : "N/A"));
                    r.setFramedIpAddress(((map.get(FRAMED_IP_ADDRESS) != null) ? map.get(FRAMED_IP_ADDRESS).toString() : "N/A"));
                    r.setAcctUniqueSessionId(((map.get(ACCT_UNIQUE_SESSION_ID) != null) ? map.get(ACCT_UNIQUE_SESSION_ID).toString() : "N/A"));
                    r.setRealm(((map.get(REALM) != null) ? map.get(REALM).toString() : "N/A"));
            } else {
                r = null;
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
        return r;
    }

    private void indexMeasurement(String jsonString) {
        // Store received measurements in Elasticsearch
        try {
            IndexRequest indexRequest = new IndexRequest(environment.getProperty(ES_INDEXNAME_MEASUREMENT));
            indexRequest.source(jsonString, XContentType.JSON);
            AggregatorResource.restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    private void indexMeasurementProbes(String jsonString) {
        // Store received Wireless Network Performance Metrics (from WiFiMon Hardware Probes) in Elasticsearch
        try {
        IndexRequest indexRequest = new IndexRequest(
                environment.getProperty(ES_INDEXNAME_PROBES));
        indexRequest.source(jsonString, XContentType.JSON);
            AggregatorResource.restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    // Initialize High Level REST Client for HTTP
    private RestHighLevelClient initHttpClient() {

	RestHighLevelClient restClient = null;
        try {
            restClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(
                                    environment.getProperty(ES_HOST),
                                    Integer.parseInt(environment.getProperty(ES_PORT)),
                                    "http")));
        } catch (Exception e) {
	    logger.info(e.toString());
        }

        return restClient;
    }

    // A big part of the code that follows was taken from the Search Guard GitHub repository about Elasticsearch Java High Level REST Client
    // This method initializes a High Level REST Client using Keystore Certificates
    private RestHighLevelClient initKeystoreClient() {
	    RestHighLevelClient restClient = null;

        try {
            char[] truststorePassword = environment.getProperty(SSL_TRUSTSTORE_PHRASE).toCharArray();
            char[] keystorePassword = environment.getProperty(SSL_KEYSTORE_PHRASE).toCharArray();
            char[] keyPassword = environment.getProperty(SSL_KEY_PHRASE).toCharArray();

            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            environment.getProperty(SSL_USER_USERNAME),
                            environment.getProperty(SSL_USER_PHRASE)));

            SSLContext sslContextFromJks = SSLContexts
                    .custom()
                    .loadKeyMaterial(
                            new File(environment.getProperty(SSL_KEYSTORE_FILEPATH)),
                            keystorePassword,
                            keyPassword)
                    .loadTrustMaterial(
                            new File(environment.getProperty(SSL_TRUSTSTORE_FILEPATH)),
                            truststorePassword, null)
                    .build();


            restClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(
                            environment.getProperty(ES_HOST),
                            Integer.parseInt(environment.getProperty(ES_PORT)),
                            "https"))
                            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
                                    .setSSLContext(sslContextFromJks)
                            ));
        } catch (Exception e) {
	    logger.info(e.toString());
        }

        return restClient;
    }

    // Used to encrypt IP addresses of end users
    public class EncryptClass {
        public String encrypt(String myString, String myKey) {
            Mac shaHmac512 = null;
            String result = null;
            String key = myKey;

            try {
                byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
                final String algorithmHmac = "HmacSHA512";
                shaHmac512 = Mac.getInstance(algorithmHmac);
                SecretKeySpec keySpec = new SecretKeySpec(byteKey, algorithmHmac);
                shaHmac512.init(keySpec);
                byte[] macData = shaHmac512.
                        doFinal(myString.getBytes(StandardCharsets.UTF_8));
                result = bytesToHex(macData);
            } catch (Exception e) {
	    	logger.info(e.toString());
            }

            return result;
        }

        public String bytesToHex(byte[] bytes) {
            final char[] hexArray = "0123456789ABCDEF".toCharArray();
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
    }
}
