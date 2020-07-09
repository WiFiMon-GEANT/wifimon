package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.repository.AccesspointRepository;
import net.geant.wifimon.agent.repository.UserRepository;
import net.geant.wifimon.agent.repository.VisualOptionsRepository;
import net.geant.wifimon.model.entity.UrlParameters;
import net.geant.wifimon.model.entity.UserData;
import net.geant.wifimon.model.entity.UserVisualOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by kanakisn on 8/5/15.
 */

@Controller
public class MeasurementsController {

    private static final String SERVER_HOST_NAME = "server.host.name";
    private static final String KIBANA_PROTOCOL = "kibana.protocol";
    private static final String KIBANA_PORT = "kibana.port";
    private static final String CLASS_SETTINGS_STATE = "active";
    private static final String ELASTICSEARCH_URL = "elasticsearchURL";
    private static final String ELASTICSEARCH_CLASS_SETTINGS = "classActiveSettingsElasticTimeseries";
    private static final String HWPROBES_CLASS_SETTINGS = "classActiveSettingsHWProbes";

    @Autowired
    AccesspointRepository accesspointRepository;

    @Autowired
    VisualOptionsRepository visualOptionsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Environment environment;

    @GetMapping(value = "/username")
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping(value = "/secure/overview")
    public String elasticsearchOverview(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                "/app/kibana#/dashboard/wifimon_dash_01_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_44_v0.1,panelIndex:1,row:1,size_x:12,size_y:6,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Overview+DashBoard+%5Bv0.1%5D',uiState:(P-1:(vis:(defaultColors:('0+-+100':'rgb(0,104,55)')))),viewMode:view)";

        model.addAttribute("classActiveSettingsElasticOverview", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchOverviewDashboard";
    }

    @GetMapping(value = "/secure/measurements")
    public String genericMeasurements(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.HIDE)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_privacy_dash_02_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_privacy_vis_43_v0.1,panelIndex:1,row:1,size_x:12,size_y:7,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Recent+measurements+%5Bv0.1-privacy%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_dash_02_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_43_v0.1,panelIndex:1,row:1,size_x:12,size_y:8,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Recent+measurements+%5Bv0.1%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
            }
        } else {
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                    "/app/kibana#/dashboard/wifimon_dash_02_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_43_v0.1,panelIndex:1,row:1,size_x:12,size_y:8,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Recent+measurements+%5Bv0.1%5D',uiState:(P-1:(vis:(params:(sort:(columnIndex:!n,direction:!n))))),viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticMeasurements", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/genericMeasurements";
    }


       @GetMapping(value = "/secure/Subnets")
    public String elasticsearchSubnets(Model model, HttpSession session, HttpServletRequest request)
    {
            String elasticsearchURL;
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		    "/app/kibana#/dashboard/eda78290-20e1-11ea-9567-dfd517a2bd97?embed=true&_g=(filters:!(),refreshInterval:(pause:!f,value:30000),time:(from:now-1h,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:origin,negate:!f,params:(query:User),type:phrase,value:User),query:(match:(origin:(query:User,type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_13_v0.1,panelIndex:'1',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_38_v0.1,panelIndex:'2',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:wifimon_vis_33_v0.1,panelIndex:'3',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:wifimon_vis_32_v0.1,panelIndex:'4',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:wifimon_vis_18_v0.1,panelIndex:'5',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:wifimon_vis_17_v0.1,panelIndex:'6',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:wifimon_vis_05_v0.1,panelIndex:'7',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:wifimon_vis_40_v0.1,panelIndex:'8',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:wifimon_vis_25_v0.1,panelIndex:'10',type:visualization,version:'7.4.2'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'11',w:24,x:0,y:120),id:wifimon_vis_28_v0.1,panelIndex:'11',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'13',w:24,x:0,y:135),id:wifimon_vis_15_v0.1,panelIndex:'13',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'14',w:24,x:24,y:135),id:wifimon_vis_39_v0.1,panelIndex:'14',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'15',w:24,x:0,y:150),id:wifimon_vis_24_v0.1,panelIndex:'15',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'16',w:24,x:24,y:165),id:wifimon_vis_20_v0.1,panelIndex:'16',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'17',w:24,x:0,y:165),id:wifimon_vis_21_v0.1,panelIndex:'17',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'19',w:24,x:0,y:60),id:wifimon_vis_22_v0.1,panelIndex:'19',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'24',w:24,x:24,y:150),id:wifimon_vis_23_v0.1,panelIndex:'24',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'26',w:24,x:0,y:75),id:wifimon_vis_41_v0.1,panelIndex:'26',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'27',w:24,x:24,y:75),id:wifimon_vis_34_v0.1,panelIndex:'27',type:visualization,version:'7.4.2'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'28',w:24,x:0,y:90),id:wifimon_vis_12_v0.1,panelIndex:'28',type:visualization,version:'7.4.2'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'29',w:24,x:24,y:90),id:wifimon_vis_11_v0.1,panelIndex:'29',type:visualization,version:'7.4.2'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'30',w:24,x:0,y:105),id:wifimon_vis_31_v0.1,panelIndex:'30',type:visualization,version:'7.4.2'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'31',w:24,x:24,y:105),id:wifimon_vis_30_v0.1,panelIndex:'31',type:visualization,version:'7.4.2'),(embeddableConfig:(spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'32',w:24,x:24,y:120),id:wifimon_vis_27_v0.1,panelIndex:'32',type:visualization,version:'7.4.2')),query:(language:lucene,query:''),timeRestore:!t,title:'Subnets+Dashboard',viewMode:view)";
             model.addAttribute("classActiveSettingsSubnets", CLASS_SETTINGS_STATE);
             model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
             return "secure/elasticsearchSubnets";
    }


    @GetMapping(value = "/secure/Probes")
    public String elasticsearchProbes(Model model, HttpSession session, HttpServletRequest request)
    {
	    String elasticsearchURL;
	    elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
	"/app/kibana#/dashboard/e537f2f0-0b80-11ea-a8ab-cf1a4435820b?embed=true&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-1h,to:now))&_a=(description:'',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:origin,negate:!f,params:(query:Probe),type:phrase,value:Probe),query:(match:(origin:(query:Probe,type:phrase))))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'5',w:24,x:0,y:30),id:'3354b410-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'5',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',type:visualization,version:'7.4.2'),(embeddableConfig:(),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',type:visualization,version:'7.4.2')),query:(language:lucene,query:''),timeRestore:!t,title:'HW+Probe+Dashboard',viewMode:view)";
	     model.addAttribute("classActiveSettingsProbes", CLASS_SETTINGS_STATE);
	     model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
	     return "secure/elasticsearchProbes";
    }

    @GetMapping(value = "/secure/statistics/pieStatistics")
    public String elasticsearchPieStatistics(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);
        String elasticsearchURL;

        if (visualOptionsRepository.getLastEntry() != null) {
            if (visualOptionsRepository.getLastEntry().getUserdata().equals(UserData.HIDE)) {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_privacy_dash_06_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:7,id:wifimon_vis_36_v0.1,panelIndex:1,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_35_v0.1,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Pie+statistics+%5Bv0.1-privacy%5D',uiState:(),viewMode:view)";
            } else {
                elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_dash_06_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_36_v0.1,panelIndex:1,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_37_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_35_v0.1,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Pie+statistics+%5Bv0.1%5D',uiState:(),viewMode:view)";
            }
        } else {
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                    "/app/kibana#/dashboard/wifimon_dash_06_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_36_v0.1,panelIndex:1,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:wifimon_vis_37_v0.1,panelIndex:2,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:wifimon_vis_35_v0.1,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Pie+statistics+%5Bv0.1%5D',uiState:(),viewMode:view)";
        }

        model.addAttribute("classActiveSettingsElasticStatistics", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchPieStatistics";
    }

    @GetMapping(value = "/secure/statistics/tableStatistics")
    public String elasticsearchTableStatistics(Model model, HttpSession session, HttpServletRequest request) {

        String elasticsearchURL;

        elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                        "/app/kibana#/dashboard/wifimon_dash_07_v0.1?embed=true&_g=(refreshInterval:(pause:!f,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(darkTheme:!f),panels:!((embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_02_v0.1,panelIndex:'1',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_03_v0.1,panelIndex:'2',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'5',w:24,x:0,y:15),id:wifimon_vis_10_v0.1,panelIndex:'5',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'6',w:24,x:24,y:15),id:wifimon_vis_09_v0.1,panelIndex:'6',type:visualization,version:'6.8.3'),(embeddableConfig:(vis:(params:(sort:(columnIndex:!n,direction:!n)))),gridData:(h:15,i:'7',w:24,x:0,y:30),id:wifimon_vis_08_v0.1,panelIndex:'7',type:visualization,version:'6.8.3')),query:(language:lucene,query:(match_all:())),timeRestore:!t,title:'WiFiMon:+Table+statistics+%5Bv0.1%5D',viewMode:view)";

        model.addAttribute("classActiveSettingsElasticStatistics", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchTableStatistics";
    }

    @GetMapping(value = "/secure/maps/clients")
    public String elasticsearchMap(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                "/app/kibana#/dashboard/wifimon_dash_08_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_07_v0.1,panelIndex:1,row:1,size_x:12,size_y:6,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Measurements+per+Client+map+%5Bv0.1%5D',uiState:(),viewMode:view)";
        model.addAttribute("classActiveSettingsElasticMap", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchMeasurementsMap";
    }

    @GetMapping(value = "/secure/maps/aps")
    public String elasticsearchApMap(Model model, HttpSession session, HttpServletRequest request) {

        UrlParameters urlParameters = createUrlParameters(request);

        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
                "/app/kibana#/dashboard/wifimon_dash_09_v0.1?embed=true&_g=(refreshInterval:(display:'30+seconds',pause:!f,section:1,value:30000),time:(from:now%2Fd,mode:quick,to:now%2Fd))&_a=(description:'',filters:!(),options:(darkTheme:!f),panels:!((col:1,id:wifimon_vis_42_v0.1,panelIndex:1,row:1,size_x:12,size_y:6,type:visualization)),query:(" + urlParameters.getQueryFilter() + "),timeRestore:!t,title:'WiFiMon:+Measurements+per+AP+map+%5Bv0.1%5D',uiState:(),viewMode:view)";
        model.addAttribute("classActiveSettingsElasticMap", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchApMap";
    }

    @GetMapping(value = "/secure/guide")
    public String guide(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsGuide", CLASS_SETTINGS_STATE);
        return "secure/guide";
    }

    @GetMapping(value = "/secure/help")
    public String help(Model model, HttpSession session) {
        model.addAttribute("classActiveSettingsHelp", CLASS_SETTINGS_STATE);
        return "secure/help";
    }

    private UrlParameters createUrlParameters(HttpServletRequest request) {
        UrlParameters urlParameters = new UrlParameters();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        String currentUserRole = userRepository.getRoleByEmail(currentUserEmail);
        String currentUserIp = request.getRemoteAddr();
        String queryFilter;

        if (visualOptionsRepository.getLastEntry() != null && currentUserIp != null && !currentUserRole.equals("ADMIN")) {
            if (visualOptionsRepository.getLastEntry().getUservisualoption().equals(UserVisualOption.MATCH_USERNAME)) {
                queryFilter = "query_string:(analyze_wildcard:!t,query:'username:*" + currentUserEmail + "*')";
            } else if (visualOptionsRepository.getLastEntry().getUservisualoption().equals(UserVisualOption.MATCH_IP)) {
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
