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
    private static final String AGENT = "User-Agent";
    private static final String TIMESTAMP = "Timestamp";
    private static final String USERNAME = "User-Name";
    private static final String CALLING_STATION_ID = "Calling-Station-Id";
    private static final String NAS_PORT = "nas_port";
    private static final String CALLED_STATION_ID = "Called-Station-Id";
    private static final String NAS_IP_ADDRESS = "NAS-IP-Address";
    private static final String NAS_IDENTIFIER = "NAS-Identifier";
    private static final String ACCT_STATUS_TYPE = "Acct-Status-Type";
    private static final String FRAMED_IP_ADDRESS = "Framed-IP-Address";

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
            String bitRateJson = measurement.getBitRate() != null ? "\"bitRate\" : " + measurement.getBitRate() + ", " : "";
            String txPowerJson = measurement.getTxPower() != null ? "\"txPower\" : " + measurement.getTxPower() + ", " : "";
            String linkQualityJson = measurement.getLinkQuality() != null ? "\"linkQuality\" : " + measurement.getLinkQuality() + ", " : "";
            String signalLevelJson = measurement.getSignalLevel() != null ? "\"signalLevel\" : " + measurement.getSignalLevel() + ", " : "";
            String testToolJson = measurement.getTestTool() != null ? "\"testTool\" : \"" + measurement.getTestTool() + "\"" : "";

            // Construct JSON object that will be stored in Elasticsearch
            String jsonStringDraft = "{" +
                    "\"timestamp\" : " + measurement.getTimestamp() + ", " +
                    bitRateJson + txPowerJson + linkQualityJson +
                    signalLevelJson + testToolJson + "}";

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
	String agent = request.getHeader(AGENT) != null || request.getHeader(AGENT).isEmpty() ? request.getHeader(AGENT) : "N/A";

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
                String calledStationIdTemp = r.getCalledStationId().substring(0, 17).toUpperCase().replace("-", ":");
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
        m.setUserName(radius != null ? radius.getUserName() : null);
        m.setNasPort(radius != null ? radius.getNasPort() : null);
        m.setCallingStationId(radius != null ? radius.getCallingStationId() : null);
        m.setNasIdentifier(radius != null ? radius.getNasIdentifier() : null);
        m.setCalledStationId(radius != null ? radius.getCalledStationId() : null);
        m.setNasIpAddress(radius != null ? radius.getNasIpAddress() : null);
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
	System.out.println(measurementOrigin);

        // Define Strings for the different measurement fields and results from correlation with RADIUS Logs
        String downloadThroughputJson = measurement.getDownloadThroughput() != null ? "\"downloadThroughput\" : " + measurement.getDownloadThroughput() + ", " : "";
        String uploadThroughputJson = measurement.getUploadThroughput() != null ? "\"uploadThroughput\" : " + measurement.getUploadThroughput() + ", " : "";
        String localPingJson = measurement.getLocalPing() != null ? "\"localPing\" : " + measurement.getLocalPing() + ", " : "";
        String locationJson = measurement.getLatitude() != null ? "\"location\" : \"" + measurement.getLatitude() + "," + measurement.getLongitude() + "\", " : "";
        String locationMethodJson = measurement.getLocationMethod() != null ? "\"locationMethod\" : \"" + measurement.getLocationMethod() + "\", " : "";
        String clientIpJson = measurement.getClientIp() != null ? "\"clientIp\" : \"" + measurement.getClientIp() + "\", " : "";
        String userAgentJson = measurement.getUserAgent() != null ? "\"userAgent\" : \"" + measurement.getUserAgent() + "\", " : "";
        String userBrowserJson = userBrowser != null ? "\"userBrowser\" : \"" + userBrowser + "\", " : "";
        String userOsJson = userOs != null ? "\"userOS\" : \"" + userOs + "\", " : "";
        String testToolJson = measurement.getTestTool() != null ? "\"testTool\" : \"" + measurement.getTestTool() + "\", " : "";
        String measurementOriginJson = measurementOrigin != null ? "\"origin\" : \"" + measurementOrigin + "\", " : "";
        String probeNumberJson = probeNumber != null ? "\"probeNo\" : \"" + probeNumber + "\", " : "";
        String requesterSubnetJson = requesterSubnet != null ? "\"requesterSubnet\" : \"" + requesterSubnet + "\", " : "";
        String encryptedIPJson = encryptedIP != null ? "\"encryptedIP\" : \"" + encryptedIP + "\", " : "";
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

        // Build the Json String to store in the elasticsearch cluster
        String jsonStringDraft = "{" +
                "\"timestamp\" : " + measurement.getTimestamp() + ", " +
                downloadThroughputJson + uploadThroughputJson + localPingJson +
                locationJson + locationMethodJson +
                userAgentJson + userBrowserJson + userOsJson + clientIpJson +
                testToolJson + measurementOriginJson + probeNumberJson +
	       	requesterSubnetJson + encryptedIPJson + usernameJson + nasPortJson + 
		callingStationIdJson + nasIdentifierJson + calledStationIdJson +
                nasIpAddressJson + apBuildingJson + apFloorJson + apLocationJson +
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
            .sort(new FieldSortBuilder(TIMESTAMP).order(SortOrder.DESC))
            .from(0)
            .fetchSource(new String[]{TIMESTAMP, "IP-Address", "MAC-Address"}, null)
            .postFilter(QueryBuilders.termQuery("IP-Address", ip))
            .size(1)
            .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_DHCP))
                    .source(builder);
            final SearchResponse response = AggregatorResource.restHighLevelClient.search(request, RequestOptions.DEFAULT);

            final SearchHits hits = response.getHits();
            if (response.getHits().getTotalHits().value > 0) {
                    SearchHit hit = hits.getAt(0);
                    Map map = hit.getSourceAsMap();
                    macAddress = (map.get("MAC-Address") != null) ? map.get("MAC-Address").toString() : "N/A";
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
                    .sort(new FieldSortBuilder(TIMESTAMP).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{USERNAME, TIMESTAMP,
                            NAS_PORT, CALLING_STATION_ID,
                            NAS_IDENTIFIER, CALLED_STATION_ID, NAS_IP_ADDRESS,
                            FRAMED_IP_ADDRESS, ACCT_STATUS_TYPE}, null)
                    .postFilter(QueryBuilders.termQuery(FRAMED_IP_ADDRESS, ip))
                    .query(QueryBuilders.termQuery(ACCT_STATUS_TYPE, "Start"))
                    .size(1)
                    .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                    .source(builder);
            final SearchResponse response = AggregatorResource.restHighLevelClient.search(request, RequestOptions.DEFAULT);

            final SearchHits hits = response.getHits();
            if (response.getHits().getTotalHits().value > 0) {
                    SearchHit hit = hits.getAt(0);
                    Map map = hit.getSourceAsMap();
                    r.setUserName(((map.get(USERNAME) != null) ? map.get(USERNAME).toString() : "N/A"));
                    r.setTimestamp(((map.get(TIMESTAMP) != null) ? map.get(TIMESTAMP).toString() : "N/A"));
                    r.setNasPort(((map.get(NAS_PORT) != null) ? map.get(NAS_PORT).toString() : "N/A"));
                    r.setCallingStationId(((map.get(CALLING_STATION_ID) != null) ? map.get(CALLING_STATION_ID).toString() : "N/A"));
                    r.setNasIdentifier(((map.get(NAS_IDENTIFIER) != null) ? map.get(NAS_IDENTIFIER).toString() : "N/A"));
                    r.setCalledStationId(((map.get(CALLED_STATION_ID) != null) ? map.get(CALLED_STATION_ID).toString() : "N/A"));
                    r.setNasIpAddress(((map.get(NAS_IP_ADDRESS) != null) ? map.get(NAS_IP_ADDRESS).toString() : "N/A"));
                    r.setFramedIpAddress(((map.get(FRAMED_IP_ADDRESS) != null) ? map.get(FRAMED_IP_ADDRESS).toString() : "N/A"));
                    r.setAcctStatusType(((map.get(ACCT_STATUS_TYPE) != null) ? map.get(ACCT_STATUS_TYPE).toString() : "N/A"));
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
                    .sort(new FieldSortBuilder(TIMESTAMP).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{USERNAME, TIMESTAMP,
                            NAS_PORT, CALLING_STATION_ID,
                            NAS_IDENTIFIER, CALLED_STATION_ID, NAS_IP_ADDRESS,
                            FRAMED_IP_ADDRESS, ACCT_STATUS_TYPE}, null)
                    .postFilter(QueryBuilders.wildcardQuery(CALLING_STATION_ID, "*" + mac.replace(":", "-").toLowerCase() + "*"))
                    .size(1)
                    .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                    .source(builder);

            final SearchResponse response = AggregatorResource.restHighLevelClient.search(request, RequestOptions.DEFAULT);
            final SearchHits hits = response.getHits();

            if (response.getHits().getTotalHits().value > 0) {
		    SearchHit hit = hits.getAt(0); 
                    Map map = hit.getSourceAsMap();
                    r.setUserName(((map.get(USERNAME) != null) ? map.get(USERNAME).toString() : "N/A"));
                    r.setTimestamp(((map.get(TIMESTAMP) != null) ? map.get(TIMESTAMP).toString() : "N/A"));
                    r.setNasPort(((map.get(NAS_PORT) != null) ? map.get(NAS_PORT).toString() : "N/A"));
                    r.setCallingStationId(((map.get(CALLING_STATION_ID) != null) ? map.get(CALLING_STATION_ID).toString() : "N/A"));
                    r.setNasIdentifier(((map.get(NAS_IDENTIFIER) != null) ? map.get(NAS_IDENTIFIER).toString() : "N/A"));
                    r.setCalledStationId(((map.get(CALLED_STATION_ID) != null) ? map.get(CALLED_STATION_ID).toString() : "N/A"));
                    r.setNasIpAddress(((map.get(NAS_IP_ADDRESS) != null) ? map.get(NAS_IP_ADDRESS).toString() : "N/A"));
                    r.setFramedIpAddress(((map.get(FRAMED_IP_ADDRESS) != null) ? map.get(FRAMED_IP_ADDRESS).toString() : "N/A"));
                    r.setAcctStatusType(((map.get(ACCT_STATUS_TYPE) != null) ? map.get(ACCT_STATUS_TYPE).toString() : "N/A"));
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
