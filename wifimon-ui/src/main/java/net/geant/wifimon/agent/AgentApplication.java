package net.geant.wifimon.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import java.util.logging.Logger;
import org.json.*;
import java.net.URL;
import org.apache.commons.io.*;
import java.nio.charset.Charset;

/**
 * Created by kanakisn on 11/17/15.
 */
@SpringBootApplication
@EntityScan(basePackages = AgentApplication.BASE_PACKAGE)
public class AgentApplication extends SpringBootServletInitializer {
    public static final String BASE_PACKAGE = "net.geant.wifimon";

    private static Logger loggerAgentApplication = Logger.getLogger(AgentApplication.class.getName());

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AgentApplication.class, args);
	TrackingProcess trackingProcess = context.getBean(TrackingProcess.class);
	String userTracking = trackingProcess.getUserTracking();
	String vmUrl = trackingProcess.getVmUrl();
	if (userTracking.equals("no")) {
		loggerAgentApplication.info("WiFiMon User Tracking is disabled");
	} else if (userTracking.equals("yes")) {
		String runningVersion = AgentApplication.class.getPackage().getImplementationVersion();
		loggerAgentApplication.info("WiFiMon Running Version: " + runningVersion);
		try {
			JSONObject json = new JSONObject(IOUtils.toString(new URL(vmUrl), Charset.forName("UTF-8")));
			String mostRecentVersion = json.getString("version");
			loggerAgentApplication.info("WiFiMon Most Recent Version: " + mostRecentVersion);
			if (mostRecentVersion == runningVersion) {
				loggerAgentApplication.info("You are running the most recent WiFiMon Version");
			} else {
				loggerAgentApplication.info("There is a new WiFiMon version available!");
			}
		} catch(Exception e) {
			loggerAgentApplication.info(e.toString());
		}
	}
    }

    @Component
    class TrackingProcess {
	    @Value("${user.tracking}")
	    private String userTracking;

	    public String getUserTracking() {
		    return userTracking;
	    }

	    @Value("${vmUrl}")
	    private String vmUrl;

	    public String getVmUrl() {
		    return vmUrl;
	    }
    }
}
