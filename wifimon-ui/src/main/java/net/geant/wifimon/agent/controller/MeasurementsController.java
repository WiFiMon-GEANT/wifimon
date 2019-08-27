package net.geant.wifimon.agent.controller;

//import com.sun.jersey.api.client.Client;
import javax.ws.rs.client.Client;

import net.geant.wifimon.agent.repository.AccesspointRepository;
import net.geant.wifimon.agent.repository.UserRepository;
import net.geant.wifimon.agent.repository.VisualOptionsRepository;
import net.geant.wifimon.agent.service.AccesspointService;
import net.geant.wifimon.model.entity.UrlParameters;
import net.geant.wifimon.model.entity.UserData;
import net.geant.wifimon.model.entity.UserVisualOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by kanakisn on 8/5/15.
 */

@Controller
public class MeasurementsController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String SERVER_HOST_NAME = "server.host.name";
    private static final String KIBANA_PROTOCOL = "kibana.protocol";
    private static final String KIBANA_PORT = "kibana.port";

    private static final int PAGE_SIZE = 10;
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "date"));

    private final AccesspointService accesspointService;

    @Autowired
    public MeasurementsController(AccesspointService accesspointService) {
        this.accesspointService = accesspointService;
    }

    @Autowired
    AccesspointRepository accesspointRepository;

    @Autowired
    VisualOptionsRepository visualOptionsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Client client;

    @Autowired
    Environment environment;

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

    @RequestMapping(value = "/secure/overview")
    public String elasticsearchOverview(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                "/app/kibana#/dashboard/wifimon_dash_01_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_44_v0.1,panelIndex:1,row:1,size_x:12,size_y:6,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Overview+DashBoard+%5Bv0.1%5D',uiState:(P-1:(vis:(defaultColors:('0+-+100':'rgb(0,104,55)')))),viewMode:view)";

        model.addAttribute("classActiveSettingsElasticOverview", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchOverviewDashboard";
    }

    @RequestMapping(value = "/secure/measurements")
    public String genericMeasurements(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.Hide)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_privacy_dash_02_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_privacy_vis_43_v0.1,panelIndex:1,row:1,size_x:12,size_y:7,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Recent+measurements+%5Bv0.1-privacy%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_dash_02_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_43_v0.1,panelIndex:1,row:1,size_x:12,size_y:8,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Recent+measurements+%5Bv0.1%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                    "/app/kibana#/dashboard/wifimon_dash_02_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_43_v0.1,panelIndex:1,row:1,size_x:12,size_y:8,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Recent+measurements+%5Bv0.1%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticMeasurements", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/genericMeasurements";
    }

    @RequestMapping(value = "/secure/timeseries/downloadTimeseries")
    public String elasticsearchDownload(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.Hide)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_privacy_dash_03_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_06_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_14_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_40_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_05_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:!n),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_15_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:!n),gridData:(h:3,i:'6',w:6,x:6,y:6),id:wifimon_vis_39_v0.1,panelIndex:'6',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+download+time-series+%5Bv0.1-privacy%5D',viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_dash_03_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_06_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_14_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_40_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_05_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'5',w:6,x:0,y:9),id:wifimon_vis_38_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'6',w:6,x:6,y:9),id:wifimon_vis_13_v0.1,panelIndex:'6',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'7',w:6,x:0,y:6),id:wifimon_vis_11_v0.1,panelIndex:'7',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'8',w:6,x:6,y:6),id:wifimon_vis_12_v0.1,panelIndex:'8',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'9',w:6,x:0,y:12),id:wifimon_vis_15_v0.1,panelIndex:'9',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'10',w:6,x:6,y:12),id:wifimon_vis_39_v0.1,panelIndex:'10',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+download+time-series+%5Bv0.1%5D',viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		    "/app/kibana#/dashboard/wifimon_dash_03_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_06_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_14_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_40_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_05_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'5',w:6,x:0,y:9),id:wifimon_vis_38_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'6',w:6,x:6,y:9),id:wifimon_vis_13_v0.1,panelIndex:'6',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'7',w:6,x:0,y:6),id:wifimon_vis_11_v0.1,panelIndex:'7',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'8',w:6,x:6,y:6),id:wifimon_vis_12_v0.1,panelIndex:'8',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'9',w:6,x:0,y:12),id:wifimon_vis_15_v0.1,panelIndex:'9',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'10',w:6,x:6,y:12),id:wifimon_vis_39_v0.1,panelIndex:'10',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+download+time-series+%5Bv0.1%5D',viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticTimeseries", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchDownloadTimeseries";
    }


    @RequestMapping(value = "/secure/timeseries/uploadTimeseries")
    public String elasticsearchUpload(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.Hide)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_privacy_dash_04_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_29_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_16_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_25_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_22_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:!n),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_24_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:!n),gridData:(h:3,i:'6',w:6,x:6,y:6),id:wifimon_vis_23_v0.1,panelIndex:'6',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+upload+time-series+%5Bv0.1-privacy%5D',viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_dash_04_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_29_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_16_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_25_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_22_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_30_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'6',w:6,x:6,y:6),id:wifimon_vis_31_v0.1,panelIndex:'6',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'7',w:6,x:0,y:9),id:wifimon_vis_32_v0.1,panelIndex:'7',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'8',w:6,x:6,y:9),id:wifimon_vis_33_v0.1,panelIndex:'8',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'9',w:6,x:0,y:12),id:wifimon_vis_24_v0.1,panelIndex:'9',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'10',w:6,x:6,y:12),id:wifimon_vis_23_v0.1,panelIndex:'10',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+upload+time-series+%5Bv0.1%5D',viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		    "/app/kibana#/dashboard/wifimon_dash_04_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_29_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_16_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_25_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_22_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_30_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'6',w:6,x:6,y:6),id:wifimon_vis_31_v0.1,panelIndex:'6',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'7',w:6,x:0,y:9),id:wifimon_vis_32_v0.1,panelIndex:'7',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'8',w:6,x:6,y:9),id:wifimon_vis_33_v0.1,panelIndex:'8',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'9',w:6,x:0,y:12),id:wifimon_vis_24_v0.1,panelIndex:'9',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'10',w:6,x:6,y:12),id:wifimon_vis_23_v0.1,panelIndex:'10',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+upload+time-series+%5Bv0.1%5D',viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticTimeseries", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchUploadTimeseries";
    }

    @RequestMapping(value = "/secure/timeseries/pingTimeseries")
    public String elasticsearchPing(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.Hide)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_privacy_dash_05_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_26_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_19_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_34_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_41_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:!n),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_21_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:!n),gridData:(h:3,i:'7',w:6,x:6,y:6),id:wifimon_vis_20_v0.1,panelIndex:'7',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+ping+time-series+%5Bv0.1-privacy%5D',viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_dash_05_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_26_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_19_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_34_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_41_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_27_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'6',w:6,x:6,y:6),id:wifimon_vis_28_v0.1,panelIndex:'6',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'7',w:6,x:0,y:9),id:wifimon_vis_17_v0.1,panelIndex:'7',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'8',w:6,x:6,y:9),id:wifimon_vis_18_v0.1,panelIndex:'8',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'9',w:6,x:0,y:12),id:wifimon_vis_21_v0.1,panelIndex:'9',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'10',w:6,x:6,y:12),id:wifimon_vis_20_v0.1,panelIndex:'10',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+ping+time-series+%5Bv0.1%5D',viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		    "/app/kibana#/dashboard/wifimon_dash_05_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:wifimon_vis_26_v0.1,panelIndex:'1',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'2',w:6,x:6,y:0),id:wifimon_vis_19_v0.1,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:wifimon_vis_34_v0.1,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:wifimon_vis_41_v0.1,panelIndex:'4',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'5',w:6,x:0,y:6),id:wifimon_vis_27_v0.1,panelIndex:'5',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'6',w:6,x:6,y:6),id:wifimon_vis_28_v0.1,panelIndex:'6',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'7',w:6,x:0,y:9),id:wifimon_vis_17_v0.1,panelIndex:'7',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'8',w:6,x:6,y:9),id:wifimon_vis_18_v0.1,panelIndex:'8',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'9',w:6,x:0,y:12),id:wifimon_vis_21_v0.1,panelIndex:'9',type:visualization,version:'6.2.4'),(embeddableConfig:(spy:(mode:(fill:!f,name:!n))),gridData:(h:3,i:'10',w:6,x:6,y:12),id:wifimon_vis_20_v0.1,panelIndex:'10',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+ping+time-series+%5Bv0.1%5D',viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticTimeseries", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchPingTimeseries";
    }

    @RequestMapping(value = "/secure/HWProbes/downloadTimeseries")
    public String elasticsearchHWProbesDownload(Model model, HttpSession session, HttpServletRequest request) {

	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/kibana#/dashboard/71f7adc0-c810-11e9-a7f0-5d9876c8dfd2?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-30m,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:c59200b0-c811-11e9-a7f0-5d9876c8dfd2,panelIndex:'1',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'2',w:6,x:6,y:0),id:'93a413f0-c80b-11e9-a7f0-5d9876c8dfd2',panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:e37537a0-c811-11e9-a7f0-5d9876c8dfd2,panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:'9a727aa0-c80b-11e9-a7f0-5d9876c8dfd2',panelIndex:'4',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'5',w:6,x:0,y:6),id:f79558f0-c811-11e9-a7f0-5d9876c8dfd2,panelIndex:'5',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'6',w:6,x:6,y:6),id:a1dfb960-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'6',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'7',w:6,x:0,y:9),id:'098aab00-c812-11e9-a7f0-5d9876c8dfd2',panelIndex:'7',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'8',w:6,x:6,y:9),id:b09ac800-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'8',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'9',w:6,x:0,y:12),id:'24e896a0-c812-11e9-a7f0-5d9876c8dfd2',panelIndex:'9',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'10',w:6,x:6,y:12),id:a6106ac0-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'10',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'12',w:6,x:0,y:15),id:'38d3c540-c812-11e9-a7f0-5d9876c8dfd2',panelIndex:'12',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'13',w:6,x:6,y:15),id:edc5a7e0-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'13',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'14',w:6,x:0,y:18),id:'4bb747e0-c812-11e9-a7f0-5d9876c8dfd2',panelIndex:'14',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'15',w:6,x:6,y:18),id:f1c55e30-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'15',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'16',w:6,x:0,y:21),id:aba36710-c812-11e9-a7f0-5d9876c8dfd2,panelIndex:'16',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'17',w:6,x:6,y:21),id:f5121830-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'17',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'18',w:6,x:0,y:24),id:be3d5d90-c812-11e9-a7f0-5d9876c8dfd2,panelIndex:'18',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'19',w:6,x:6,y:24),id:f8976e60-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'19',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'20',w:6,x:0,y:27),id:cf6aa4b0-c812-11e9-a7f0-5d9876c8dfd2,panelIndex:'20',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'21',w:6,x:6,y:27),id:fb86c620-c80b-11e9-a7f0-5d9876c8dfd2,panelIndex:'21',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+Download+Throughput+Timeseries+per+HW+probe',viewMode:view)";

	model.addAttribute("classActiveSettingsElasticHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbesDownloadTimeseries";
    }

    @RequestMapping(value = "/secure/HWProbes/uploadTimeseries")
    public String elasticsearchHWProbesUpload(Model model, HttpSession session, HttpServletRequest request) {

	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/kibana#/dashboard/81f2dd80-c810-11e9-a7f0-5d9876c8dfd2?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-30m,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:f5afb390-c812-11e9-a7f0-5d9876c8dfd2,panelIndex:'1',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'2',w:6,x:6,y:0),id:f4d91bf0-c80d-11e9-a7f0-5d9876c8dfd2,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:'046d5a40-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:'03d8ada0-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'4',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'5',w:6,x:0,y:6),id:'126fa030-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'5',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'6',w:6,x:6,y:6),id:'1265ce70-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'6',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'7',w:6,x:0,y:9),id:'3c09a670-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'7',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'8',w:6,x:6,y:9),id:'1f0d8d70-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'8',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'9',w:6,x:0,y:12),id:'457ec320-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'9',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'10',w:6,x:6,y:12),id:'2c887af0-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'10',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'11',w:6,x:0,y:15),id:'50024290-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'11',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'12',w:6,x:6,y:15),id:'38f812f0-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'12',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'13',w:6,x:0,y:18),id:'5ad568a0-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'13',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'14',w:6,x:6,y:18),id:'45abdfe0-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'14',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'15',w:6,x:0,y:21),id:'65a9c730-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'15',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'16',w:6,x:6,y:21),id:'6fbbd970-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'16',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'17',w:6,x:0,y:24),id:'72084830-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'17',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'18',w:6,x:6,y:24),id:'7a5ca4e0-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'18',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'19',w:6,x:0,y:27),id:'7bf0c020-c813-11e9-a7f0-5d9876c8dfd2',panelIndex:'19',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'20',w:6,x:6,y:27),id:'87c0fd20-c80e-11e9-a7f0-5d9876c8dfd2',panelIndex:'20',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+Upload+Throughput+Timeseries+per+HW+Probe',viewMode:view)";

	model.addAttribute("classActiveSettingsElasticHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbesUploadTimeseries";
    }

    @RequestMapping(value = "/secure/HWProbes/pingTimeseries")
    public String elasticsearchHWProbesPing(Model model, HttpSession session, HttpServletRequest request) {

	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;
	
	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/kibana#/dashboard/03347190-c8c2-11e9-b2b0-176d156b1dbd?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now-30m,mode:quick,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((gridData:(h:3,i:'1',w:6,x:0,y:0),id:'0bb87590-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'1',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'2',w:6,x:6,y:0),id:b88f8970-c80f-11e9-a7f0-5d9876c8dfd2,panelIndex:'2',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'3',w:6,x:0,y:3),id:'1d6aa420-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'3',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'4',w:6,x:6,y:3),id:c7654c00-c80f-11e9-a7f0-5d9876c8dfd2,panelIndex:'4',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'5',w:6,x:0,y:6),id:'31344100-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'5',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'6',w:6,x:6,y:6),id:d59c0f70-c80f-11e9-a7f0-5d9876c8dfd2,panelIndex:'6',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'7',w:6,x:0,y:9),id:'51204040-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'7',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'8',w:6,x:6,y:9),id:ed123990-c80f-11e9-a7f0-5d9876c8dfd2,panelIndex:'8',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'9',w:6,x:0,y:12),id:'611c3350-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'9',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'10',w:6,x:6,y:12),id:fa938fb0-c80f-11e9-a7f0-5d9876c8dfd2,panelIndex:'10',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'11',w:6,x:0,y:15),id:'6e435b80-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'11',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'12',w:6,x:6,y:15),id:'09073510-c810-11e9-a7f0-5d9876c8dfd2',panelIndex:'12',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'13',w:6,x:0,y:18),id:'7af44240-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'13',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'14',w:6,x:6,y:18),id:'176ea570-c810-11e9-a7f0-5d9876c8dfd2',panelIndex:'14',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'15',w:6,x:0,y:21),id:'877602b0-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'15',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'16',w:6,x:6,y:21),id:'266dc1f0-c810-11e9-a7f0-5d9876c8dfd2',panelIndex:'16',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'17',w:6,x:0,y:24),id:'98fdc5e0-c814-11e9-a7f0-5d9876c8dfd2',panelIndex:'17',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'18',w:6,x:6,y:24),id:'34e0a400-c810-11e9-a7f0-5d9876c8dfd2',panelIndex:'18',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'19',w:6,x:0,y:27),id:aabe2540-c814-11e9-a7f0-5d9876c8dfd2,panelIndex:'19',type:visualization,version:'6.2.4'),(gridData:(h:3,i:'20',w:6,x:6,y:27),id:'47832600-c810-11e9-a7f0-5d9876c8dfd2',panelIndex:'20',type:visualization,version:'6.2.4')),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Average+Ping+Timeseries+per+HW+Probe',viewMode:edit)";

	model.addAttribute("classActiveSettingsElasticHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbesPingTimeseries";
    }

    @RequestMapping(value = "/secure/statistics/pieStatistics")
    public String elasticsearchPieStatistics(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.Hide)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_privacy_dash_06_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:7,id:wifimon_vis_36_v0.1,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_35_v0.1,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Pie+statistics+%5Bv0.1-privacy%5D',uiState:(),viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_dash_06_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_36_v0.1,panelIndex:1,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_37_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_35_v0.1,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Pie+statistics+%5Bv0.1%5D',uiState:(),viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                    "/app/kibana#/dashboard/wifimon_dash_06_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_36_v0.1,panelIndex:1,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_37_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_35_v0.1,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Pie+statistics+%5Bv0.1%5D',uiState:(),viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticStatistics", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchPieStatistics";
    }

    @RequestMapping(value = "/secure/statistics/tableStatistics")
    public String elasticsearchTableStatistics(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.Hide)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_privacy_dash_07_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_02_v0.1,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_03_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_09_v0.1,panelIndex:6,row:4,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1-privacy%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-2:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-6:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_dash_07_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_02_v0.1,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_03_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_04_v0.1,panelIndex:3,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_01_v0.1,panelIndex:4,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_10_v0.1,panelIndex:5,row:7,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_09_v0.1,panelIndex:6,row:7,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_08_v0.1,panelIndex:7,row:10,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-2:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-3:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-4:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-5:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-6:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-7:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                    "/app/kibana#/dashboard/wifimon_dash_07_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_02_v0.1,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_03_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_04_v0.1,panelIndex:3,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_01_v0.1,panelIndex:4,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_10_v0.1,panelIndex:5,row:7,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_09_v0.1,panelIndex:6,row:7,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_08_v0.1,panelIndex:7,row:10,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-2:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-3:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-4:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-5:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-6:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),P-7:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticStatistics", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchTableStatistics";
    }

    @RequestMapping(value = "/secure/maps/clients")
    public String elasticsearchMap(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                "/app/kibana#/dashboard/wifimon_dash_08_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_07_v0.1,panelIndex:1,row:1,size_x:12,size_y:6,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Measurements+per+Client+map+%5Bv0.1%5D',uiState:(),viewMode:view)";
        model.addAttribute("classActiveSettingsElasticMap", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchMeasurementsMap";
    }

    @RequestMapping(value = "/secure/maps/aps")
    public String elasticsearchApMap(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                "/app/kibana#/dashboard/wifimon_dash_09_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_42_v0.1,panelIndex:1,row:1,size_x:12,size_y:6,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Measurements+per+AP+map+%5Bv0.1%5D',uiState:(),viewMode:view)";
        model.addAttribute("classActiveSettingsElasticMap", "active");
        model.addAttribute("elasticsearchURL", elasticsearchURL);
        return "secure/elasticsearchApMap";
    }

    @RequestMapping(value = "/secure/guide")
    public String guide(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsGuide", "active");
        return "secure/guide";
    }

    @RequestMapping(value = "/secure/help")
    public String help(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsHelp", "active");
        return "secure/help";
    }

    private UrlParameters createUrlParameters(HttpServletRequest request) {
        UrlParameters urlParameters = new UrlParameters();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        String currentUserRole = userRepository.getRoleByEmail(currentUserEmail);
        String currentUserIp = request.getRemoteAddr();
        String queryFilter = new String();

        if (visualOptionsRepository.getLastEntry() != null && currentUserIp != null && !currentUserRole.equals("ADMIN")) {
            if (visualOptionsRepository.getLastEntry().getUservisualoption().equals(UserVisualOption.Match_Username)) {
                queryFilter = "query_string:(analyze_wildcard:!t,query:'username:*" + currentUserEmail + "*')";
            } else if (visualOptionsRepository.getLastEntry().getUservisualoption().equals(UserVisualOption.Match_IP)) {
                queryFilter = "query_string:(analyze_wildcard:!t,query:'clientIp:*" + currentUserIp + "*')";
            } else {
                queryFilter = "match_all:()";
            }
        } else {
            queryFilter = "match_all:()";
        }
        urlParameters.setCurrentUserRole(currentUserRole);
        urlParameters.setQueryFilter(queryFilter);
        return urlParameters;
    }
}
