package net.geant.wifimon.secureprocessor.resource;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.google.json.JsonSanitizer;
import net.geant.wifimon.model.dto.AggregatedMeasurement;
import net.geant.wifimon.model.dto.NetTestMeasurement;
import net.geant.wifimon.model.dto.ProbesMeasurement;
import net.geant.wifimon.model.dto.TwampMeasurement;
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
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.elasticsearch._types.SortOrder;

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
    private static final String ES_INDEXNAME_TWAMP = "elasticsearch.indexnametwamp";
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
    private static final String JITTER_MSEC = "Jitter-Msec";
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
    // JSON headers for WiFiMon Hardware Probes (ping metrics)
    private static final String PROBE_WTS = "Wts";
    private static final String PROBE_PING_PACKET_TRANSMIT = "Ping-Packet-Transmit";
    private static final String PROBE_PING_PACKET_RECEIVE = "Ping-Packet-Receive";
    private static final String PROBE_PING_PACKET_LOSS_RATE = "Ping-Packet-Loss-Rate";
    private static final String PROBE_PING_PACKET_LOSS_COUNT = "Ping-Packet-Loss-Count";
    private static final String PROBE_PING_RTT_MIN = "Ping-Rtt-Min";
    private static final String PROBE_PING_RTT_AVG = "Ping-Rtt-Avg";
    private static final String PROBE_PING_RTT_MAX = "Ping-Rtt-Max";
    private static final String PROBE_PING_RTT_MDEV = "Ping-Rtt-Mdev";
    private static final String PROBE_PING_PACKET_DUPLICATE_RATE = "Ping-Packet-Duplicate-Rate";
    private static final String PROBE_PING_PACKET_DUPLICATE_COUNT = "Ping-Packet-Duplicate-Count";
    // JSON headers for WiFiMon Hardware Probes (WiFiMon Side, JSON that is stored in ELK cluster)
    private static final String PROBE_ACCESSPOINT = "Accesspoint";
    private static final String PROBE_ESSID = "Essid";
    private static final String PROBE_BIT_RATE = "Bit-Rate";
    private static final String PROBE_TX_POWER = "Tx-Power";
    private static final String PROBE_LINK_QUALITY = "Link-Quality";
    private static final String PROBE_SIGNAL_LEVEL = "Signal-Level";
    private static final String PROBE_MONITOR = "Monitor";
    private static final String PROBE_SYSTEM = "System";
    private static final String PROBE_LOCATION_NAME = "Location-Name";
    private static final String PROBE_TEST_DEVICE_LOCATION_DESCRIPTION = "Test-Device-Location-Description";
    private static final String NUMBER_OF_USERS = "Number-Of-Users";
    private static final String PROBE_NAT_NETWORK = "NAT-Network";
    private static final String PROBE_ENCRYPTION = "Encryption-Type";
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
    // JSON headers for TWAMP measurements from WiFiMon Hardware Probes
    private static final String TWAMP_TIMESTAMP = "Timestamp";
    private static final String TWAMP_PROBE_NUMBER = "Probe-Number";
    private static final String TWAMP_SERVER = "Twamp-Server";
    private static final String TWAMP_SENT = "Sent";
    private static final String TWAMP_LOST = "Lost";
    private static final String TWAMP_SEND_DUPS = "Send-Dups";
    private static final String TWAMP_REFLECT_DUPS = "Reflect-Dups";
    private static final String TWAMP_MIN_RTT = "Min-Rtt";
    private static final String TWAMP_MEDIAN_RTT = "Median-Rtt";
    private static final String TWAMP_MAX_RTT = "Max-Rtt";
    private static final String TWAMP_ERR_RTT = "Err-Rtt";
    private static final String TWAMP_MIN_SEND = "Min-Send";
    private static final String TWAMP_MEDIAN_SEND = "Median-Send";
    private static final String TWAMP_MAX_SEND = "Max-Send";
    private static final String TWAMP_ERR_SEND = "Err-Send";
    private static final String TWAMP_MIN_REFLECT = "Min-Reflect";
    private static final String TWAMP_MEDIAN_REFLECT = "Median-Reflect";
    private static final String TWAMP_MAX_REFLECT = "Max-Reflect";
    private static final String TWAMP_ERR_REFLECT = "Err-Reflect";
    private static final String TWAMP_MIN_REFLECTOR_PROCESSING_TIME = "Min-Reflector-Processing-Time";
    private static final String TWAMP_MAX_REFLECTOR_PROCESSING_TIME = "Max-Reflector-Processing-Time";
    private static final String TWAMP_TWO_WAY_JITTER_VALUE = "Two-Way-Jitter-Value";
    private static final String TWAMP_TWO_WAY_JITTER_CHAR = "Two-Way-Jitter-Char";
    private static final String TWAMP_SEND_JITTER_VALUE = "Send-Jitter-Value";
    private static final String TWAMP_SEND_JITTER_CHAR = "Send-Jitter-Char";
    private static final String TWAMP_REFLECT_JITTER_VALUE = "Reflect-Jitter-Value";
    private static final String TWAMP_REFLECT_JITTER_CHAR = "Reflect-Jitter-Char";
    private static final String TWAMP_SEND_HOPS_VALUE = "Send-Hops-Value";
    private static final String TWAMP_SEND_HOPS_CHAR = "Send-Hops-Char";
    private static final String TWAMP_REFLECT_HOPS_VALUE = "Reflect-Hops-Value";
    private static final String TWAMP_REFLECT_HOPS_CHAR = "Reflect-Hops-Char";
    // JSON headers for NTP data from WiFiMon Hardware Probes
    private static final String NTP_SERVER_NTPSTAT = "Ntp-Server-Ntpstat";
    private static final String STRATUM = "Stratum";
    private static final String TIME_CORRECT = "Time-Correct";
    private static final String NTP_SERVER_NTPQ = "Ntp-Server-Ntpq";
    private static final String DELAY_NTPQ = "Delay-Ntpq";
    private static final String OFFSET_NTPQ = "Offset-Ntpq";
    private static final String JITTER_NTPQ = "Jitter-Ntpq";

    private static Logger logger = Logger.getLogger(AggregatorResource.class.getName());
    private static RestClient restClient;
    private static ElasticsearchTransport transport;
    private static ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void init() {
	    if (environment.getProperty(SSL_ENABLED).equals("true")) {
            AggregatorResource.restClient = initKeystoreClient();
            } else {
            AggregatorResource.restClient = initHttpClient();
            }
	    AggregatorResource.transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
	    AggregatorResource.elasticsearchClient = new ElasticsearchClient(transport);
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
	    String wtsJson = dataValidator(measurement.getWts(), PROBE_WTS, false, false, true);
	    String pingPacketTransmitJson = dataValidator(measurement.getPingPacketTransmit().toString(), PROBE_PING_PACKET_TRANSMIT, true, false, true);
	    String pingPacketReceiveJson = dataValidator(measurement.getPingPacketReceive().toString(), PROBE_PING_PACKET_RECEIVE, true, false, true);
	    String pingPacketLossRateJson = dataValidator(measurement.getPingPacketLossRate().toString(), PROBE_PING_PACKET_LOSS_RATE, true, false, true);
	    String pingPacketLossCountJson = dataValidator(measurement.getPingPacketLossCount().toString(), PROBE_PING_PACKET_LOSS_COUNT, true, false, true);
	    String pingRttMinJson = dataValidator(measurement.getPingRttMin().toString(), PROBE_PING_RTT_MIN, true, false, true);
	    String pingRttAvgJson = dataValidator(measurement.getPingRttAvg().toString(), PROBE_PING_RTT_AVG, true, false, true);
	    String pingRttMaxJson = dataValidator(measurement.getPingRttMax().toString(), PROBE_PING_RTT_MAX, true, false, true);
	    String pingRttMdevJson = dataValidator(measurement.getPingRttMdev().toString(), PROBE_PING_RTT_MDEV, true, false, true);
	    String pingPacketDuplicateRateJson = dataValidator(measurement.getPingPacketDuplicateRate().toString(), PROBE_PING_PACKET_DUPLICATE_RATE, true, false, true);
	    String pingPacketDuplicateCountJson = dataValidator(measurement.getPingPacketDuplicateCount().toString(), PROBE_PING_PACKET_DUPLICATE_COUNT, true, false, true);
	    String macAddressJson = dataValidator(measurement.getMacAddress(), MAC_ADDRESS, false, false, true);
	    String accesspointJson = dataValidator(measurement.getAccesspoint(), PROBE_ACCESSPOINT, false, false, true);
	    String essidJson = dataValidator(measurement.getEssid(), PROBE_ESSID, false, false, true);
	    String bitRateJson = dataValidator(measurement.getBitRate().toString(), PROBE_BIT_RATE, true, false, true);
	    String txPowerJson = dataValidator(measurement.getTxPower().toString(), PROBE_TX_POWER, true, false, true);
	    String linkQualityJson = dataValidator(measurement.getLinkQuality().toString(), PROBE_LINK_QUALITY, true, false, true);
	    String signalLevelJson = dataValidator(measurement.getSignalLevel().toString(), PROBE_SIGNAL_LEVEL, true, false, true);
	    probeNumber = measurement.getProbeNo();
	    String probeNoJson = dataValidator(probeNumber, PROBE_NUMBER, false, false, true);
	    String originJson = "\"" + ORIGIN + "\": \"Probe\", ";
	    String locationNameJson = dataValidator(measurement.getLocationName(), PROBE_LOCATION_NAME, false, false, true);
	    String testDeviceLocationDescriptionJson = dataValidator(measurement.getTestDeviceLocationDescription(), PROBE_TEST_DEVICE_LOCATION_DESCRIPTION, false, false, true);
	    String numberOfUsers = dataValidator(measurement.getNumberOfUsers(), NUMBER_OF_USERS, true, false, true);
	    String natNetworkJson = dataValidator(measurement.getNat(), PROBE_NAT_NETWORK, false, false, true);
	    String encTypeJson = dataValidator(measurement.getEncType(), PROBE_ENCRYPTION, false, false, true);
	    String monitorJson = dataValidator(measurement.getMonitor(), PROBE_MONITOR, false, false, true);
	    String systemJson = dataValidator(measurement.getSystem(), PROBE_SYSTEM, false, true, true);

            // Construct JSON object that will be stored in Elasticsearch
            String jsonStringDraft = "{" +
                    timestampJson + wtsJson + pingPacketTransmitJson + pingPacketReceiveJson +
		    pingPacketLossRateJson + pingPacketLossCountJson + pingRttMinJson +
		    pingRttAvgJson + pingRttMaxJson + pingRttMdevJson + pingPacketDuplicateRateJson +
		    pingPacketDuplicateCountJson + macAddressJson + accesspointJson + essidJson + 
		    bitRateJson + txPowerJson + linkQualityJson + signalLevelJson + probeNoJson + 
		    originJson + locationNameJson + testDeviceLocationDescriptionJson +
		    numberOfUsers + natNetworkJson + encTypeJson + monitorJson + systemJson + "}";

            String jsonString = jsonStringDraft.replace("\", }", "\"}");

            String finalProbeNumber = probeNumber;
            SearchResponse<Map> responseForTimestamp = AggregatorResource.elasticsearchClient.search(s -> s
                            .index(environment.getProperty(ES_INDEXNAME_PROBES))
                            .sort(so -> so.field(f -> f.field(TIMESTAMP).order(SortOrder.Desc)))
                            .from(0)
                            .postFilter(new Query.Builder()
                                    .term(t -> t
                                            .field(PROBE_NUMBER)
                                            .value(finalProbeNumber)
                                    ).build()
                            )
                            .size(1)
                            .explain(true),
                    Map.class
            );
            final List<Hit<Map>> hitsForTimestamp = responseForTimestamp.hits().hits();
            if (responseForTimestamp.hits().total().value() > 0) {
                Hit<Map> hitForTimestamp = hitsForTimestamp.get(0);
                Map mapForTimestamp = hitForTimestamp.source();
                timestamp = (mapForTimestamp.get(TIMESTAMP) != null) ? mapForTimestamp.get(TIMESTAMP).toString() : "N/A";
            }

            // Store measurements in elasticsearch
            indexMeasurementProbes(jsonString);

	    if (environment.getProperty(JSON_COLLECT).equals("true")) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC+0"));

		String finalTimestamp = timestamp;
		SearchResponse<Map> searchResponse = AggregatorResource.elasticsearchClient.search(s -> s
                        .index(environment.getProperty(ES_INDEXNAME_MEASUREMENT))
                        .query(new Query.Builder()
                                .range(r -> r
                                        .field("Timestamp")
                                        .from(finalTimestamp)
                                        .to("now")
                                )
                                .build())
                        .postFilter(new Query.Builder()
                                .term(t -> t
                                        .field(PROBE_NUMBER)
                                        .value(finalProbeNumber)
                                ).build()),
                Map.class);
		final List<Hit<Map>> hits = searchResponse.hits().hits();
                int sequence = 1;
                Iterator<Hit<Map>> iterator = hits.iterator();
                Map<String, Hit<Map>> distinctObjects = new HashMap<String,Hit<Map>>();

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
                        Hit<Map> searchHit = iterator.next();
                        Map source = searchHit.source();
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
    @Path("/twamp")
    public Response correlate(final TwampMeasurement measurement, @Context HttpServletRequest request) {
        Response response = null;
	try {
	    String timestampCurrent = String.valueOf(System.currentTimeMillis());
	    String timestampJson = dataValidator(timestampCurrent, TWAMP_TIMESTAMP, true, false, false);
	    String probeNumberJson = dataValidator(measurement.getProbeNumber(), TWAMP_PROBE_NUMBER, false, false, true);
	    String twampServerJson = dataValidator(measurement.getTwampServer(), TWAMP_SERVER, false, false, true);
	    String sentJson = dataValidator(measurement.getSent(), TWAMP_SENT, true, false, true);
	    String lostJson = dataValidator(measurement.getLost(), TWAMP_LOST, true, false, true);
	    String sendDupsJson = dataValidator(measurement.getSendDups(), TWAMP_SEND_DUPS, true, false, true);
	    String reflectDupsJson = dataValidator(measurement.getReflectDups(), TWAMP_REFLECT_DUPS, true, false, true);
	    String minRttJson = dataValidator(measurement.getMinRtt(), TWAMP_MIN_RTT, true, false, true);
	    String medianRttJson = dataValidator(measurement.getMedianRtt(), TWAMP_MEDIAN_RTT, true, false, true);
	    String maxRttJson = dataValidator(measurement.getMaxRtt(), TWAMP_MAX_RTT, true, false, true);
	    String errRttJson = dataValidator(measurement.getErrRtt(), TWAMP_ERR_RTT, true, false, true);
	    String minSendJson = dataValidator(measurement.getMinSend(), TWAMP_MIN_SEND, true, false, true);
	    String medianSendJson = dataValidator(measurement.getMedianSend(), TWAMP_MEDIAN_SEND, true, false, true);
	    String maxSendJson = dataValidator(measurement.getMaxSend(), TWAMP_MAX_SEND, true, false, true);
	    String errSendJson = dataValidator(measurement.getErrSend(), TWAMP_ERR_SEND, true, false, true);
	    String minReflectJson = dataValidator(measurement.getMinReflect(), TWAMP_MIN_REFLECT, true, false, true);
	    String medianReflectJson = dataValidator(measurement.getMedianReflect(), TWAMP_MEDIAN_REFLECT, true, false, true);
	    String maxReflectJson = dataValidator(measurement.getMaxReflect(), TWAMP_MAX_REFLECT, true, false, true);
	    String errReflectJson = dataValidator(measurement.getErrReflect(), TWAMP_ERR_REFLECT, true, false, true);
	    String minReflectorProcessingTimeJson = dataValidator(measurement.getMinReflectorProcessingTime(), TWAMP_MIN_REFLECTOR_PROCESSING_TIME, true, false, true);
	    String maxReflectorProcessingTimeJson = dataValidator(measurement.getMaxReflectorProcessingTime(), TWAMP_MAX_REFLECTOR_PROCESSING_TIME, true, false, true);
	    String twoWayJitterValueJson = dataValidator(measurement.getTwoWayJitterValue(), TWAMP_TWO_WAY_JITTER_VALUE, true, false, true);
	    String twoWayJitterCharJson = dataValidator(measurement.getTwoWayJitterChar(), TWAMP_TWO_WAY_JITTER_CHAR, false, false, true);
	    String sendJitterValueJson = dataValidator(measurement.getSendJitterValue(), TWAMP_SEND_JITTER_VALUE, true, false, true);
	    String sendJitterCharJson = dataValidator(measurement.getSendJitterChar(), TWAMP_SEND_JITTER_CHAR, false, false, true);
	    String reflectJitterValueJson = dataValidator(measurement.getReflectJitterValue(), TWAMP_REFLECT_JITTER_VALUE, true, false, true);
	    String reflectJitterCharJson = dataValidator(measurement.getReflectJitterChar(), TWAMP_REFLECT_JITTER_CHAR, false, false, true);
	    String sendHopsValueJson = dataValidator(measurement.getSendHopsValue(), TWAMP_SEND_HOPS_VALUE, true, false, true);
	    String sendHopsCharJson = dataValidator(measurement.getSendHopsChar(), TWAMP_SEND_HOPS_CHAR, false, false, true);
	    String reflectHopsValueJson = dataValidator(measurement.getReflectHopsValue(), TWAMP_REFLECT_HOPS_VALUE, true, false, true);
	    String reflectHopsCharJson = dataValidator(measurement.getReflectHopsChar(), TWAMP_REFLECT_HOPS_CHAR, false, false, true);
	    String ntpServerNtpstatJson = dataValidator(measurement.getNtpServerNtpstat(), NTP_SERVER_NTPSTAT, false, false, true);
	    String stratumJson = dataValidator(measurement.getStratum(), STRATUM, false, false, true);
	    String timeCorrectJson = dataValidator(measurement.getTimeCorrect(), TIME_CORRECT, false, false, true);
	    String ntpServerNtpqJson = dataValidator(measurement.getNtpServerNtpq(), NTP_SERVER_NTPQ, false, false, true);
	    String delayNtpqJson = dataValidator(measurement.getDelayNtpq(), DELAY_NTPQ, false, false, true);
	    String offsetNtpqJson = dataValidator(measurement.getOffsetNtpq(), OFFSET_NTPQ, false, false, true);
	    String jitterNtpqJson = dataValidator(measurement.getJitterNtpq(), JITTER_NTPQ, false, true, true);

	    String jsonStringDraft = "{" +
		    timestampJson + probeNumberJson + twampServerJson +
		    sentJson + lostJson + sendDupsJson + reflectDupsJson + minRttJson +
		    medianRttJson + maxRttJson + errRttJson + minSendJson + medianSendJson +
		    maxSendJson + errSendJson + minReflectJson + medianReflectJson +
		    maxReflectJson + errReflectJson + minReflectorProcessingTimeJson +
		    maxReflectorProcessingTimeJson + twoWayJitterValueJson +
		    twoWayJitterCharJson + sendJitterValueJson + sendJitterCharJson + 
		    reflectJitterValueJson + reflectJitterCharJson + sendHopsValueJson + 
		    sendHopsCharJson + reflectHopsValueJson + reflectHopsCharJson +
		    ntpServerNtpstatJson + stratumJson + timeCorrectJson +
		    ntpServerNtpqJson + delayNtpqJson + offsetNtpqJson +
		    jitterNtpqJson + "}";

            String jsonString = jsonStringDraft.replace("\", }", "\"}");
	    indexMeasurementTwamp(jsonString);
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
                // There are no RADIUS Logs corresponding to the received measurement
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
	m.setJitterMsec(measurement.getJitterMsec());
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
		probeNumber = testtoolUsed.split("-", 2)[1];
	}

        // Define Strings for the different measurement fields and results from correlation with RADIUS Logs
	String downloadThroughputJson = dataValidator(measurement.getDownloadThroughput().toString(), DOWNLOAD_THROUGHPUT, true, false, true);
	String uploadThroughputJson = dataValidator(measurement.getUploadThroughput().toString(), UPLOAD_THROUGHPUT, true, false, true);
	String localPingJson = dataValidator(measurement.getLocalPing().toString(), LOCAL_PING, true, false, true);
	String jitterMsecJson = dataValidator(measurement.getJitterMsec().toString(), JITTER_MSEC, true, false, true);
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
                downloadThroughputJson + uploadThroughputJson + localPingJson + jitterMsecJson +
                locationJson + locationMethodJson + testServerLocationJson + userAgentJson + 
		userBrowserJson + userOsJson + testToolJson + radiusTimestampJson + serviceTypeJson +
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
            SearchResponse<Map> response = AggregatorResource.elasticsearchClient.search(s -> s
                    .index(environment.getProperty(ES_INDEXNAME_DHCP))
                    .sort(so -> so.field(f -> f.field(DHCP_TIMESTAMP).order(SortOrder.Desc)))
                    .from(0)
                    .postFilter(new Query.Builder()
                            .term(t -> t
                                    .field(IP_ADDRESS_KEYWORD)
                                    .value(ip)
                            ).build())
                    .size(1)
                    .explain(true),
                    Map.class
            );
            final List<Hit<Map>> hits = response.hits().hits();
            if (response.hits().total().value() > 0) {
                    Hit<Map> hit = hits.get(0);
                    Map map = hit.source();
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
            SearchResponse<Map> response = AggregatorResource.elasticsearchClient.search(s -> s
                            .index(environment.getProperty(ES_INDEXNAME_RADIUS))
                            .sort(so -> so.field(f -> f.field(RADIUS_TIMESTAMP).order(SortOrder.Desc)))
                            .from(0)
                            .postFilter(new Query.Builder()
                                    .term(t -> t
                                            .field(FRAMED_IP_ADDRESS_KEYWORD)
                                            .value(ip)
                                    ).build())
                            .query(new Query.Builder()
                                    .term(t -> t
                                            .field(ACCT_STATUS_TYPE_KEYWORD)
                                            .value("Start")
                                    )
                                    .build())
                            .size(1)
                            .explain(true),
                    Map.class
            );
            final List<Hit<Map>> hits = response.hits().hits();
            if (response.hits().total().value() > 0) {
                Hit<Map> hit = hits.get(0);
                Map map = hit.source();
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
            SearchResponse<Map> response = AggregatorResource.elasticsearchClient.search(s -> s
                            .index(environment.getProperty(ES_INDEXNAME_RADIUS))
                            .sort(so -> so.field(f -> f.field(RADIUS_TIMESTAMP).order(SortOrder.Desc)))
                            .from(0)
                            .postFilter(new Query.Builder()
                                    .term(t -> t
                                            .field(CALLING_STATION_ID_KEYWORD)
                                            .value(mac)
                                    ).build())
                            .size(1)
                            .explain(true),
                    Map.class
            );

            final List<Hit<Map>> hits = response.hits().hits();
            if (response.hits().total().value() > 0) {
		    Hit<Map> hit = hits.get(0);
                    Map map = hit.source();
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
            Reader json = new StringReader(jsonString);
            IndexRequest<JsonData> request = IndexRequest.of(i -> i
                    .index(environment.getProperty(ES_INDEXNAME_MEASUREMENT))
                    .withJson(json)
            );
            AggregatorResource.elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    private void indexMeasurementProbes(String jsonString) {
        // Store received Wireless Network Performance Metrics (from WiFiMon Hardware Probes) in Elasticsearch
        try {
            Reader json = new StringReader(jsonString);
            IndexRequest<JsonData> request = IndexRequest.of(i -> i
                    .index(environment.getProperty(ES_INDEXNAME_PROBES))
                    .withJson(json)
            );

            AggregatorResource.elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    private void indexMeasurementTwamp(String jsonString) {
        try {
            Reader json = new StringReader(jsonString);
            IndexRequest<JsonData> request = IndexRequest.of(i -> i
                    .index(environment.getProperty(ES_INDEXNAME_TWAMP))
                    .withJson(json)
            );

            AggregatorResource.elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.info(e.toString());
        }
    }

    // Initialize High Level REST Client for HTTP
    private RestClient initHttpClient() {

        RestClient restClient = null;
        try {
            restClient = RestClient.builder(
                            new HttpHost(
                                    environment.getProperty(ES_HOST),
                                    Integer.parseInt(environment.getProperty(ES_PORT)),
                                    "http")).build();
        } catch (Exception e) {
	    logger.info(e.toString());
        }

        return restClient;
    }

    // A big part of the code that follows was taken from the Search Guard GitHub repository about Elasticsearch Java High Level REST Client
    // This method initializes a High Level REST Client using Keystore Certificates
    private RestClient initKeystoreClient() {
        RestClient restClient = null;

        try {
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            environment.getProperty(SSL_USER_USERNAME),
                            environment.getProperty(SSL_USER_PHRASE)));

            restClient = RestClient.builder(new HttpHost(
                            environment.getProperty(ES_HOST),
                            Integer.parseInt(environment.getProperty(ES_PORT)),
                            "https"))
                            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider)
                            ).build();
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
