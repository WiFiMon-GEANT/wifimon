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

       @GetMapping(value = "/secure/crowdsourced/averageValues")
    public String elasticsearchCrowdsourcedAverage(Model model, HttpSession session, HttpServletRequest request)
    {
            String elasticsearchURL;
            elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		    "/app/dashboards#/view/eda78290-20e1-11ea-9567-dfd517a2bd97?embed=true&_g=(filters:!())&_a=(description:'A%20dashboard%20related%20to%20average%20values%20of%20Crowdsourced%20Measurements',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:Origin,negate:!f,params:(query:User),type:phrase),query:(match_phrase:(Origin:User)))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(enhancements:(),hidePanelTitles:!f,vis:(legendOpen:!f)),gridData:(h:15,i:'1',w:24,x:0,y:0),id:wifimon_vis_13_v0.1,panelIndex:'1',title:'Average%20Download%20Throughput%20per%20Client%20IP%20(Bottom%2010%20Clients)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,vis:(legendOpen:!f)),gridData:(h:15,i:'2',w:24,x:24,y:0),id:wifimon_vis_38_v0.1,panelIndex:'2',title:'Average%20Download%20Throughput%20per%20Client%20IP%20(Top%2010%20Clients)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,vis:(legendOpen:!f)),gridData:(h:15,i:'3',w:24,x:0,y:15),id:wifimon_vis_33_v0.1,panelIndex:'3',title:'Average%20Upload%20Throughput%20per%20Client%20IP%20(Bottom%2010%20Clients)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,vis:(legendOpen:!f)),gridData:(h:15,i:'4',w:24,x:24,y:15),id:wifimon_vis_32_v0.1,panelIndex:'4',title:'Average%20Upload%20Throughput%20per%20Client%20IP%20(Top%2010%20Clients)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,vis:(legendOpen:!f)),gridData:(h:15,i:'5',w:24,x:0,y:30),id:wifimon_vis_18_v0.1,panelIndex:'5',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Client%20IP%20(Bottom%2010%20Clients)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,vis:(legendOpen:!f)),gridData:(h:15,i:'6',w:24,x:24,y:30),id:wifimon_vis_17_v0.1,panelIndex:'6',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Client%20IP%20(Top%2010%20Clients)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'7',w:24,x:0,y:45),id:wifimon_vis_05_v0.1,panelIndex:'7',title:'Average%20Download%20Throughput%20per%20Access%20Point%20(AP)%20MAC%20(Bottom%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'8',w:24,x:24,y:45),id:wifimon_vis_40_v0.1,panelIndex:'8',title:'Average%20Download%20Throughput%20per%20Access%20Point%20(AP)%20MAC%20(Top%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'10',w:24,x:24,y:60),id:wifimon_vis_25_v0.1,panelIndex:'10',title:'Average%20Upload%20Throughput%20per%20Access%20Point%20(AP)%20MAC%20(Top%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'11',w:24,x:0,y:120),id:wifimon_vis_28_v0.1,panelIndex:'11',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Client%20MAC%20(Bottom%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'13',w:24,x:0,y:135),id:wifimon_vis_15_v0.1,panelIndex:'13',title:'Average%20Download%20Throughput%20per%20Operating%20System%20(OS)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'14',w:24,x:24,y:135),id:wifimon_vis_39_v0.1,panelIndex:'14',title:'Average%20Download%20Throughput%20per%20Client%20Browser',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'15',w:24,x:0,y:150),id:wifimon_vis_24_v0.1,panelIndex:'15',title:'Average%20Upload%20Throughput%20per%20Operating%20System%20(OS)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'16',w:24,x:24,y:165),id:wifimon_vis_20_v0.1,panelIndex:'16',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Client%20Browser',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'17',w:24,x:0,y:165),id:wifimon_vis_21_v0.1,panelIndex:'17',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Operating%20System%20(OS)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'19',w:24,x:0,y:60),id:wifimon_vis_22_v0.1,panelIndex:'19',title:'Average%20Upload%20Throughput%20per%20Access%20Point%20(AP)%20MAC%20(Bottom%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'24',w:24,x:24,y:150),id:wifimon_vis_23_v0.1,panelIndex:'24',title:'Average%20Upload%20Throughput%20per%20Client%20Browser',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'26',w:24,x:0,y:75),id:wifimon_vis_41_v0.1,panelIndex:'26',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Access%20Point%20(AP)%20MAC%20(Bottom%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'27',w:24,x:24,y:75),id:wifimon_vis_34_v0.1,panelIndex:'27',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Access%20Point%20(AP)%20MAC%20(Top%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,spy:(mode:(fill:!f,name:table)),table:!n,vis:(legendOpen:!f)),gridData:(h:15,i:'28',w:24,x:0,y:90),id:wifimon_vis_12_v0.1,panelIndex:'28',title:'Average%20Download%20Throughput%20per%20Client%20MAC%20(Bottom%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'29',w:24,x:24,y:90),id:wifimon_vis_11_v0.1,panelIndex:'29',title:'Average%20Download%20Throughput%20per%20Client%20MAC%20(Top%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,spy:(mode:(fill:!f,name:table)),table:!n,vis:(legendOpen:!f)),gridData:(h:15,i:'30',w:24,x:0,y:105),id:wifimon_vis_31_v0.1,panelIndex:'30',title:'Average%20Upload%20Throughput%20per%20Client%20MAC%20(Bottom%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'31',w:24,x:24,y:105),id:wifimon_vis_30_v0.1,panelIndex:'31',title:'Average%20Upload%20Throughput%20per%20Client%20MAC%20(Top%2010)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f,spy:(mode:(fill:!f,name:table)),vis:(legendOpen:!f)),gridData:(h:15,i:'32',w:24,x:24,y:120),id:wifimon_vis_27_v0.1,panelIndex:'32',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20per%20Client%20MAC%20(Top%2010)',type:visualization,version:'7.15.1')),query:(language:lucene,query:''),tags:!(),timeRestore:!t,title:'Crowdsourced%20Measurements%20Dashboard%20(Average)',viewMode:view)";
             model.addAttribute("classActiveSettingsCrowdsourced", CLASS_SETTINGS_STATE);
             model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
             return "secure/elasticsearchSubnetsAverage";
    }

    @GetMapping(value = "/secure/crowdsourced/medianValues")
    public String elasticsearchCrowdsourcedMedian(Model model, HttpSession session, HttpServletRequest request) {
        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/dashboards#/view/4978de50-3dac-11ec-8eec-09df95559c81?embed=true&_g=(filters:!())&_a=(description:'A%20dashboard%20related%20to%20median%20values%20of%20Crowdsourced%20Measurements',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:Origin,negate:!f,params:(query:User),type:phrase),query:(match_phrase:(Origin:User)))),fullScreenMode:!f,options:(hidePanelTitles:!f,syncColors:!f,useMargins:!t),panels:!((embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:f7122f1c-d0b7-44a8-967b-f129ce79dd7b,w:24,x:0,y:0),id:'05a40d80-3d9d-11ec-8eec-09df95559c81',panelIndex:f7122f1c-d0b7-44a8-967b-f129ce79dd7b,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:'9c290e97-0b62-4e4b-bf0f-b4193a3df3be',w:24,x:24,y:0),id:'33d241e0-3d9d-11ec-8eec-09df95559c81',panelIndex:'9c290e97-0b62-4e4b-bf0f-b4193a3df3be',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:'66fd46c6-2dab-4681-ad29-c9ecdc38d066',w:24,x:0,y:15),id:wifimon_vis_33_v0.1,panelIndex:'66fd46c6-2dab-4681-ad29-c9ecdc38d066',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:b71c535a-c284-459e-afd2-59d7fe5bd6aa,w:24,x:24,y:15),id:'1e010340-3d9f-11ec-8eec-09df95559c81',panelIndex:b71c535a-c284-459e-afd2-59d7fe5bd6aa,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:'3aa583cc-541d-4eb6-ad5f-23e617aef770',w:24,x:24,y:30),id:f91908d0-3d9d-11ec-8eec-09df95559c81,panelIndex:'3aa583cc-541d-4eb6-ad5f-23e617aef770',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'6ef837ce-a86a-460c-b755-138b6f4c20fe',w:24,x:0,y:45),id:e9a63b90-3da0-11ec-8eec-09df95559c81,panelIndex:'6ef837ce-a86a-460c-b755-138b6f4c20fe',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:d021d1c0-d64c-403d-b8d2-8b7a10e4daaa,w:24,x:24,y:45),id:'28be6c80-3da1-11ec-8eec-09df95559c81',panelIndex:d021d1c0-d64c-403d-b8d2-8b7a10e4daaa,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:cab16151-b407-4559-857c-926ba4b9b7e2,w:24,x:0,y:60),id:'9dbb0070-3da1-11ec-8eec-09df95559c81',panelIndex:cab16151-b407-4559-857c-926ba4b9b7e2,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'9ae1f72d-2988-4e8a-b447-2be3b5121510',w:24,x:24,y:60),id:c619c790-3da1-11ec-8eec-09df95559c81,panelIndex:'9ae1f72d-2988-4e8a-b447-2be3b5121510',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'3635f384-5f40-436f-aaa2-8d46284db23b',w:24,x:0,y:75),id:b7be48a0-3da2-11ec-8eec-09df95559c81,panelIndex:'3635f384-5f40-436f-aaa2-8d46284db23b',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:be0f7c9a-c86b-4a7f-af9c-ee977c5004ac,w:24,x:24,y:75),id:fe08f940-3da2-11ec-8eec-09df95559c81,panelIndex:be0f7c9a-c86b-4a7f-af9c-ee977c5004ac,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:fb513777-3403-46bf-af18-137fb1b104a3,w:24,x:0,y:90),id:'3b7336a0-3da4-11ec-8eec-09df95559c81',panelIndex:fb513777-3403-46bf-af18-137fb1b104a3,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:'0692ac2d-5a3f-404b-9267-c814e7f87855',w:24,x:24,y:90),id:'5cae2410-3da4-11ec-8eec-09df95559c81',panelIndex:'0692ac2d-5a3f-404b-9267-c814e7f87855',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:f807fdeb-57e7-4d17-806e-171b9047cd8c,w:24,x:0,y:105),id:'03442ae0-3da5-11ec-8eec-09df95559c81',panelIndex:f807fdeb-57e7-4d17-806e-171b9047cd8c,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:a1672b8b-5a34-4207-9c29-88c11818df8b,w:24,x:24,y:105),id:e5538670-3da4-11ec-8eec-09df95559c81,panelIndex:a1672b8b-5a34-4207-9c29-88c11818df8b,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:'4b711746-671f-4e1a-8da9-29575158bd4a',w:24,x:0,y:120),id:'9c5224d0-3da5-11ec-8eec-09df95559c81',panelIndex:'4b711746-671f-4e1a-8da9-29575158bd4a',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),vis:(legendOpen:!f)),gridData:(h:15,i:'65b5a9eb-5db7-40c9-8bd7-d88a7c41586c',w:24,x:24,y:120),id:c61a18e0-3da5-11ec-8eec-09df95559c81,panelIndex:'65b5a9eb-5db7-40c9-8bd7-d88a7c41586c',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:e5c40148-893d-4ed9-b692-4df5043e257b,w:24,x:0,y:135),id:'0e684530-3da7-11ec-8eec-09df95559c81',panelIndex:e5c40148-893d-4ed9-b692-4df5043e257b,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'6ef4b89a-c86b-4a1b-bcf5-508ff9083061',w:24,x:24,y:135),id:'08459260-3da8-11ec-8eec-09df95559c81',panelIndex:'6ef4b89a-c86b-4a1b-bcf5-508ff9083061',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'584bee6e-4098-481d-afc3-30775fcb7b96',w:24,x:0,y:150),id:'7ecfc0a0-3da7-11ec-8eec-09df95559c81',panelIndex:'584bee6e-4098-481d-afc3-30775fcb7b96',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:c61cdafd-c0f8-4545-8959-1bf634305a36,w:24,x:24,y:150),id:'3037dc10-3da8-11ec-8eec-09df95559c81',panelIndex:c61cdafd-c0f8-4545-8959-1bf634305a36,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'7edc33c6-bd89-4c5c-ab33-06e0f9dc90a7',w:24,x:0,y:165),id:'4c73e820-3da7-11ec-8eec-09df95559c81',panelIndex:'7edc33c6-bd89-4c5c-ab33-06e0f9dc90a7',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'440d9122-68ef-4b5c-8410-46140bfe9a59',w:24,x:24,y:165),id:'5a0c7a50-3da8-11ec-8eec-09df95559c81',panelIndex:'440d9122-68ef-4b5c-8410-46140bfe9a59',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'5ad10681-3bdf-4435-81d4-bad938ea4df3',w:24,x:0,y:30),id:'9a4cbe40-3dad-11ec-8eec-09df95559c81',panelIndex:'5ad10681-3bdf-4435-81d4-bad938ea4df3',type:visualization,version:'7.15.1')),query:(language:kuery,query:''),tags:!(),timeRestore:!t,title:'Crowdsourced%20Measurements%20Dashboard%20(Median)',viewMode:view)";
        model.addAttribute("classActiveSettingsCrowdsourced", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchSubnetsMedian";
    }

    @GetMapping(value = "/secure/hardwareProbes/averageValues")
    public String elasticsearchProbesAverage(Model model, HttpSession session, HttpServletRequest request) {
        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/dashboards#/view/e537f2f0-0b80-11ea-a8ab-cf1a4435820b?embed=true&_g=(filters:!())&_a=(description:'A%20dashboard%20related%20to%20average%20values%20of%20WiFiMon%20Hardware%20Probe%20Measurements',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:Origin,negate:!f,params:(query:Probe),type:phrase),query:(match_phrase:(Origin:Probe)))),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!f,useMargins:!t),panels:!((embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'1',w:24,x:0,y:0),id:'24fcad00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'1',title:'Average%20Download%20Throughput%20for%20WiFiMon%20Hardware%20Probes%20(per%20Test%20Tool)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'2',w:24,x:24,y:0),id:'2c3d5d80-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'2',title:'Average%20Download%20Throughput%20for%20WiFiMon%20Hardware%20Probes%20(Aggregated%20all%20Test%20Tools)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'3',w:24,x:0,y:15),id:'44643a00-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'3',title:'Average%20Upload%20Throughput%20for%20WiFiMon%20Hardware%20Probes%20(per%20Test%20Tool)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'4',w:24,x:24,y:15),id:'4d2aa020-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'4',title:'Average%20Upload%20Throughput%20for%20WiFiMon%20Hardware%20Probes%20(Aggregated%20all%20Test%20Tools)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'6',w:24,x:24,y:30),id:'3c3558f0-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'6',title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20for%20WiFiMon%20Hardware%20Probes%20(Aggregated%20all%20Test%20Tools)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'7',w:24,x:0,y:45),id:'89692430-0b80-11ea-a8ab-cf1a4435820b',panelIndex:'7',title:'Average%20Bit%20Rate%20for%20WiFiMon%20Hardware%20Probes%20(per%20Probe)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'8',w:24,x:24,y:45),id:a11f1670-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'8',title:'Average%20Link%20Quality%20for%20WiFiMon%20Hardware%20Probes%20(per%20Probe)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'9',w:24,x:0,y:60),id:afb956a0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'9',title:'Average%20Signal%20Level%20for%20WiFiMon%20Hardware%20Probes%20(per%20Probe)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:'10',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'10',title:'Average%20TX%20Power%20for%20WiFiMon%20Hardware%20Probes%20(per%20Probe)',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:(),hidePanelTitles:!f),gridData:(h:15,i:db5049c8-207a-4eeb-a1b2-3d1303f1f5b6,w:24,x:0,y:30),id:fa05d330-145a-11eb-ad81-870789fb8a8b,panelIndex:db5049c8-207a-4eeb-a1b2-3d1303f1f5b6,title:'Average%20HTTP%20Ping%20Round%20Trip%20Time%20for%20WiFiMon%20Hardware%20Probes%20(per%20Test%20Tool)',type:visualization,version:'7.15.1'),(embeddableConfig:(attributes:(description:'A%20table%20describing%20the%20WiFiMon%20Hardware%20Probes%20used%20by%20the%20WiFiMon%20administrator%20and%20includes%20information%20about%20the%20associated%20access%20point%20and%20the%20location%20of%20the%20probe',references:!((id:probes_v0.1,name:indexpattern-datasource-current-indexpattern,type:index-pattern),(id:probes_v0.1,name:indexpattern-datasource-layer-57e9160b-ceb8-4209-94b1-4192fa3908fb,type:index-pattern)),state:(datasourceStates:(indexpattern:(layers:('57e9160b-ceb8-4209-94b1-4192fa3908fb':(columnOrder:!(f86edc2e-1373-4086-a632-8ea2d3173ae3,'7a116979-977c-4be0-847a-4696331503d7','0f20491f-2c72-4434-a9b7-3e3c1621d89e','816a60ad-dee8-40e0-897c-3fcce98fa5ba','3a943357-eb7c-4126-bd79-2f5d77819c3e','45ec9300-9aed-4d55-bb09-c93fb86708c5'),columns:('0f20491f-2c72-4434-a9b7-3e3c1621d89e':(customLabel:!t,dataType:string,isBucketed:!f,label:'Test%20Device%20Location',operationType:last_value,params:(sortField:Timestamp),scale:ordinal,sourceField:Test-Device-Location-Description.keyword),'3a943357-eb7c-4126-bd79-2f5d77819c3e':(customLabel:!t,dataType:number,isBucketed:!f,label:'Signal%20Level%20(Median)',operationType:median,scale:ratio,sourceField:Signal-Level),'45ec9300-9aed-4d55-bb09-c93fb86708c5':(customLabel:!t,dataType:number,isBucketed:!f,label:'Link-Quality%20(Median)',operationType:median,scale:ratio,sourceField:Link-Quality),'7a116979-977c-4be0-847a-4696331503d7':(customLabel:!t,dataType:string,isBucketed:!f,label:'Location%20Name',operationType:last_value,params:(sortField:Timestamp),scale:ordinal,sourceField:Location-Name.keyword),'816a60ad-dee8-40e0-897c-3fcce98fa5ba':(customLabel:!t,dataType:string,isBucketed:!f,label:Accesspoint,operationType:last_value,params:(sortField:Timestamp),scale:ordinal,sourceField:Accesspoint),f86edc2e-1373-4086-a632-8ea2d3173ae3:(customLabel:!t,dataType:string,isBucketed:!t,label:'Probe%20Number',operationType:terms,params:(missingBucket:!f,orderBy:(columnId:'45ec9300-9aed-4d55-bb09-c93fb86708c5',type:column),orderDirection:desc,otherBucket:!t,size:20),scale:ordinal,sourceField:Probe-No)),incompleteColumns:())))),filters:!(),query:(language:lucene,query:''),visualization:(columns:!((alignment:center,columnId:f86edc2e-1373-4086-a632-8ea2d3173ae3,isTransposed:!f),(alignment:center,columnId:'7a116979-977c-4be0-847a-4696331503d7',isTransposed:!f),(alignment:center,columnId:'0f20491f-2c72-4434-a9b7-3e3c1621d89e',isTransposed:!f),(alignment:center,columnId:'816a60ad-dee8-40e0-897c-3fcce98fa5ba',isTransposed:!f),(alignment:center,columnId:'3a943357-eb7c-4126-bd79-2f5d77819c3e',isTransposed:!f),(alignment:center,columnId:'45ec9300-9aed-4d55-bb09-c93fb86708c5',isTransposed:!f)),layerId:'57e9160b-ceb8-4209-94b1-4192fa3908fb',layerType:data)),title:'Probes%20Description%20Table',type:lens,visualizationType:lnsDatatable),enhancements:(),hidePanelTitles:!f),gridData:(h:12,i:'0e87bfc4-2be0-4361-9bf3-083447d686cb',w:48,x:0,y:75),panelIndex:'0e87bfc4-2be0-4361-9bf3-083447d686cb',title:'WiFiMon%20Hardware%20Probes%20Summary%20(Number,%20Location,%20etc.)',type:lens,version:'7.15.1')),query:(language:lucene,query:''),tags:!(),timeRestore:!t,title:'WiFiMon%20Hardware%20Probes%20(Average%20Values)',viewMode:view)";
        model.addAttribute("classActiveSettingsHardwareProbes", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchProbesAverage";
    }

    @GetMapping(value = "/secure/hardwareProbes/medianValues")
    public String elasticsearchProbesMedian(Model model, HttpSession session, HttpServletRequest request) {
        String elasticsearchURL = environment.getProperty(KIBANA_PROTOCOL) + "://" + environment.getProperty(SERVER_HOST_NAME) + ":" + environment.getProperty(KIBANA_PORT) +
		"/app/dashboards#/view/18b98780-3d9b-11ec-8eec-09df95559c81?embed=true&_g=(filters:!())&_a=(description:'A%20dashboard%20related%20to%20median%20values%20of%20WiFiMon%20Hardware%20Probe%20Measurements',filters:!(('$state':(store:appState),meta:(alias:!n,disabled:!f,index:wifimon_v0.1,key:Origin,negate:!f,params:(query:Probe),type:phrase),query:(match_phrase:(Origin:Probe)))),fullScreenMode:!f,options:(hidePanelTitles:!f,syncColors:!f,useMargins:!t),panels:!((embeddableConfig:(enhancements:()),gridData:(h:15,i:e2afc9f5-b5b4-4cce-9eda-0151a650b1ec,w:24,x:0,y:0),id:'41793450-3d90-11ec-8eec-09df95559c81',panelIndex:e2afc9f5-b5b4-4cce-9eda-0151a650b1ec,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:cef119d1-0c71-4c97-b024-8f819a5cb1bd,w:24,x:24,y:0),id:'492dbfd0-3d91-11ec-8eec-09df95559c81',panelIndex:cef119d1-0c71-4c97-b024-8f819a5cb1bd,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'84f4bcd5-86a3-4eba-8797-667e4e076dfb',w:24,x:0,y:15),id:e2b265d0-3d90-11ec-8eec-09df95559c81,panelIndex:'84f4bcd5-86a3-4eba-8797-667e4e076dfb',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'20faf6a7-2413-43f9-9d17-7f3361e36140',w:24,x:24,y:15),id:'8f18c440-3d91-11ec-8eec-09df95559c81',panelIndex:'20faf6a7-2413-43f9-9d17-7f3361e36140',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'80248c7b-9264-4501-aeb5-5267d6219fc9',w:24,x:0,y:30),id:b68e3240-3d90-11ec-8eec-09df95559c81,panelIndex:'80248c7b-9264-4501-aeb5-5267d6219fc9',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:e73e24a7-caca-4c4c-9d34-8b8c1c769a54,w:24,x:24,y:30),id:c95f48e0-3d91-11ec-8eec-09df95559c81,panelIndex:e73e24a7-caca-4c4c-9d34-8b8c1c769a54,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:b30202ec-9b04-4630-b21a-e23ccaaeeb35,w:24,x:0,y:45),id:fd9ba130-3d91-11ec-8eec-09df95559c81,panelIndex:b30202ec-9b04-4630-b21a-e23ccaaeeb35,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:ea64880d-a91b-4a9f-ab6a-5ae4dd9f22ce,w:24,x:24,y:45),id:'81e9de70-3d92-11ec-8eec-09df95559c81',panelIndex:ea64880d-a91b-4a9f-ab6a-5ae4dd9f22ce,type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'5dd32b83-6b60-4572-a1e1-aec0a99e5b62',w:24,x:0,y:60),id:'2020c1e0-3d92-11ec-8eec-09df95559c81',panelIndex:'5dd32b83-6b60-4572-a1e1-aec0a99e5b62',type:visualization,version:'7.15.1'),(embeddableConfig:(enhancements:()),gridData:(h:15,i:'912c8ea5-491d-48f9-9717-3e2185ba8d59',w:24,x:24,y:60),id:bd4bc3c0-0b80-11ea-a8ab-cf1a4435820b,panelIndex:'912c8ea5-491d-48f9-9717-3e2185ba8d59',type:visualization,version:'7.15.1')),query:(language:kuery,query:''),tags:!(),timeRestore:!t,title:'WiFiMon%20Hardware%20Probes%20(Median%20Values)',viewMode:view)";
        model.addAttribute("classActiveSettingsHardwareProbes", CLASS_SETTINGS_STATE);
        model.addAttribute(ELASTICSEARCH_URL, elasticsearchURL);
        return "secure/elasticsearchProbesMedian";
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
