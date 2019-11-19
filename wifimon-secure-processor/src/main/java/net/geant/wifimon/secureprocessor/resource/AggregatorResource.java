package net.geant.wifimon.secureprocessor.resource;

import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.floragunn.searchguard.ssl.util.SSLConfigConstants;
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
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import java.io.IOException;
import java.util.HashMap;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static javax.crypto.Cipher.DECRYPT_MODE;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.SSLContext;
import javax.security.auth.x500.X500Principal;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.common.xcontent.XContentType;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;

// Added 01/10/2019
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


/**
 * Created by Kokkinos on 12/02/16, Transport client upgraded to High Level REST Client by Kostopoulos on 31/03/19.
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

    private static final String ES_HOST = "elasticsearch.host";
    private static final String ES_PORT = "elasticsearch.port";
    private static final String ES_INDEXNAME_MEASUREMENT = "elasticsearch.indexnamemeasurement";
    private static final String ES_TYPE_MEASUREMENT = "elasticsearch.typenamemeasurement";
    private static final String ES_INDEXNAME_RADIUS = "elasticsearch.indexnameradius";
    private static final String ES_TYPE_RADIUS = "elasticsearch.typenameradius";
    private static final String ES_INDEXNAME_DHCP = "elasticsearch.indexnamedhcp";
    private static final String ES_TYPE_DHCP = "elasticsearch.typenamedhcp";
    private static final String ES_INDEXNAME_PROBES = "elasticsearch.indexnameprobes";
    private static final String ES_TYPE_PROBES = "elasticsearch.typenameprobes";
    private static final String SG_SSL_ENABLED = "sg.ssl.enabled";
    private static final String SG_SSL_CERT_TYPE = "sg.ssl.certificate.type";
    private static final String SG_SSL_USER_USERNAME = "sg.ssl.http.user.username";
    private static final String SG_SSL_USER_PASSWORD = "sg.ssl.http.user.password";
    private static final String SG_SSL_PEMKEY_FILEPATH = "sg.ssl.http.pemkey.filepath";
    private static final String SG_SSL_PEMKEY_PASSWORD = "sg.ssl.http.pemkey.password";
    private static final String SG_SSL_PEMCERT_FILEPATH = "sg.ssl.http.pemcert.filepath";
    private static final String SG_SSL_PEMTRUSTEDCAS_FILEPATH = "sg.ssl.http.pemtrustedcas.filepath";
    private static final String SG_SSL_KEYSTORE_FILEPATH = "sg.ssl.http.keystore.filepath";
    private static final String SG_SSL_KEYSTORE_PASSWORD = "sg.ssl.http.keystore.password";
    private static final String SG_SSL_TRUSTSTORE_FILEPATH = "sg.ssl.http.truststore.filepath";
    private static final String SG_SSL_TRUSTSTORE_PASSWORD = "sg.ssl.http.truststore.password";
    private static final String SG_SSL_KEY_PASSWORD = "sg.ssl.http.key.password";

    // Added 01/10/2019
    private static final String AES_KEY = "aes.key";
    private static final String AES_IV = "aes.iv";

    // properties added by me
    private static RestHighLevelClient restHighLevelClient;


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
    @Path("/probes")
    public Response correlate(final ProbesMeasurement measurement, @Context HttpServletRequest request) {
	Response response = null;
	System.out.println("I HAVE SOMETHING HERE!!!!!");
	
	try {

		String bitRateJson = measurement.getBitRate() != null ? "\"bitRate\" : " + measurement.getBitRate() + ", " : "";
		String txPowerJson = measurement.getTxPower() != null ? "\"txPower\" : " + measurement.getTxPower() + ", " : "";
		String linkQualityJson = measurement.getLinkQuality() != null ? "\"linkQuality\" : " + measurement.getLinkQuality() + ", " : "";
		String signalLevelJson = measurement.getSignalLevel() != null ? "\"signalLevel\" : " + measurement.getSignalLevel() + ", " : "";
		String testToolJson = measurement.getTestTool() != null ? "\"testTool\" : \"" + measurement.getTestTool() + "\"" : "";
	        
		String jsonStringDraft = "{" +
			"\"timestamp\" : " + measurement.getTimestamp() + ", " +
			bitRateJson + txPowerJson + linkQualityJson +
			signalLevelJson + testToolJson + "}";

	        String jsonString = jsonStringDraft.replace("\", }", "\"}");
		System.out.println(jsonString);

		// Initialize High Level REST Client
        	if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            	   if (environment.getProperty(SG_SSL_CERT_TYPE, "keystore").equals("pem")) {
		       restHighLevelClient = initPemClient();
                   }else{
		       restHighLevelClient = initKeystoreClient();
                   }
                } else {
	               restHighLevelClient = initHttpClient();
	        }

	        // Store measurements in elasticsearch
	        indexMeasurementProbes(restHighLevelClient, jsonString);
	        closeConnection();

                return Response.ok().build();


	} catch(Exception e) {
		System.out.println("Exception Caught. In detail: ");
		System.out.println(e);
		response = null;
	}
	return response;
    }


    @POST
    @Path("/add")
    public Response correlate(final NetTestMeasurement measurement, @Context HttpServletRequest request) {
	Response response;
        String agent = request.getHeader("User-Agent") != null || !request.getHeader("User-Agent").isEmpty() ?  request.getHeader("User-Agent") : "N/A";
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty()) return Response.serverError().build();

	// Adding 19/09/2019
	// In the following, we find the registered subnet corresponding to the requester IP
	List<Subnet> subnets = subnetRepository.findAll();
	if (subnets == null || subnets.isEmpty()) return Response.ok(false).build();
	List<SubnetUtils.SubnetInfo> s = subnets.stream().
		map(it -> it.fromSubnetString()).collect(Collectors.toList());

	String foundSubnet = new String();
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

	// Encrypt Requester IP
	byte[] ipToEncrypt = ip.getBytes();
	byte[] key = DatatypeConverter.parseHexBinary(environment.getProperty(AES_KEY));
	byte[] iv = DatatypeConverter.parseHexBinary(environment.getProperty(AES_IV));
	AesCBC aes = new AesCBC(key, iv);
	String encryptedIP = "";
	try {
		encryptedIP = DatatypeConverter.printBase64Binary(aes.encrypt(ipToEncrypt));
	} catch(Exception e) {
		System.out.println("Exception Caught 1. In detail: ");
		System.out.println(e);
		System.exit(1);
	}


	// What is the correlation method defined by the administrator in the WiFiMon GUI?
        String correlationmethod = visualOptionsRepository.findCorrelationmethod();
        if (correlationmethod == null || correlationmethod.isEmpty()) {
            correlationmethod = "Radius_only";
        }

        RadiusStripped r = new RadiusStripped();
        Accesspoint ap = new Accesspoint();

	// Perform correlations and insert new measurements in the elasticsearch cluster
        if (correlationmethod.equals(CorrelationMethod.DHCP_and_Radius.toString())){
            //TODO Complete the else for correlation with DHCP and Radius
            String callingStationIdTemp = "A1:b2-cc-33-d0-da".substring(0,17);
            r = retrieveLastRadiusEntryByMac(callingStationIdTemp);
        }else{
            r = retrieveLastRadiusEntryByIp(encryptedIP);
        }

	try {
           if (r != null) {
               String calledStationIdTemp = r.getCalledStationId().substring(0,17).toUpperCase().replace("-",":");
               ap = accesspointsRepository.find(calledStationIdTemp);
               response = addElasticMeasurement(joinMeasurement(measurement, r, ap, agent), requesterSubnet, encryptedIP);
           }
           else {
               response = addElasticMeasurement(joinMeasurement(measurement, r, null, agent), requesterSubnet, encryptedIP);
       	   }
        }
	catch (IOException e) {
		System.out.println("Exception Caught 2. In detail:");
		System.out.println(e);
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
        m.setLongitude(measurement.getLongitude() != null ? Double.valueOf(measurement.getLongitude()): null);
        m.setLocationMethod(measurement.getLocationMethod());
        //m.setClientIp(ip);
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

    private Response addElasticMeasurement(AggregatedMeasurement measurement, String requesterSubnet, String encryptedIP) throws IOException {
        // addElasticMeasurement for Kibana 6.2.4
        String UserAgent = measurement.getUserAgent();

	// Section for User Operating System
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

	// Section for User Browser
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

	// Define Strings for the different measurememt fields
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

	// Build the Json String to store in the elasticsearch
        String jsonStringDraft = "{" +
                "\"timestamp\" : " + measurement.getTimestamp() + ", " +
                downloadThroughputJson + uploadThroughputJson + localPingJson +
                locationJson + locationMethodJson +
                userAgentJson + userBrowserJson + userOSJson +
                testToolJson + requesterSubnetJson + encryptedIPJson + usernameJson + nasPortJson +
                callingStationIdJson + nasIdentifierJson + calledStationIdJson +
                nasIpAddressJson + apBuildingJson + apFloorJson + apLocationJson +
                apNotesJson + "}";

        String jsonString = jsonStringDraft.replace("\", }", "\"}");

	// Initialize High Level REST Client
        if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            if (environment.getProperty(SG_SSL_CERT_TYPE, "keystore").equals("pem")) {
		restHighLevelClient = initPemClient();
            }else{
		restHighLevelClient = initKeystoreClient();
            }
        } else {
	        restHighLevelClient = initHttpClient();
	}

	// Store measurements in elasticsearch
	indexMeasurement(restHighLevelClient, jsonString);
	closeConnection();

        return Response.ok().build();
    }

    private RadiusStripped retrieveLastRadiusEntryByIp(String ip) {

        RadiusStripped r = new RadiusStripped();

        if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            if (environment.getProperty(SG_SSL_CERT_TYPE, "keystore").equals("pem")) {
		   restHighLevelClient = initPemClient();
            } else {
		   restHighLevelClient = initKeystoreClient();
            } 
	} else {
            restHighLevelClient = initHttpClient();
        }

	try {
           final SearchSourceBuilder builder = new SearchSourceBuilder()
            	.query(QueryBuilders.matchAllQuery())
                .sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC))
                .from(0)
                .fetchSource(new String[]{"username", "timestamp",
                         "nas_port", "source_host", "calling_station_id", "result",
                         "trace_id", "nas_identifier", "called_station_id", "nas_ip_address",
                         "framed_ip_address", "acct_status_type"}, null)
                 .postFilter(QueryBuilders.termQuery("framed_ip_address", ip))
                 .query(QueryBuilders.termQuery("acct_status_type", "Start"))
	         .size(1)
	         .explain(true);

           final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
	         .types(environment.getProperty(ES_TYPE_RADIUS))
                 .source(builder);
		      
           final SearchResponse response = restHighLevelClient.search(request); 
           final SearchHits hits = response.getHits();

           if (response.getHits().getTotalHits() > 0) {
	       for (SearchHit hit : hits.getHits()) {
		   Map map = hit.getSourceAsMap();
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
	    } else {
	       r = null;
            }

         closeConnection();
	} catch (Exception e) {
		System.out.println("Exception caught. In detail: ");
		System.out.println(e);
	}
        return r;
    }

    private RadiusStripped retrieveLastRadiusEntryByMac(String mac) {

        RadiusStripped r = new RadiusStripped();

        if (environment.getProperty(SG_SSL_ENABLED).equals("true")){
            if (environment.getProperty(SG_SSL_CERT_TYPE, "keystore").equals("pem")) {
		    restHighLevelClient = initPemClient();
            }else{
		    restHighLevelClient = initKeystoreClient();
	   }
        }else {
		restHighLevelClient = initHttpClient();
	}

	try {
	    final SearchSourceBuilder builder = new SearchSourceBuilder()
		  .query(QueryBuilders.matchAllQuery())
		  .sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC))
                  .from(0)
                  .fetchSource(new String[]{"username", "timestamp",
                         "nas_port", "source_host", "calling_station_id", "result",
                         "trace_id", "nas_identifier", "called_station_id", "nas_ip_address",
                         "framed_ip_address", "acct_status_type"}, null)
                  .postFilter(QueryBuilders.wildcardQuery("calling_station_id", "*" + mac.replace(":","-").toLowerCase() + "*"))
                  .size(1)
                  .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                  .types(environment.getProperty(ES_TYPE_RADIUS))
                  .source(builder);

            final SearchResponse response = restHighLevelClient.search(request);
            final SearchHits hits = response.getHits();

            if (response.getHits().getTotalHits() > 0) {
               for (SearchHit hit : hits.getHits()) {
                   Map map = hit.getSourceAsMap();
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
           } else {
               r = null;
           }

           closeConnection();
        } catch (Exception e) {
                System.out.println("Exception caught. In detail: ");
                System.out.println(e);
	}
        return r;
    }

    private SubnetUtils.SubnetInfo fromSubnetString(String subnet) {
        return new SubnetUtils(subnet).getInfo();
    }
    
    private void indexMeasurement(RestHighLevelClient restHighLevelClient, String jsonString) {
	   IndexRequest indexRequest = new IndexRequest(
		environment.getProperty(ES_INDEXNAME_MEASUREMENT), 
		environment.getProperty(ES_TYPE_MEASUREMENT));
           indexRequest.source(jsonString,XContentType.JSON);
           try {
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
           } catch (ElasticsearchException e) {
                e.getDetailedMessage();
           } catch (java.io.IOException ex) {
                ex.getLocalizedMessage();
           }
    }

    private void indexMeasurementProbes(RestHighLevelClient restHighLevelClient, String jsonString) {
	   IndexRequest indexRequest = new IndexRequest(
		environment.getProperty(ES_INDEXNAME_PROBES), 
		environment.getProperty(ES_TYPE_PROBES));
           indexRequest.source(jsonString,XContentType.JSON);
           try {
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
           } catch (ElasticsearchException e) {
                e.getDetailedMessage();
           } catch (java.io.IOException ex) {
                ex.getLocalizedMessage();
           }
    }

    // Initialize High Level REST Client for HTTP
    public RestHighLevelClient initHttpClient() {
	RestHighLevelClient restHighLevelClient = null;

	try {
            restHighLevelClient = new RestHighLevelClient(
            RestClient.builder(
                  new HttpHost(
                  environment.getProperty(ES_HOST),
                  Integer.parseInt(environment.getProperty(ES_PORT)),
        	  "http")));
	} catch (Exception e) {
            System.out.println("Exception caught. In detail: ");
	    System.out.println(e);
        }

        return restHighLevelClient;
    }

    // Close High Level REST Client
    public void closeConnection() throws IOException {
	 try {
             restHighLevelClient.close();
             restHighLevelClient = null;
	 } catch (Exception e) {
	     System.out.println("Exception caught. In detail: ");
             System.out.println(e);
	 }
    }

    // This method initializes a High Level REST Client using PEM Certificates
    public RestHighLevelClient initPemClient() {

        RestHighLevelClient restHighLevelClient = null;

        try {
                 final CredentialsProvider credentialsProvider =
                         new BasicCredentialsProvider();
                         credentialsProvider.setCredentials(AuthScope.ANY,
                         new UsernamePasswordCredentials(
                                  environment.getProperty(SG_SSL_USER_USERNAME),
                                  environment.getProperty(SG_SSL_USER_PASSWORD)));

                 Optional<String> keyPasswordOpt = Optional.empty();
                 boolean trustSelfSigned=true;

                 SSLContext sslContextFromPem = SSLContexts
                         .custom()
                         .loadKeyMaterial(PemReader.loadKeyStore(
                                 new File(environment.getProperty(SG_SSL_PEMCERT_FILEPATH)),
                                 new File(environment.getProperty(SG_SSL_PEMKEY_FILEPATH)),
                                 keyPasswordOpt),
                                 keyPasswordOpt.orElse(environment.getProperty(SG_SSL_PEMKEY_PASSWORD)).toCharArray())
                         .loadTrustMaterial(PemReader.loadTrustStore(
                                 new File(environment.getProperty(SG_SSL_PEMTRUSTEDCAS_FILEPATH))),
                                 trustSelfSigned?new TrustSelfSignedStrategy():null)
                         .build();

                         restHighLevelClient = new RestHighLevelClient(
                         RestClient.builder(new HttpHost(
                                 environment.getProperty(ES_HOST),
                                 Integer.parseInt(environment.getProperty(ES_PORT)),
                                 "https"))
                         .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                         .setDefaultCredentialsProvider(credentialsProvider)
                         .setSSLContext(sslContextFromPem)
                         ));

        }
        catch (Exception e) {
                 System.out.println("Exception caught. In detail: ");
                 System.out.println(e);
        }

        return restHighLevelClient;

    }

   // This method initializes a High Level REST Client using Keystore Certificates
   public RestHighLevelClient initKeystoreClient() {

          RestHighLevelClient restHighLevelClient = null;

          try {
                 char[] truststorePassword = environment.getProperty(SG_SSL_TRUSTSTORE_PASSWORD).toCharArray();
                 char[] keystorePassword = environment.getProperty(SG_SSL_KEYSTORE_PASSWORD).toCharArray();
                 char[] keyPassword = environment.getProperty(SG_SSL_KEY_PASSWORD).toCharArray();

                 final CredentialsProvider credentialsProvider =
                      new BasicCredentialsProvider();
                      credentialsProvider.setCredentials(AuthScope.ANY,
                      new UsernamePasswordCredentials(
                              environment.getProperty(SG_SSL_USER_USERNAME),
                              environment.getProperty(SG_SSL_USER_PASSWORD)));

                 Optional<String> keyPasswordOpt = Optional.empty();
                 boolean trustSelfSigned=false;

                 SSLContext sslContextFromJks = SSLContexts
                      .custom()
                      .loadKeyMaterial(
                              new File(environment.getProperty(SG_SSL_KEYSTORE_FILEPATH)),
                              keystorePassword,
                              keyPassword)
                      .loadTrustMaterial(
                              new File(environment.getProperty(SG_SSL_TRUSTSTORE_FILEPATH)),
                              truststorePassword,
                              trustSelfSigned?new TrustSelfSignedStrategy():null)
                      .build();

                 restHighLevelClient = new RestHighLevelClient(
                      RestClient.builder(new HttpHost(
                                      environment.getProperty(ES_HOST),
                                      Integer.parseInt(environment.getProperty(ES_PORT)),
                                      "https"))
                                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setSSLContext(sslContextFromJks)
                                ));
          }
          catch (Exception e) {
                  System.out.println("Exception caught. In detail: ");
                  System.out.println(e);
          }

          return restHighLevelClient;
       }

	// From: https://stackoverflow.com/questions/46835158/aes-256-cbc-in-java
       // A class to encrypt Strings using AES 256 CBC algorithm
      public static final class AesCBC {
    		private byte[] key;
    		private byte[] iv;

    		private static final String ALGORITHM="AES";

    		public AesCBC(byte[] key, byte[] iv) {
        		this.key = key;
        		this.iv = iv;
    		}

		// Encrypts Data based on the Aes 256 CBC algorithm
    		public byte[] encrypt(byte[] plainText) throws Exception{
        		SecretKeySpec secretKey=new SecretKeySpec(key,ALGORITHM);
        		IvParameterSpec ivParameterSpec=new IvParameterSpec(iv);
        		Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
        		cipher.init(Cipher.ENCRYPT_MODE,secretKey,ivParameterSpec);
        		return cipher.doFinal(plainText);
    		}

    		public byte[] getKey() {
        		return key;
    		}

   		 public void setKey(byte[] key) {
        		this.key = key;
    		}

    		public byte[] getIv() {
        		return iv;
    		}

    		public void setIv(byte[] iv) {
        		this.iv = iv;
    		}

	}


    public static final class PemReader
    {
        private static final Pattern CERT_PATTERN = Pattern.compile(
                "-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+" + // Header
                        "([a-z0-9+/=\\r\\n]+)" +                    // Base64 text
                        "-+END\\s+.*CERTIFICATE[^-]*-+",            // Footer
                CASE_INSENSITIVE);

        private static final Pattern KEY_PATTERN = Pattern.compile(
                "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+" + // Header
                        "([a-z0-9+/=\\r\\n]+)" +                       // Base64 text
                        "-+END\\s+.*PRIVATE\\s+KEY[^-]*-+",            // Footer
                CASE_INSENSITIVE);

        private PemReader() {}

        public static KeyStore loadTrustStore(File certificateChainFile)
                throws IOException, GeneralSecurityException
        {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);

            List<X509Certificate> certificateChain = readCertificateChain(certificateChainFile);
            for (X509Certificate certificate : certificateChain) {
                X500Principal principal = certificate.getSubjectX500Principal();
                keyStore.setCertificateEntry(principal.getName("RFC2253"), certificate);
            }
            return keyStore;
        }

        public static KeyStore loadKeyStore(File certificateChainFile, File privateKeyFile, Optional<String> keyPassword)
                throws IOException, GeneralSecurityException
        {
            PKCS8EncodedKeySpec encodedKeySpec = readPrivateKey(privateKeyFile, keyPassword);
            PrivateKey key;
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                key = keyFactory.generatePrivate(encodedKeySpec);
            }
            catch (InvalidKeySpecException ignore) {
                KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                key = keyFactory.generatePrivate(encodedKeySpec);
            }

            List<X509Certificate> certificateChain = readCertificateChain(certificateChainFile);
            if (certificateChain.isEmpty()) {
                throw new CertificateException("Certificate file does not contain any certificates: " + certificateChainFile);
            }

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);
            keyStore.setKeyEntry("key", key, keyPassword.orElse("").toCharArray(), certificateChain.stream().toArray(Certificate[]::new));
            return keyStore;
        }

        private static List<X509Certificate> readCertificateChain(File certificateChainFile)
                throws IOException, GeneralSecurityException
        {
            String contents = readFile(certificateChainFile);

            Matcher matcher = CERT_PATTERN.matcher(contents);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            List<X509Certificate> certificates = new ArrayList<>();

            int start = 0;
            while (matcher.find(start)) {
                byte[] buffer = base64Decode(matcher.group(1));
                certificates.add((X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(buffer)));
                start = matcher.end();
            }

            return certificates;
        }

        private static PKCS8EncodedKeySpec readPrivateKey(File keyFile, Optional<String> keyPassword)
                throws IOException, GeneralSecurityException
        {
            String content = readFile(keyFile);

            Matcher matcher = KEY_PATTERN.matcher(content);
            if (!matcher.find()) {
                throw new KeyStoreException("found no private key: " + keyFile);
            }
            byte[] encodedKey = base64Decode(matcher.group(1));

            if (!keyPassword.isPresent()) {
                return new PKCS8EncodedKeySpec(encodedKey);
            }

            EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(encodedKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptedPrivateKeyInfo.getAlgName());
            SecretKey secretKey = keyFactory.generateSecret(new PBEKeySpec(keyPassword.get().toCharArray()));

            Cipher cipher = Cipher.getInstance(encryptedPrivateKeyInfo.getAlgName());
            cipher.init(DECRYPT_MODE, secretKey, encryptedPrivateKeyInfo.getAlgParameters());

            return encryptedPrivateKeyInfo.getKeySpec(cipher);
        }

        private static byte[] base64Decode(String base64)
        {
            return Base64.getMimeDecoder().decode(base64.getBytes(US_ASCII));
        }

        private static String readFile(File file)
                throws IOException
        {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), US_ASCII)) {
                StringBuilder stringBuilder = new StringBuilder();

                CharBuffer buffer = CharBuffer.allocate(2048);
                while (reader.read(buffer) != -1) {
                    buffer.flip();
                    stringBuilder.append(buffer);
                    buffer.clear();
                }
                return stringBuilder.toString();
            }
        }
    }


}

