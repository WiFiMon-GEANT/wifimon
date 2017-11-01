package net.geant.wifimon.agent.controller;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.geant.wifimon.agent.repository.AccesspointRepository;
import net.geant.wifimon.agent.repository.GenericMeasurementRepository;
import net.geant.wifimon.agent.repository.MapSettingsRepository;
import net.geant.wifimon.agent.repository.VisualOptionsRepository;
import net.geant.wifimon.agent.service.AccesspointService;
import net.geant.wifimon.agent.util.UiConstants;
import net.geant.wifimon.model.dto.GrafanaSnapshotResponse;
import net.geant.wifimon.model.entity.Accesspoint;
import net.geant.wifimon.model.entity.ElasticSearchSupport;
import net.geant.wifimon.model.entity.GenericMeasurement;
import net.geant.wifimon.model.entity.GrafanaSupport;
import net.geant.wifimon.model.entity.MapSettings;
import net.geant.wifimon.model.entity.Units;
import net.geant.wifimon.model.entity.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by kanakisn on 8/5/15.
 */

@Controller
public class MeasurementsController implements UiConstants {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String SERVER_HOST_NAME = "server.host.name";
    private static final String KIBANA_PROTOCOL = "kibana.protocol";
    private static final String KIBANA_PORT = "kibana.port";

