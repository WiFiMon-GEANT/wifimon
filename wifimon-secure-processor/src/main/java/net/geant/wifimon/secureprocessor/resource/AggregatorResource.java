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
import org.elasticsearch.ElasticsearchStatusException;
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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.elasticsearch.client.RequestOptions;
import javax.crypto.Mac;

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
    private static final String SSL_ENABLED = "xpack.security.enabled";
    private static final String SSL_USER_USERNAME = "ssl.http.user.username";
    private static final String SSL_USER_PASSWORD = "ssl.http.user.password";
    private static final String SSL_KEYSTORE_FILEPATH = "ssl.http.keystore.filepath";
    private static final String SSL_KEYSTORE_PASSWORD = "ssl.http.keystore.password";
    private static final String SSL_TRUSTSTORE_FILEPATH = "ssl.http.truststore.filepath";
    private static final String SSL_TRUSTSTORE_PASSWORD = "ssl.http.truststore.password";
    private static final String SSL_KEY_PASSWORD = "ssl.http.key.password";
    private static final String HMAC_SHA512_KEY = "sha.key";

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

		// Initialize High Level REST Client
        	if (environment.getProperty(SSL_ENABLED).equals("true")){
		       restHighLevelClient = initKeystoreClient();
                } else {
	               restHighLevelClient = initHttpClient();
	        }

	        // Store measurements in elasticsearch
	        indexMeasurementProbes(restHighLevelClient, jsonString);
	        closeConnection();

                return Response.ok().build();

	} catch(Exception e) {
		System.out.println("Exception Caught. In detail:");
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

	// Encrypt Requester IP using HMAC SHA512 Algorithm
	EncryptClass encryptClass = new EncryptClass();
	String encryptedIP = "";
	try {
		encryptedIP = encryptClass.encrypt(ip, environment.getProperty(HMAC_SHA512_KEY));
		encryptedIP = encryptedIP.toLowerCase();
	} catch(Exception e) {
		System.out.println("Exception Caught. In detail:");
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
	       // There are RADIUS Logs corresponding to the received measurement
               String calledStationIdTemp = r.getCalledStationId().substring(0,17).toUpperCase().replace("-",":");
               ap = accesspointsRepository.find(calledStationIdTemp);
               response = addElasticMeasurement(joinMeasurement(measurement, r, ap, agent), requesterSubnet, encryptedIP);
           }
           else {
	       // There are not RADIUS Logs corresponding to the received measurement
               response = addElasticMeasurement(joinMeasurement(measurement, r, null, agent), requesterSubnet, encryptedIP);
       	   }
        }
	catch (IOException e) {
		System.out.println("Exception Caught. In detail:");
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

	// Define Strings for the different measurement fields and results from correlation with RADIUS Logs
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

	// Build the Json String to store in the elasticsearch cluster
        String jsonStringDraft = "{" +
                "\"timestamp\" : " + measurement.getTimestamp() + ", " +
                downloadThroughputJson + uploadThroughputJson + localPingJson +
                locationJson + locationMethodJson +
                userAgentJson + userBrowserJson + userOSJson + clientIpJson +
                testToolJson + requesterSubnetJson + encryptedIPJson + usernameJson + nasPortJson +
                callingStationIdJson + nasIdentifierJson + calledStationIdJson +
                nasIpAddressJson + apBuildingJson + apFloorJson + apLocationJson +
                apNotesJson + "}";

        String jsonString = jsonStringDraft.replace("\", }", "\"}");

	// Initialize High Level REST Client
        if (environment.getProperty(SSL_ENABLED).equals("true")){
		restHighLevelClient = initKeystoreClient();
        } else {
	        restHighLevelClient = initHttpClient();
	}

	// Store measurements in elasticsearch
	indexMeasurement(restHighLevelClient, jsonString);
	closeConnection();

        return Response.ok().build();
    }

    private RadiusStripped retrieveLastRadiusEntryByIp(String ip) {
	// Correlation with RADIUS Logs based on the Framed IP Address field

        RadiusStripped r = new RadiusStripped();

        if (environment.getProperty(SSL_ENABLED).equals("true")){
	    restHighLevelClient = initKeystoreClient();
	} else {
            restHighLevelClient = initHttpClient();
        }


	try {
           final SearchSourceBuilder builder = new SearchSourceBuilder()
            	.query(QueryBuilders.matchAllQuery())
                .sort(new FieldSortBuilder("Timestamp").order(SortOrder.DESC))
                .from(0)
                .fetchSource(new String[]{"User-Name", "Timestamp",
                         "nas_port", "Calling-Station-Id",
                         "NAS-Identifier", "Called-Station-Id", "NAS-IP-Address",
                         "Framed-IP-Address", "Acct-Status-Type"}, null)
		 .postFilter(QueryBuilders.termQuery("Framed-IP-Address", ip))
                 .query(QueryBuilders.termQuery("Acct-Status-Type", "Start"))
	         .size(1)
	         .explain(true);

           final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                 .source(builder);
		      
           final SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT); 


           final SearchHits hits = response.getHits();

           if (response.getHits().getTotalHits().value > 0) {
	       for (SearchHit hit : hits.getHits()) {
		   Map map = hit.getSourceAsMap();
		   r.setUserName(((map.get("User-Name") != null) ? map.get("User-Name").toString() : "N/A"));
                   r.setTimestamp(((map.get("Timestamp") != null) ? map.get("Timestamp").toString() : "N/A"));
                   r.setNasPort(((map.get("nas_port") != null) ? map.get("nas_port").toString() : "N/A"));
                   r.setCallingStationId(((map.get("Calling-Station-Id") != null) ? map.get("Calling-Station-Id").toString() : "N/A"));
                   r.setNasIdentifier(((map.get("NAS-Identifier") != null) ? map.get("NAS-Identifier").toString() : "N/A"));
                   r.setCalledStationId(((map.get("Called-Station-Id") != null) ? map.get("Called-Station-Id").toString() : "N/A"));
                   r.setNasIpAddress(((map.get("NAS-IP-Address") != null) ? map.get("NAS-IP-Address").toString() : "N/A"));
                   r.setFramedIpAddress(((map.get("Framed-IP-Address") != null) ? map.get("Framed-IP-Address").toString() : "N/A"));
                   r.setAcctStatusType(((map.get("Acct-Status-Type") != null) ? map.get("Acct-Status-Type").toString() : "N/A"));
	           break;
	       }
	    } else {
	       r = null;
            }

         closeConnection();

	}catch (ElasticsearchStatusException e) {
		r = null;
    	}catch (Exception e) {
		System.out.println(e);
		r = null;
	}
        return r;
    }

    private RadiusStripped retrieveLastRadiusEntryByMac(String mac) {
	// Correlation with RADIUS Logs based on the Calling Station Id field

        RadiusStripped r = new RadiusStripped();

        if (environment.getProperty(SSL_ENABLED).equals("true")){
	        restHighLevelClient = initKeystoreClient();
        }else {
		restHighLevelClient = initHttpClient();
	}

	try {
	    final SearchSourceBuilder builder = new SearchSourceBuilder()
		  .query(QueryBuilders.matchAllQuery())
		  .sort(new FieldSortBuilder("Timestamp").order(SortOrder.DESC))
                  .from(0)
                  .fetchSource(new String[]{"User-Name", "Timestamp",
                         "nas_port", "Calling-Station-Id",
                         "NAS-Identifier", "Called-Station-Id", "NAS-IP-Address",
                         "Framed-IP-Address", "Acct-Status-Type"}, null)
                  .postFilter(QueryBuilders.wildcardQuery("Calling-Station-Id", "*" + mac.replace(":","-").toLowerCase() + "*"))
                  .size(1)
                  .explain(true);

            final SearchRequest request = new SearchRequest(environment.getProperty(ES_INDEXNAME_RADIUS))
                  .source(builder);

            final SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            final SearchHits hits = response.getHits();

            if (response.getHits().getTotalHits().value > 0) {
               for (SearchHit hit : hits.getHits()) {
                   Map map = hit.getSourceAsMap();
                   r.setUserName(((map.get("User-Name") != null) ? map.get("User-Name").toString() : "N/A"));
                   r.setTimestamp(((map.get("Timestamp") != null) ? map.get("Timestamp").toString() : "N/A"));
                   r.setNasPort(((map.get("nas_port") != null) ? map.get("nas_port").toString() : "N/A"));
                   r.setCallingStationId(((map.get("Calling-Station-Id") != null) ? map.get("Calling-Station-Id").toString() : "N/A"));
                   r.setNasIdentifier(((map.get("NAS-Identifier") != null) ? map.get("NAS-Identifier").toString() : "N/A"));
                   r.setCalledStationId(((map.get("Called-Station-Id") != null) ? map.get("Called-Station-Id").toString() : "N/A"));
                   r.setNasIpAddress(((map.get("NAS-IP-Address") != null) ? map.get("NAS-IP-Address").toString() : "N/A"));
                   r.setFramedIpAddress(((map.get("Framed-IP-Address") != null) ? map.get("Framed-IP-Address").toString() : "N/A"));
                   r.setAcctStatusType(((map.get("Acct-Status-Type") != null) ? map.get("Acct-Status-Type").toString() : "N/A"));
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
	   // Store received measurements in Elasticsearch
	   IndexRequest indexRequest = new IndexRequest(
		environment.getProperty(ES_INDEXNAME_MEASUREMENT)); 
           indexRequest.source(jsonString,XContentType.JSON);
           try {
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
           } catch (ElasticsearchException e) {
                e.getDetailedMessage();
           } catch (java.io.IOException ex) {
                ex.getLocalizedMessage();
           }
    }

    private void indexMeasurementProbes(RestHighLevelClient restHighLevelClient, String jsonString) {
	   // Store received Wireless Network Performance Metrics (from WiFiMon Hardware Probes) in Elasticsearch
	   IndexRequest indexRequest = new IndexRequest(
		environment.getProperty(ES_INDEXNAME_PROBES)); 
           indexRequest.source(jsonString,XContentType.JSON);
           try {
                IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
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

    // A big part of the code that follows was taken from the Search Guard GitHub repository about Elasticsearch Java High Level REST Client
   // This method initializes a High Level REST Client using Keystore Certificates
   public RestHighLevelClient initKeystoreClient() {

          RestHighLevelClient restHighLevelClient = null;

          try {
                 char[] truststorePassword = environment.getProperty(SSL_TRUSTSTORE_PASSWORD).toCharArray();
                 char[] keystorePassword = environment.getProperty(SSL_KEYSTORE_PASSWORD).toCharArray();
                 char[] keyPassword = environment.getProperty(SSL_KEY_PASSWORD).toCharArray();

                 final CredentialsProvider credentialsProvider =
                      new BasicCredentialsProvider();
                      credentialsProvider.setCredentials(AuthScope.ANY,
                      new UsernamePasswordCredentials(
                              environment.getProperty(SSL_USER_USERNAME),
                              environment.getProperty(SSL_USER_PASSWORD)));


                 Optional<String> keyPasswordOpt = Optional.empty();
                 boolean trustSelfSigned=false;

                 SSLContext sslContextFromJks = SSLContexts
                      .custom()
                      .loadKeyMaterial(
                              new File(environment.getProperty(SSL_KEYSTORE_FILEPATH)),
                              keystorePassword,
                              keyPassword)
                      .loadTrustMaterial(
                              new File(environment.getProperty(SSL_TRUSTSTORE_FILEPATH)),
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
		  System.out.println("Exception Caught. In detail:");
                  System.out.println(e);
          }

          return restHighLevelClient;
       }

        // Used to encrypt IP addresses of end users
	public class EncryptClass {
    		public String encrypt(String myString, String myKey) {
        		Mac sha512_HMAC = null;
        		String result = null;
        		String key =  myKey;

        		try{
            			byte [] byteKey = key.getBytes("UTF-8");
            			final String HMAC_SHA512 = "HmacSHA512";
            			sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            			SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            			sha512_HMAC.init(keySpec);
            			byte [] mac_data = sha512_HMAC.
             			doFinal(myString.getBytes("UTF-8"));
            			result = bytesToHex(mac_data);
        		} catch (Exception e) {
				System.out.println("Exception Occured. In detail: ");
				System.out.println(e);
        		}

			return result;
    		}

   		 public String bytesToHex(byte[] bytes) {
        		final  char[] hexArray = "0123456789ABCDEF".toCharArray();
        		char[] hexChars = new char[bytes.length * 2];
        		for ( int j = 0; j < bytes.length; j++ ) {
            			int v = bytes[j] & 0xFF;
            			hexChars[j * 2] = hexArray[v >>> 4];
            			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        		}
        		return new String(hexChars);
    		}
	}

}
