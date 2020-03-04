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

    @RequestMapping(value = "/secure/Subnets/147_102_13_0_24")
    public String elasticsearchSubnets_147_102_13_0_24(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;
	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/kibana#/dashboard/d3197ca0-e5f2-11e9-97e3-8705c21306b1?embed=true&_g=(refreshInterval:(pause:!f,value:30000),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:requesterSubnet,negate:!f,params:(query:'147.102.13.0%2F24',type:phrase),type:phrase,value:'147.102.13.0%2F24'),query:(match:(requesterSubnet:(query:'147.102.13.0%2F24',type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_13_v0.1,panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_38_v0.1,panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:wifimon_vis_33_v0.1,panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:wifimon_vis_32_v0.1,panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:wifimon_vis_18_v0.1,panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:wifimon_vis_17_v0.1,panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:wifimon_vis_05_v0.1,panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:wifimon_vis_40_v0.1,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:wifimon_vis_25_v0.1,panelIndex:'10',type:visualization,version:'6.8.3'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'11',w:24,x:0,y:120),id:wifimon_vis_28_v0.1,panelIndex:'11',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'13',w:24,x:0,y:135),id:wifimon_vis_15_v0.1,panelIndex:'13',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'14',w:24,x:24,y:135),id:wifimon_vis_39_v0.1,panelIndex:'14',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'15',w:24,x:0,y:150),id:wifimon_vis_24_v0.1,panelIndex:'15',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'16',w:24,x:24,y:165),id:wifimon_vis_20_v0.1,panelIndex:'16',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'17',w:24,x:0,y:165),id:wifimon_vis_21_v0.1,panelIndex:'17',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'19',w:24,x:0,y:60),id:wifimon_vis_22_v0.1,panelIndex:'19',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'24',w:24,x:24,y:150),id:wifimon_vis_23_v0.1,panelIndex:'24',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'26',w:24,x:0,y:75),id:wifimon_vis_41_v0.1,panelIndex:'26',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'27',w:24,x:24,y:75),id:wifimon_vis_34_v0.1,panelIndex:'27',type:visualization,version:'6.8.3'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'28',w:24,x:0,y:90),id:wifimon_vis_12_v0.1,panelIndex:'28',type:visualization,version:'6.8.3'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'29',w:24,x:24,y:90),id:wifimon_vis_11_v0.1,panelIndex:'29',type:visualization,version:'6.8.3'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'30',w:24,x:0,y:105),id:wifimon_vis_31_v0.1,panelIndex:'30',type:visualization,version:'6.8.3'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'31',w:24,x:24,y:105),id:wifimon_vis_30_v0.1,panelIndex:'31',type:visualization,version:'6.8.3'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'32',w:24,x:24,y:120),id:wifimon_vis_27_v0.1,panelIndex:'32',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:SubnetsTimeseries_147.102.13.0%2F24,viewMode:view)";
	model.addAttribute("classActiveSettingsSubnets", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchSubnets_147_102_13_0_24";
}
    @RequestMapping(value = "/secure/HWProbes/HWProbe1")
    public String elasticsearchHWProbe1(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/kibana#/dashboard/007907c0-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-1,boomerang-1,speedtest-1),type:phrases,value:'NetTest-1,+boomerang-1,+speedtest-1'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-1)),(match_phrase:(testTool:boomerang-1)),(match_phrase:(testTool:speedtest-1))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+1',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes1";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe2")
    public String elasticsearchHWProbe2(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/0dab5380-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-2,boomerang-2,speedtest-2),type:phrases,value:'NetTest-2,+boomerang-2,+speedtest-2'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-2)),(match_phrase:(testTool:boomerang-2)),(match_phrase:(testTool:speedtest-2))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+2',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes2";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe3")
    public String elasticsearchHWProbe3(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/1b3f6e50-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-3,boomerang-3,speedtest-3),type:phrases,value:'NetTest-3,+boomerang-3,+speedtest-3'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-3)),(match_phrase:(testTool:boomerang-3)),(match_phrase:(testTool:speedtest-3))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+3',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes3";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe4")
    public String elasticsearchHWProbe4(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/287bcc30-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-4,boomerang-4,speedtest-4),type:phrases,value:'NetTest-4,+boomerang-4,+speedtest-4'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-4)),(match_phrase:(testTool:boomerang-4)),(match_phrase:(testTool:speedtest-4))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+4',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes4";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe5")
    public String elasticsearchHWProbe5(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/33c02aa0-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-5,boomerang-5,speedtest-5),type:phrases,value:'NetTest-5,+boomerang-5,+speedtest-5'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-5)),(match_phrase:(testTool:boomerang-5)),(match_phrase:(testTool:speedtest-5))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+5',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes5";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe6")
    public String elasticsearchHWProbe6(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/429725b0-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-6,boomerang-6,speedtest-6),type:phrases,value:'NetTest-6,+boomerang-6,+speedtest-6'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-6)),(match_phrase:(testTool:boomerang-6)),(match_phrase:(testTool:speedtest-6))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+6',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes6";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe7")
    public String elasticsearchHWProbe7(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/539baf20-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-7,boomerang-7,speedtest-7),type:phrases,value:'NetTest-7,+boomerang-7,+speedtest-7'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-7)),(match_phrase:(testTool:boomerang-7)),(match_phrase:(testTool:speedtest-7))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+7',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes7";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe8")
    public String elasticsearchHWProbe8(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/60a03420-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-8,boomerang-8,speedtest-8),type:phrases,value:'NetTest-8,+boomerang-8,+speedtest-8'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-8)),(match_phrase:(testTool:boomerang-8)),(match_phrase:(testTool:speedtest-8))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+8',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes8";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe9")
    public String elasticsearchHWProbe9(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/6d943e60-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-9,boomerang-9,speedtest-9),type:phrases,value:'NetTest-9,+boomerang-9,+speedtest-9'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-9)),(match_phrase:(testTool:boomerang-9)),(match_phrase:(testTool:speedtest-9))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+9',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes9";
    }

    @RequestMapping(value = "/secure/HWProbes/HWProbe10")
    public String elasticsearchHWProbe10(Model model, HttpSession session, HttpServletRequest request) {
	UrlParameters urlParameters = createUrlParameters(request);
	String elasticsearchURL;

	elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/78983870-0b81-11ea-a8ab-cf1a4435820b?embed=true&_g=(refreshInterval:(pause:!t,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:testTool,negate:!f,params:!(NetTest-10,boomerang-10,speedtest-10),type:phrases,value:'NetTest-10,+boomerang-10,+speedtest-10'),query:(bool:(minimum_should_match:1,should:!((match_phrase:(testTool:NetTest-10)),(match_phrase:(testTool:boomerang-10)),(match_phrase:(testTool:speedtest-10))))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'6.8.3'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'6.8.3')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard+10',viewMode:view)";

	model.addAttribute("classActiveSettingsHWProbes", "active");
	model.addAttribute("elasticsearchURL", elasticsearchURL);
	return "secure/elasticsearchHWProbes10";
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
			"/app/kibana#/dashboard/wifimon_dash_07_v0.1?embed=true&_g=(refreshInterval:(pause:!f,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f),panels:!((embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_02_v0.1,panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_03_v0.1,panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'5',w:24,x:0,y:15),id:wifimon_vis_10_v0.1,panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'6',w:24,x:24,y:15),id:wifimon_vis_09_v0.1,panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'7',w:24,x:0,y:30),id:wifimon_vis_08_v0.1,panelIndex:'7',type:visualization,version:'6.8.3')),query:(language:lucene,query:(match_all:())),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1%5D',viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
			"/app/kibana#/dashboard/wifimon_dash_07_v0.1?embed=true&_g=(refreshInterval:(pause:!f,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f),panels:!((embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_02_v0.1,panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_03_v0.1,panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'5',w:24,x:0,y:15),id:wifimon_vis_10_v0.1,panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'6',w:24,x:24,y:15),id:wifimon_vis_09_v0.1,panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'7',w:24,x:0,y:30),id:wifimon_vis_08_v0.1,panelIndex:'7',type:visualization,version:'6.8.3')),query:(language:lucene,query:(match_all:())),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1%5D',viewMode:view)";
            }
        }else{
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		    "/app/kibana#/dashboard/wifimon_dash_07_v0.1?embed=true&_g=(refreshInterval:(pause:!f,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f),panels:!((embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_02_v0.1,panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_03_v0.1,panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'5',w:24,x:0,y:15),id:wifimon_vis_10_v0.1,panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'6',w:24,x:24,y:15),id:wifimon_vis_09_v0.1,panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'7',w:24,x:0,y:30),id:wifimon_vis_08_v0.1,panelIndex:'7',type:visualization,version:'6.8.3')),query:(language:lucene,query:(match_all:())),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1%5D',viewMode:view)";
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