    private static final int PAGE_SIZE = 10;
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC,"date"));

    private final AccesspointService accesspointService;

    @Autowired
    public MeasurementsController(AccesspointService accesspointService) {
        this.accesspointService = accesspointService;
    }

    @Autowired
    GenericMeasurementRepository gmRepository;

    @Autowired
    AccesspointRepository accesspointRepository;

    @Autowired
    VisualOptionsRepository visualOptionsRepository;

    @Autowired
    MapSettingsRepository mapSettingsRepository;

    @Autowired
    Client client;

    @Autowired
    Environment environment;

    @Value("${grafana.port}") @NotNull
    private int grafanaPort;

    @RequestMapping(value = "/secure/measurements/generic")
    public String afterLogin(@RequestParam(value = "move", required = false) String move, Model model,
                             HttpSession session) {
        Integer page = (Integer) session.getAttribute("page");
        String userdata;
        String measurementUnits;
        if (visualOptionsRepository.countEntries() < 1) {
            userdata = UserData.Show.toString();
            measurementUnits = Units.KBps.toString();
        }else{
            userdata = visualOptionsRepository.getLastEntry().getUserdata().toString();
            measurementUnits = visualOptionsRepository.getLastEntry().getUnits().toString();
        }

        boolean userDataView = ((userdata == "Show") ? true : false);
        boolean selectUnitsMBps = ((measurementUnits == "MBps") ? true : false);
        boolean selectUnitsMbps = ((measurementUnits == "Mbps") ? true : false);
        boolean selectUnitsKBps = ((measurementUnits == "KBps") ? true : false);
        boolean selectUnitsKbps = ((measurementUnits == "Kbps") ? true : false);

        if (page != null && move != null && !move.isEmpty()) {
            if ("front".equals(move)) page++;
            if ("back".equals(move)) page--;
            if (page < 0) page = 0;
        } else {
            page = 0;
        }
        Page<GenericMeasurement> measurementPage = gmRepository.findAll(new PageRequest(page, PAGE_SIZE, sort));
        int totalPages = measurementPage.getTotalPages();
        if (totalPages > 0 && page > totalPages - 1) {
            page = totalPages - 1;
            measurementPage = gmRepository.findAll(new PageRequest(page, PAGE_SIZE, sort));
        }
        session.setAttribute("page", page);
        model.addAttribute("measurements", measurementPage.getContent());
        model.addAttribute("userdataview", userDataView);
        model.addAttribute("selectUnitsMBps", selectUnitsMBps);
        model.addAttribute("selectUnitsMbps", selectUnitsMbps);
        model.addAttribute("selectUnitsKBps", selectUnitsKBps);
        model.addAttribute("selectUnitsKbps", selectUnitsKbps);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("classActiveSettingsHome","active");
        return "secure/genericMeasurements";
    }

    @RequestMapping(value = "/secure/grafana")
    public String grafana(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsTimeline","active");
        WebResource webResource = client
                .resource("https://admin:admin@localhost:" + grafanaPort + "/api/snapshots");
        ClientResponse response = null;
        String userdata;
        String grafanaSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            userdata = UserData.Show.toString();
            grafanaSupport = GrafanaSupport.True.toString();
        }else{
            userdata = visualOptionsRepository.getLastEntry().getUserdata().toString();
            grafanaSupport = visualOptionsRepository.getLastEntry().getGrafanasupport().toString();
        }

        boolean grafanaSupportView = ((grafanaSupport == "True") ? true : false);

        try {
            if (visualOptionsRepository.countEntries() < 1) {
                response = webResource.accept("application/json").type("application/json")
                        .post(ClientResponse.class, SNAPSHOT_JSON_REQUEST);
            }else{
                if (userdata == "Show"){
                    response = webResource.accept("application/json").type("application/json")
                            .post(ClientResponse.class, SNAPSHOT_JSON_REQUEST);
                }else {
                    response = webResource.accept("application/json").type("application/json")
                            .post(ClientResponse.class, SNAPSHOT_JSON_REQUEST_STRIPPED);
                }
            }
        } catch (Exception e) {
            LOG.error("An error occurred on creating grafana snapshot", e);
        }
        if (response == null || response.getStatus() != 200) {
            model.addAttribute("error", "Grafana snapshot creation failed");
            return "secure/grafana";
        }
        GrafanaSnapshotResponse snapshotResponse = response.getEntity(GrafanaSnapshotResponse.class);
        model.addAttribute("url", snapshotResponse.getUrl());
        model.addAttribute("grafanaSupportView", grafanaSupportView);
        return "secure/grafana";
    }

    @RequestMapping(value = "/secure/elasticsearch/overview")
    public String elasticsearchOverview(Model model, HttpSession session) {

        /*String hostName = "localhost";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GT72dfW_1lJUXnkLt?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8F0kfSfW_1lJUXnkKx,panelIndex:1,row:1,size_x:12,size_y:5,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Overview+DashBoard',uiState:(P-1:(vis:(defaultColors:('0+-+100':'rgb(0,104,55)')))),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchOverviewDashboard";
    }

    @RequestMapping(value = "/secure/elasticsearch/downloadTimeseries")
    public String elasticsearchDownload(Model model, HttpSession session) {

        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GN6UcfW_1lJUXnkLp?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8GCc2EfW_1lJUXnkLG,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GEMYufW_1lJUXnkLP,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GC3pffW_1lJUXnkLH,panelIndex:3,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GC8nFfW_1lJUXnkLI,panelIndex:4,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GDbj6fW_1lJUXnkLK,panelIndex:5,row:10,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GDgFbfW_1lJUXnkLL,panelIndex:6,row:10,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GD1DrfW_1lJUXnkLN,panelIndex:7,row:7,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GDwW3fW_1lJUXnkLM,panelIndex:8,row:7,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GD_HefW_1lJUXnkLO,panelIndex:9,row:13,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GDOZXfW_1lJUXnkLJ,panelIndex:10,row:13,size_x:6,size_y:3,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Average+download+time-series',uiState:(P-10:(spy:(mode:(fill:!f,name:!n))),P-2:(spy:(mode:(fill:!f,name:!n))),P-5:(spy:(mode:(fill:!f,name:!n))),P-6:(spy:(mode:(fill:!f,name:!n))),P-7:(spy:(mode:(fill:!f,name:!n))),P-8:(spy:(mode:(fill:!f,name:!n))),P-9:(spy:(mode:(fill:!f,name:!n)))),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchDownloadTimeseries";
    }

    @RequestMapping(value = "/secure/elasticsearch/uploadTimeseries")
    public String elasticsearchUpload(Model model, HttpSession session) {

        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GOfHJfW_1lJUXnkLq?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8GE_jMfW_1lJUXnkLQ,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GGOXnfW_1lJUXnkLa,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GFNaffW_1lJUXnkLS,panelIndex:3,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GFIXJfW_1lJUXnkLR,panelIndex:4,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GF7A0fW_1lJUXnkLY,panelIndex:5,row:7,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GFrNJfW_1lJUXnkLW,panelIndex:6,row:7,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GFcF1fW_1lJUXnkLV,panelIndex:7,row:10,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GFXdJfW_1lJUXnkLU,panelIndex:8,row:10,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GGIskfW_1lJUXnkLZ,panelIndex:9,row:13,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GFSA7fW_1lJUXnkLT,panelIndex:10,row:13,size_x:6,size_y:3,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Average+upload+time-series',uiState:(P-10:(spy:(mode:(fill:!f,name:!n))),P-2:(spy:(mode:(fill:!f,name:!n))),P-5:(spy:(mode:(fill:!f,name:!n))),P-6:(spy:(mode:(fill:!f,name:!n))),P-7:(spy:(mode:(fill:!f,name:!n))),P-8:(spy:(mode:(fill:!f,name:!n))),P-9:(spy:(mode:(fill:!f,name:!n)))),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchUploadTimeseries";
    }

    @RequestMapping(value = "/secure/elasticsearch/pingTimeseries")
    public String elasticsearchPing(Model model, HttpSession session) {

        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GOycifW_1lJUXnkLr?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8GIPXqfW_1lJUXnkLb,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GJJdifW_1lJUXnkLk,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GIdzAfW_1lJUXnkLd,panelIndex:3,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GIW7kfW_1lJUXnkLc,panelIndex:4,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GI9ssfW_1lJUXnkLi,panelIndex:5,row:7,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GI4GGfW_1lJUXnkLh,panelIndex:6,row:7,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GIyJ_fW_1lJUXnkLg,panelIndex:7,row:10,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GIs4yfW_1lJUXnkLf,panelIndex:8,row:10,size_x:6,size_y:3,type:visualization),(col:1,id:AV8GJC6kfW_1lJUXnkLj,panelIndex:9,row:13,size_x:6,size_y:3,type:visualization),(col:7,id:AV8GImn_fW_1lJUXnkLe,panelIndex:10,row:13,size_x:6,size_y:3,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Average+ping+time-series',uiState:(P-10:(spy:(mode:(fill:!f,name:!n))),P-2:(spy:(mode:(fill:!f,name:!n))),P-5:(spy:(mode:(fill:!f,name:!n))),P-6:(spy:(mode:(fill:!f,name:!n))),P-7:(spy:(mode:(fill:!f,name:!n))),P-8:(spy:(mode:(fill:!f,name:!n))),P-9:(spy:(mode:(fill:!f,name:!n)))),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchPingTimeseries";
    }

    @RequestMapping(value = "/secure/elasticsearch/pieStatistics")
    public String elasticsearchPieStatistics(Model model, HttpSession session) {

        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GKet_fW_1lJUXnkLm?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8F6zQrfW_1lJUXnkK2,panelIndex:1,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:AV8F6DNjfW_1lJUXnkK1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:AV8F7VXsfW_1lJUXnkK3,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Pie+statistics',uiState:(),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchPieStatistics";
    }

    @RequestMapping(value = "/secure/elasticsearch/tableStatistics")
    public String elasticsearchTableStatistics(Model model, HttpSession session) {

        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GKv-KfW_1lJUXnkLn?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8F9AX_fW_1lJUXnkK7,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:AV8F9Lh2fW_1lJUXnkK8,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:AV8F-dOGfW_1lJUXnkK_,panelIndex:3,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:AV8F-t-xfW_1lJUXnkLB,panelIndex:4,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:AV8F-_5hfW_1lJUXnkLC,panelIndex:5,row:7,size_x:6,size_y:3,type:visualization),(col:7,id:AV8F_RXcfW_1lJUXnkLD,panelIndex:6,row:7,size_x:6,size_y:3,type:visualization),(col:1,id:AV8F-LrcfW_1lJUXnkK-,panelIndex:7,row:10,size_x:6,size_y:3,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Table+statistics',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-2:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-3:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-4:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-5:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-6:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-7:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchTableStatistics";
    }

    @RequestMapping(value = "/secure/elasticsearch/map")
    public String elasticsearchMap(Model model, HttpSession session) {

        String elasticsearchSupport;
        if (visualOptionsRepository.countEntries() < 1) {
            elasticsearchSupport = ElasticSearchSupport.False.toString();
        }else{
            elasticsearchSupport = visualOptionsRepository.getLastEntry().getElasticsearchsupport().toString();
        }

        boolean elasticsearchSupportView = ((elasticsearchSupport == "True") ? true : false);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) + "/app/kibana#/dashboard/AV8GKBt1fW_1lJUXnkLl?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:AV8GANKDfW_1lJUXnkLE,panelIndex:1,row:1,size_x:11,size_y:5,type:visualization)),query:(match_all:()),timeRestore:!t,title:'WiFiMon:+Measurements+map',uiState:(),viewMode:view)";

        model.addAttribute("classActiveSettingsElastic","active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        model.addAttribute("elasticsearchSupportView", elasticsearchSupportView);
        return "secure/elasticsearchMeasurementsMap";
    }

    @RequestMapping(value = "/secure/guide")
    public String guide(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsGuide","active");
        return "secure/guide";
    }
    @RequestMapping(value = "/secure/help")
    public String help(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsHelp","active");
        return "secure/help";
    }

    @RequestMapping(value = "/secure/map")
    public String map(Model model, HttpSession session) {
        for (Accesspoint accesspoint : accesspointRepository.getAll()){
            /*
            AccesspointCreateModel accesspointCreateModel = new AccesspointCreateModel();
            accesspointCreateModel.setApid(accesspoint.getApid());
            accesspointCreateModel.setMac(accesspoint.getMac());
            accesspointCreateModel.setLatitude(accesspoint.getLatitude());
            accesspointCreateModel.setLongitude(accesspoint.getLongitude());
            accesspointCreateModel.setBuilding(accesspoint.getBuilding());
            accesspointCreateModel.setFloor(accesspoint.getFloor());
            accesspointCreateModel.setNotes(accesspoint.getNotes());
            accesspointCreateModel.setMeasurementscount(gmRepository.findCount(accesspoint.getMac()));
            accesspointCreateModel.setDownloadavg(gmRepository.findDownloadAvg(accesspoint.getMac()));
            accesspointCreateModel.setDownloadmin(gmRepository.findDownloadMin(accesspoint.getMac()));
            accesspointCreateModel.setDownloadmax(gmRepository.findDownloadMax(accesspoint.getMac()));
            accesspointCreateModel.setUploadavg(gmRepository.findUploadAvg(accesspoint.getMac()));
            accesspointCreateModel.setUploadmin(gmRepository.findUploadMin(accesspoint.getMac()));
            accesspointCreateModel.setUploadmax(gmRepository.findUploadMax(accesspoint.getMac()));
            accesspointCreateModel.setPingavg(gmRepository.findPingAvg(accesspoint.getMac()));
            accesspointCreateModel.setPingmin(gmRepository.findPingMin(accesspoint.getMac()));
            accesspointCreateModel.setPingmax(gmRepository.findPingMax(accesspoint.getMac()));

            accesspointRepository.delete(accesspoint.getApid());
            accesspointService.create(accesspointCreateModel);
            */
            String mac = accesspoint.getMac();
            Integer measurementsCount = gmRepository.findCount(mac);
            if (measurementsCount != 0) {
                Double downloadAvg = (double) Math.round(gmRepository.findDownloadAvg(mac) * 10) / 10;
                Double downloadMin = gmRepository.findDownloadMin(mac);
                Double downloadMax = gmRepository.findDownloadMax(mac);
                Double uploadAvg = (double) Math.round(gmRepository.findUploadAvg(mac) * 10) / 10;
                Double uploadMin = gmRepository.findUploadMin(mac);
                Double uploadMax = gmRepository.findUploadMax(mac);
                Double pingAvg = (double) Math.round(gmRepository.findPingAvg(mac) * 10) / 10;
                Double pingMin = gmRepository.findPingMin(mac);
                Double pingMax = gmRepository.findPingMax(mac);
                Integer ap = accesspointRepository.updateApStats(mac,
                                                                 measurementsCount, downloadAvg, downloadMin, downloadMax,
                                                                 uploadAvg, uploadMin, uploadMax,
                                                                 pingAvg, pingMin, pingMax);
            } else {
                Double downloadAvg = 0.0;
                Double downloadMin = 0.0;
                Double downloadMax = 0.0;
                Double uploadAvg = 0.0;
                Double uploadMin = 0.0;
                Double uploadMax = 0.0;
                Double pingAvg = 0.0;
                Double pingMin = 0.0;
                Double pingMax = 0.0;
                Integer ap = accesspointRepository.updateApStats(mac,
                                                                 measurementsCount, downloadAvg, downloadMin, downloadMax,
                                                                 uploadAvg, uploadMin, uploadMax,
                                                                 pingAvg, pingMin, pingMax);
            }
        }
        List<Accesspoint> accesspointsList = accesspointRepository.getAll();
        List<MapSettings> mapSettings = mapSettingsRepository.getAll();
        model.addAttribute("accesspointsMap", accesspointsList);
        model.addAttribute("mapSettings", mapSettings);
        model.addAttribute("classActiveSettingsMap","active");
        return "secure/map";
    }
}
