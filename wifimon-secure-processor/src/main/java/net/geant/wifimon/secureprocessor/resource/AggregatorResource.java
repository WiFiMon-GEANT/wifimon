package net.geant.wifimon.secureprocessor.resource;

import com.google.json.JsonSanitizer;
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
import net.geant.wifimon.subnet.SubnetUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.ElasticsearchException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
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
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import com.google.json.JsonSanitizer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;

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

    // Environment headers for ELK Stack, X-Pack security, encrypting sensitive information
    private static final String ES_HOST = "elasticsearch.host";
    private static final String ES_PORT = "elasticsearch.port";
    private static final String ES_INDEXNAME_MEASUREMENT = "elasticsearch.indexnamemeasurement";
    private static final String ES_INDEXNAME_RADIUS = "elasticsearch.indexnameradius";
    private static final String ES_INDEXNAME_PROBES = "elasticsearch.indexnameprobes";
    private static final String ES_INDEXNAME_DHCP = "elasticsearch.indexnamedhcp";
    private static final String SSL_ENABLED = "xpack.security.enabled";
    private static final String SSL_USER_USERNAME = "ssl.http.user.username";
    private static final String SSL_USER_PHRASE = "ssl.http.user.phrase";
    private static final String HMAC_SHA512_KEY = "sha.key";
    // JSON headers for WiFiMon crowdsourced/deterministic measurements (wifimon index)
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
    private static final String IP_TYPE = "IP-Type";
    private static final String TEST_SERVER_LOCATION = "TestServerLocation";
    // JSON headers for correlation with RADIUS logs (correlation of radiuslogs index with wifimon index)
    private static final String AP_BUILDING = "Ap-Building";
    private static final String AP_FLOOR = "Ap-Floor";
    private static final String AP_LOCATION = "Ap-Location";
    private static final String AP_NOTES = "Ap-Notes";
    private static final String RADIUS_TIMESTAMP = "RADIUS-Timestamp";
    private static final String DHCP_TIMESTAMP = "DHCP-Timestamp";
    private static final String SERVICE_TYPE = "Service-Type";
    private static final String NAS_PORT_ID = "NAS-Port-Id";
    private static final String NAS_PORT_TYPE = "NAS-Port-Type";
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
    // JSON headers for correlation with DHCP Logs (correlation of dhcplogs index with wifimon index)
    private static final String IP_ADDRESS = "IP-Address";
    private static final String IP_ADDRESS_KEYWORD = "IP-Address.keyword";
    private static final String MAC_ADDRESS = "MAC-Address";
    // JSON headers for WiFiMon Hardware Probes (WiFiMon Side, JSON that is stored in ELK cluster)
    private static final String PROBE_ACCESSPOINT = "Accesspoint";
    private static final String PROBE_ESSID = "Essid";
    private static final String PROBE_BIT_RATE = "Bit-Rate";
    private static final String PROBE_TX_POWER = "Tx-Power";
    private static final String PROBE_LINK_QUALITY = "Link-Quality";
    private static final String PROBE_SIGNAL_LEVEL = "Signal-Level";
    private static final String PROBE_MONITOR = "Monitor";
    private static final String PROBE_LOCATION_NAME = "Location-Name";
    private static final String PROBE_TEST_DEVICE_LOCATION_DESCRIPTION = "Test-Device-Location-Description";
    private static final String PROBE_NAT_NETWORK = "NAT-Network";
    // JSON headers for WiFiMon Hardware Probes (Streaming to JSON Listener)
    private static final String JSON_COLLECT = "json.collect";
    private static final String JSON_COLLECTOR = "json.collector";
    private static final String EXPORTER_PERFORMANCE_MEASUREMENTS = "performance";
    private static final String EXPORTER_ORIGIN = "reportOrigin";
    private static final String EXPORTER_CONNECTIVITY = "connectivity";
    private static final String EXPORTER_LATITUDE = "latitude";
    private static final String EXPORTER_LONGITUDE = "longitude";
    private static final String EXPORTER_LOCATION_METHOD = "locationMethod";
    private static final String EXPORTER_ENVIRONMENT = "environment";
    private static final String EXPORTER_DEVICE_TYPE = "testDeviceType";
    private static final String EXPORTER_DEVICE_VERSION = "testDeviceVersion";
    private static final String EXPORTER_DEVICE_ID = "testDeviceId";
    private static final String EXPORTER_TEST_INSTANCE_UNIQUE_ID = "testInstanceUniqueId";
    private static final String EXPORTER_START_TIMESTAMP = "testStartTimestamp";
    private static final String EXPORTER_FINISHED_TIMESTAMP = "testFinishedTimestamp";
    private static final String EXPORTER_DOWNLOAD_THROUGHPUT = "downloadThroughput";
    private static final String EXPORTER_UPLOAD_THROUGHPUT = "uploadThroughput";
    private static final String EXPORTER_IPV4_LOCAL_PING = "ipv4LocalPing";
    private static final String EXPORTER_IPV6_LOCAL_PING = "ipv6LocalPing";
    private static final String EXPORTER_TEST_TOOL = "testTool";
    private static final String EXPORTER_TEST_TIMESTAMP = "testTimestamp";
    private static final String EXPORTER_WIFI_SURROUND = "wifiSurround";
    private static final String EXPORTER_LOCATION_NAME = "locationName";
    private static final String EXPORTER_TEST_DEVICE_LOCATION_DESCRIPTION = "testDeviceLocationDescription";
    private static final String EXPORTER_NAT_NETWORK = "NAT";

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
        List<Subnet> v4Subnets = new ArrayList<>();
        List<Subnet> v6Subnets = new ArrayList<>();
        List<Subnet> subnets = subnetRepository.findAll();
        if (subnets.isEmpty()) return Response.ok(false).build();
        List<SubnetUtils.SubnetInfo> s = getSubnetInfos(ip, v4Subnets, v6Subnets, subnets);
        for (SubnetUtils.SubnetInfo si : s) {
            if (si.isInRange(ip)) return Response.ok(true).build();
        }
        return Response.ok(false).build();
    }

    private String dataValidator(String item, String label, boolean numericValue, boolean finalString, boolean sanitize) {
            String endingString = "\"";
            String endingNumeric = "";
            if (finalString == false) {
                    endingString = "\", ";
                    endingNumeric = ", ";
            }
            String sanitizedItemJson = "";
            String sanitizedItem = item;
            if (sanitize == true) {
                sanitizedItem = JsonSanitizer.sanitize(item);
                sanitizedItem = sanitizedItem.replace("\"", "");
            }

            if (numericValue == true) {
                sanitizedItemJson = sanitizedItem != null ? "\"" + label + "\" : " + sanitizedItem + endingNumeric : "";
            } else {
                sanitizedItemJson = sanitizedItem != null ? "\"" + label + "\" : \"" + sanitizedItem + endingString : "";
            }
            return sanitizedItemJson;
    }

    @POST
    @Path("/probes")
    public Response correlate(final ProbesMeasurement measurement, @Context HttpServletRequest request) {
	Response response = null;
	String probeNumber = "", timestamp = "";

        try {
            // Get Wireless Network Performance Metrics
	    String timestampCurrent = String.valueOf(System.currentTimeMillis());
            String timestampJson = timestampCurrent != null ? "\"" + TIMESTAMP + "\" : " + timestampCurrent + ", " : "";
	    String macAddressJson = dataValidator(measurement.getMacAddress(), MAC_ADDRESS, false, false, true);
	    String accesspointJson = dataValidator(measurement.getAccesspoint(), PROBE_ACCESSPOINT, false, false, true);
	    String essidJson = dataValidator(measurement.getEssid(), PROBE_ESSID, false, false, true);
	    String bitRateJson = dataValidator(measurement.getBitRate().toString(), PROBE_BIT_RATE, true, false, true);
	    String txPowerJson = dataValidator(measurement.getTxPower().toString(), PROBE_TX_POWER, true, false, true);
	    String linkQualityJson = dataValidator(measurement.getLinkQuality().toString(), PROBE_LINK_QUALITY, true, false, true);
	    String signalLevelJson = dataValidator(measurement.getSignalLevel().toString(), PROBE_SIGNAL_LEVEL, true, false, true);
	    probeNumber = measurement.getProbeNo().toString();
	    String probeNoJson = dataValidator(measurement.getProbeNo().toString(), PROBE_NUMBER, true, false, true);
	    String originJson = "\"" + ORIGIN + "\": \"Probe\", "; 
	    String locationNameJson = dataValidator(measurement.getLocationName(), PROBE_LOCATION_NAME, false, false, true);
	    String testDeviceLocationDescriptionJson = dataValidator(measurement.getTestDeviceLocationDescription(), PROBE_TEST_DEVICE_LOCATION_DESCRIPTION, false, false, true);
	    String natNetworkJson = dataValidator(measurement.getNat(), PROBE_NAT_NETWORK, false, false, true);
	    String monitorJson = dataValidator(measurement.getMonitor(), PROBE_MONITOR, false, true, true);

            // Construct JSON object that will be stored in Elasticsearch
            String jsonStringDraft = "{" +
                    timestampJson + macAddressJson + accesspointJson +essidJson + bitRateJson + 
		    txPowerJson + linkQualityJson + signalLevelJson + probeNoJson + originJson +
		    locationNameJson + testDeviceLocationDescriptionJson + natNetworkJson + 
		    monitorJson + "}";

            String jsonString = jsonStringDraft.replace("\", }", "\"}");

            final SearchSourceBuilder builderForTimestamp = new SearchSourceBuilder()
                    .query(QueryBuilders.matchAllQuery())
                    .sort(new FieldSortBuilder(TIMESTAMP).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{TIMESTAMP}, null)
                    .postFilter(QueryBuilders.termQuery(PROBE_NUMBER, probeNumber))
                    .size(1)
                    .explain(true);

            final SearchRequest requestForTimestamp = new SearchRequest(environment.getProperty(ES_INDEXNAME_PROBES)).source(builderForTimestamp);
            final SearchResponse responseForTimestamp = AggregatorResource.restHighLevelClient.search(requestForTimestamp, RequestOptions.DEFAULT);
            final SearchHits hitsForTimestamp = responseForTimestamp.getHits();
            if (responseForTimestamp.getHits().getTotalHits().value > 0) {
                    SearchHit hitForTimestamp = hitsForTimestamp.getAt(0);
                    Map mapForTimestamp = hitForTimestamp.getSourceAsMap();
                    timestamp = (mapForTimestamp.get(TIMESTAMP) != null) ? mapForTimestamp.get(TIMESTAMP).toString() : "N/A";
            }

            // Store measurements in elasticsearch
            indexMeasurementProbes(jsonString);

	    if (environment.getProperty(JSON_COLLECT).equals("true")) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC+0"));

                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                sourceBuilder.query(QueryBuilders.rangeQuery("Timestamp").from(timestamp).to("now"));
                sourceBuilder.postFilter(QueryBuilders.termQuery(PROBE_NUMBER,probeNumber));
                SearchRequest searchRequest = new SearchRequest(environment.getProperty(ES_INDEXNAME_MEASUREMENT));
                searchRequest.source(sourceBuilder);

                SearchResponse searchResponse = AggregatorResource.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                final SearchHits hits = searchResponse.getHits();

                int sequence = 1;
                Iterator<SearchHit> iterator = hits.iterator();
                Map<String, SearchHit> distinctObjects = new HashMap<String,SearchHit>();

                String probeDownloadThroughput = "", probeUploadThroughput = "", probeIpv4Ping = "", probeIpv6Ping = "", probeTestTool = "", probeTimestamp = "";

                while (iterator.hasNext()) {
                        if (sequence > 1) {
                                probeDownloadThroughput += ", ";
                                probeUploadThroughput += ", ";
                                probeIpv4Ping += ", ";
                                probeIpv6Ping += ", ";
                                probeTestTool += ", ";
                                probeTimestamp += ", ";
                        }
                        SearchHit searchHit = (SearchHit) iterator.next();
                        Map<String, Object> source = searchHit.getSourceAsMap();
                        if (source.get(DOWNLOAD_THROUGHPUT) != null) {
                                probeDownloadThroughput += source.get(DOWNLOAD_THROUGHPUT).toString();
                        }
                        if (source.get(UPLOAD_THROUGHPUT) != null) {
                                probeUploadThroughput += source.get(UPLOAD_THROUGHPUT).toString();
                        }
                        if (source.get(LOCAL_PING) != null) {
                                probeIpv4Ping += source.get(LOCAL_PING).toString();
                        }
                        if (source.get(TIMESTAMP) != null) {
				long tempTimestamp = Long.parseLong(source.get(TIMESTAMP).toString());
				Date dateTempTimestamp = new java.util.Date(tempTimestamp * 1L);
				String tempTimestampFormatted = sdf.format(dateTempTimestamp);
                                probeTimestamp += tempTimestampFormatted.toString();
                        }
                        if (source.get(TEST_TOOL) != null) {
                                probeTestTool += source.get(TEST_TOOL).toString();
                        }
                        probeIpv6Ping += "-1";
                        sequence += 1;
                }

		// format the previous measurement timestamp to the desired form
		long startTimestampLong = Long.parseLong(timestamp);
		Date dateStartTimestampLong = new java.util.Date(startTimestampLong * 1L);
		String startTimestampFormatted = sdf.format(dateStartTimestampLong);

		// format the current measurement timestamp to the desired form
		long currentTimestampLong = Long.parseLong(timestampCurrent);
		Date dateCurrentTimestampLong = new java.util.Date(currentTimestampLong * 1L);
		String currentTimestampFormatted = sdf.format(dateCurrentTimestampLong);

		// Generate a unique UUID for this measurement
		UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString();

                String overallJson = "";
		// Stream report origin part of JSON specification
                String reportOriginJson = "\"" + EXPORTER_ORIGIN + "\": {";
		reportOriginJson += dataValidator("WiFiMon Hardware Probe", EXPORTER_DEVICE_TYPE, false, false, false);
		reportOriginJson += dataValidator("4.0", EXPORTER_DEVICE_VERSION, false, false, false);
		reportOriginJson += dataValidator(measurement.getAccesspoint(), EXPORTER_DEVICE_ID, false, false, true);
		reportOriginJson += dataValidator(uuidAsString, EXPORTER_TEST_INSTANCE_UNIQUE_ID, false, false, false);
		reportOriginJson += dataValidator(startTimestampFormatted, EXPORTER_START_TIMESTAMP, false, false, false);
		reportOriginJson += dataValidator(currentTimestampFormatted, EXPORTER_FINISHED_TIMESTAMP, false, true, false);
		reportOriginJson += "}";

                // Stream environment part of JSON specification
                String environmentJson = "\"" + EXPORTER_ENVIRONMENT + "\": {";
		environmentJson += dataValidator("0", EXPORTER_LONGITUDE, true, false, false);
		environmentJson += dataValidator("0", EXPORTER_LATITUDE, true, false, false);
		environmentJson += dataValidator(measurement.getLocationName(), EXPORTER_LOCATION_NAME, false, false, true);
		environmentJson += dataValidator(measurement.getTestDeviceLocationDescription(), EXPORTER_TEST_DEVICE_LOCATION_DESCRIPTION, false, false, true);
		environmentJson += dataValidator("Geolocation", EXPORTER_LOCATION_METHOD, false, false, false);
		environmentJson += measurement.getMonitor() != null ? "\"" + PROBE_MONITOR + "\" : " + measurement.getMonitor() : "";
                environmentJson += "}";

                // Stream connectivity part of JSON specification
                String connectivityJson = "\"" + EXPORTER_CONNECTIVITY + "\": {";
		connectivityJson += dataValidator(measurement.getNat(), EXPORTER_NAT_NETWORK, false, true, true);
                connectivityJson += "}";

                // Stream performance part of JSON specification
                String performanceMeasurementsJson = "\"" + EXPORTER_PERFORMANCE_MEASUREMENTS + "\": { ";
		performanceMeasurementsJson += dataValidator(probeDownloadThroughput.toString(), EXPORTER_DOWNLOAD_THROUGHPUT, false, false, false);
		performanceMeasurementsJson += dataValidator(probeUploadThroughput.toString(), EXPORTER_UPLOAD_THROUGHPUT, false, false, false);
		performanceMeasurementsJson += dataValidator(probeIpv4Ping.toString(), EXPORTER_IPV4_LOCAL_PING, false, false, false);
		performanceMeasurementsJson += dataValidator(probeIpv6Ping.toString(), EXPORTER_IPV6_LOCAL_PING, false, false, false);
		performanceMeasurementsJson += dataValidator(probeTimestamp.toString(), EXPORTER_TEST_TIMESTAMP, false, false, false);
		performanceMeasurementsJson += dataValidator(probeTestTool.toString(), EXPORTER_TEST_TOOL, false, true, false);
                performanceMeasurementsJson += "}";

                overallJson = "{ " + reportOriginJson + ", " + environmentJson + ", " + connectivityJson + ", " + performanceMeasurementsJson + "}";

                String toPost = "type=v100&message=" + overallJson;

                String[] commands = new String[] {"curl", "--data-urlencode", toPost, environment.getProperty(JSON_COLLECTOR)};
                Process process = Runtime.getRuntime().exec(commands);
                //process.destroy();

            return Response.ok().build();
	    }

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
        List<Subnet> v4Subnets = new ArrayList<>();
        List<Subnet> v6Subnets = new ArrayList<>();
        List<Subnet> subnets = subnetRepository.findAll();
        if (subnets == null || subnets.isEmpty()) return Response.ok(false).build();
        List<SubnetUtils.SubnetInfo> s = getSubnetInfos(ip, v4Subnets, v6Subnets, subnets);

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
        InetAddressValidator inetAddressValidator = new InetAddressValidator();
        String encryptedIP = "";
        String ipType = "";
        try {
            ipType = inetAddressValidator.isValidInet4Address(ip) ? "IPv4" : "IPv6";
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

	RadiusStripped r = null;
	Accesspoint ap = null;

        // Perform correlations and insert new measurements in the elasticsearch cluster
        if (correlationmethod.equals(CorrelationMethod.DHCP_AND_RADIUS.toString())) {
            String callingStationIdTemp = retrieveLastMacEntryByIp(encryptedIP);
	    if (!callingStationIdTemp.equals("")) {
            	r = retrieveLastRadiusEntryByMac(callingStationIdTemp);
	    }
        } else {
            r = retrieveLastRadiusEntryByIp(encryptedIP);
        }

        try {
            if (r != null) {
                // There are RADIUS Logs corresponding to the received measurement
                //String calledStationIdTemp = r.getCalledStationId().substring(0, 30).toUpperCase().replace("-", ":");
                String calledStationIdTemp = r.getCalledStationId();
                ap = accesspointsRepository.find(calledStationIdTemp);
                response = addElasticMeasurement(joinMeasurement(measurement, r, ap, agent), requesterSubnet, encryptedIP, ipType);
            } else {
                // There are not RADIUS Logs corresponding to the received measurement
                response = addElasticMeasurement(joinMeasurement(measurement, r, null, agent), requesterSubnet, encryptedIP, ipType);
            }
        } catch (Exception e) {
	    logger.info(e.toString());
            response = null;
        }

        return response;
    }

    private List<SubnetUtils.SubnetInfo> getSubnetInfos(String ip, List<Subnet> v4Subnets, List<Subnet> v6Subnets, List<Subnet> subnets) {
        List<SubnetUtils.SubnetInfo> s = new ArrayList<>();
        InetAddressValidator inetAddressValidator = new InetAddressValidator();
        for (Subnet si : subnets) {
            String subnet = si.getSubnet();
            if(subnet.contains(".")) {
                v4Subnets.add(si);
            }
            else if(subnet.contains(":")) {
                v6Subnets.add(si);
            }
        }

        if(inetAddressValidator.isValidInet4Address(ip)) {
            s = v4Subnets.stream().
                    map(it -> it.fromSubnetString()).collect(Collectors.toList());
        }
        else if(inetAddressValidator.isValidInet6Address(ip)) {
            s = v6Subnets.stream().
                    map(it -> it.fromSubnetString()).collect(Collectors.toList());
        }
        return s;
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
	m.setTestServerLocation(measurement.getTestServerLocation());
        m.setUserAgent(agent);
        m.setTestTool(measurement.getTestTool());
        m.setRadiusTimestamp(radius != null ? radius.getRadiusTimestamp() : null);
        m.setServiceType(radius != null ? radius.getServiceType() : null);
        m.setNasPortId(radius != null ? radius.getNasPortId() : null);
        m.setNasPortType(radius != null ? radius.getNasPortType() : null);
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

    private Response addElasticMeasurement(AggregatedMeasurement measurement, String requesterSubnet, String encryptedIP, String ipType) {
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
	String downloadThroughputJson = dataValidator(measurement.getDownloadThroughput().toString(), DOWNLOAD_THROUGHPUT, true, false, true);
	String uploadThroughputJson = dataValidator(measurement.getUploadThroughput().toString(), UPLOAD_THROUGHPUT, true, false, true);
	String localPingJson = dataValidator(measurement.getLocalPing().toString(), LOCAL_PING, true, false, true);
	String locationJson = JsonSanitizer.sanitize(measurement.getLatitude().toString()) != null ? "\"" + LOCATION + "\" : \"" + JsonSanitizer.sanitize(measurement.getLatitude().toString()) + "," + JsonSanitizer.sanitize(measurement.getLongitude().toString()) + "\", " : "";
	String locationMethodJson = dataValidator(measurement.getLocationMethod(), LOCATION_METHOD, false, false, true);
	String testServerLocationJson = dataValidator("\"" + measurement.getTestServerLocation() + "\"", TEST_SERVER_LOCATION, false, false, true);
	String clientIpJson = dataValidator(measurement.getClientIp(), CLIENT_IP, false, false, true);
	String userAgentJson = dataValidator(measurement.getUserAgent(), USER_AGENT, false, false, true);
	String userBrowserJson = dataValidator(userBrowser, USER_BROWSER, false, false, false);
	String userOsJson = dataValidator(userOs, USER_OS, false, false, false);
	String testToolJson = dataValidator(measurement.getTestTool(), TEST_TOOL, false, false, true);
	String radiusTimestampJson = dataValidator(measurement.getRadiusTimestamp(), RADIUS_TIMESTAMP, false, false, true);
        String serviceTypeJson = dataValidator(measurement.getServiceType(), SERVICE_TYPE, false, false, true);
        String nasPortIdJson = dataValidator(measurement.getNasPortId(), NAS_PORT_ID, false, false, true);
        String nasPortTypeJson = dataValidator(measurement.getNasPortType(), NAS_PORT_TYPE, false, false, true);
        String acctSessionIdJson = dataValidator(measurement.getAcctSessionId(), ACCT_SESSION_ID, false, false, true);
        String acctMultiSessionIdJson = dataValidator(measurement.getAcctMultiSessionId(), ACCT_MULTI_SESSION_ID, false, false, true);
        String callingStationIdJson = dataValidator(measurement.getCallingStationId(), CALLING_STATION_ID, false, false, true);
        String calledStationIdJson = dataValidator(measurement.getCalledStationId(), CALLED_STATION_ID, false, false, true);
        String acctAuthenticJson = dataValidator(measurement.getAcctAuthentic(), ACCT_AUTHENTIC, false, false, true);
        String acctStatusTypeJson = dataValidator(measurement.getAcctStatusType(), ACCT_STATUS_TYPE, false, false, true);
        String nasIdentifierJson = dataValidator(measurement.getNasIdentifier(), NAS_IDENTIFIER, false, false, true);
        String acctDelayTimeJson = dataValidator(measurement.getAcctDelayTime(), ACCT_DELAY_TIME, false, false, true);
        String nasIpAddressJson = dataValidator("\"" + measurement.getNasIpAddress() + "\"", NAS_IP_ADDRESS, false, false, true);
        String framedIpAddressJson = dataValidator(measurement.getFramedIpAddress(), FRAMED_IP_ADDRESS, false, false, true);
        String acctUniqueSessionIdJson = dataValidator(measurement.getAcctUniqueSessionId(), ACCT_UNIQUE_SESSION_ID, false, false, true);
        String realmJson = dataValidator(measurement.getRealm(), REALM, false, false, true);
        String measurementOriginJson = dataValidator(measurementOrigin, ORIGIN, false, false, false);
        String probeNumberJson = dataValidator(probeNumber, PROBE_NUMBER, false, false, false);
        String requesterSubnetJson = dataValidator(requesterSubnet, REQUESTER_SUBNET, false, false, false);
        String encryptedIPJson = dataValidator(encryptedIP, ENCRYPTED_IP, false, false, false);
        String ipTypeJson = dataValidator(ipType, IP_TYPE, false, false, false);
        String apBuildingJson = dataValidator(measurement.getApBuilding(), AP_BUILDING, false, false, true);
        String apFloorJson = dataValidator(measurement.getApFloor(), AP_FLOOR, false, false, true);
        String apLocationJson = "";
        try {
                apLocationJson = JsonSanitizer.sanitize(measurement.getApLatitude().toString()) != null ? "\"" + AP_LOCATION + "\" : \"" + JsonSanitizer.sanitize(measurement.getApLatitude().toString()) + "," + JsonSanitizer.sanitize(measurement.getApLongitude().toString()) + "\", " : "";
        } catch(Exception e) {
                logger.info(e.toString());
        }
        String apNotesJson = dataValidator(measurement.getApNotes(), AP_NOTES, false, true, true);

        // Build the Json String to store in the elasticsearch cluster
        String jsonStringDraft = "{" +
                "\"" + TIMESTAMP + "\" : " + measurement.getTimestamp() + ", " +
                downloadThroughputJson + uploadThroughputJson + localPingJson +
                locationJson + locationMethodJson + testServerLocationJson + userAgentJson + userBrowserJson + 
		userOsJson + testToolJson + radiusTimestampJson + serviceTypeJson +
 		nasPortIdJson + nasPortTypeJson + acctSessionIdJson +
		acctMultiSessionIdJson + callingStationIdJson + calledStationIdJson +
		acctAuthenticJson + acctStatusTypeJson + nasIdentifierJson +
		acctDelayTimeJson + nasIpAddressJson + framedIpAddressJson +
		acctUniqueSessionIdJson + realmJson + clientIpJson +
                measurementOriginJson + probeNumberJson + requesterSubnetJson + 
		encryptedIPJson + apBuildingJson + apFloorJson + apLocationJson + ipTypeJson +
                apNotesJson + "}";

        String jsonString = jsonStringDraft.replace("\", }", "\"}");
        // Store measurements in elasticsearch
        indexMeasurement(jsonString);

        return Response.ok().build();
    }

    private String retrieveLastMacEntryByIp(String ip) {
        String macAddress = "";
        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
            .query(QueryBuilders.matchAllQuery())
            .sort(new FieldSortBuilder(DHCP_TIMESTAMP).order(SortOrder.DESC))
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
                    .sort(new FieldSortBuilder(RADIUS_TIMESTAMP).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{RADIUS_TIMESTAMP, SERVICE_TYPE, NAS_PORT_ID,
                            NAS_PORT_TYPE, ACCT_SESSION_ID, ACCT_MULTI_SESSION_ID,
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
                    .sort(new FieldSortBuilder(RADIUS_TIMESTAMP).order(SortOrder.DESC))
                    .from(0)
                    .fetchSource(new String[]{RADIUS_TIMESTAMP, SERVICE_TYPE, NAS_PORT_ID,
			    NAS_PORT_TYPE, ACCT_SESSION_ID, ACCT_MULTI_SESSION_ID,
			    CALLING_STATION_ID, CALLED_STATION_ID, ACCT_AUTHENTIC, 
			    ACCT_STATUS_TYPE, NAS_IDENTIFIER, ACCT_DELAY_TIME, NAS_IP_ADDRESS,
			    FRAMED_IP_ADDRESS, ACCT_UNIQUE_SESSION_ID, REALM}, null)
                    .postFilter(QueryBuilders.termQuery(CALLING_STATION_ID_KEYWORD, mac))
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
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            environment.getProperty(SSL_USER_USERNAME),
                            environment.getProperty(SSL_USER_PHRASE)));

            restClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(
                            environment.getProperty(ES_HOST),
                            Integer.parseInt(environment.getProperty(ES_PORT)),
                            "https"))
                            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
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
