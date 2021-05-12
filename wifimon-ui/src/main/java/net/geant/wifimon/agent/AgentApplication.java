package net.geant.wifimon.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
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

    private static final Logger loggerAgentApplication = Logger.getLogger(AgentApplication.class.getName());

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AgentApplication.class, args);
        VersionCheckProcess versionCheckProcess = context.getBean(VersionCheckProcess.class);
        String versionCheck = versionCheckProcess.getVersionCheck();
        String vmUrl = versionCheckProcess.getVmUrl();
        if (versionCheck.equals("no")) {
            loggerAgentApplication.info("WiFiMon Version Check is disabled");
        } else if (versionCheck.equals("yes")) {
            String runningVersion = AgentApplication.class.getPackage().getImplementationVersion();
            loggerAgentApplication.info("WiFiMon Running Version: " + runningVersion);
            try {
                InetAddress ipAddress = null;
                try {
                    ipAddress = InetAddress.getLocalHost();
                } catch (Exception e) {
                    loggerAgentApplication.info("Could not find local ip address:" + e.getMessage());
                }
                String targetUrl = vmUrl + "?checkTrigger=startup&version=" + runningVersion + "&ip=" + ipAddress;
                JSONObject json = new JSONObject(IOUtils.toString(new URL(targetUrl), Charset.forName("UTF-8")));
                String mostRecentVersion = json.getString("version");
                loggerAgentApplication.info("WiFiMon Most Recent Version: " + mostRecentVersion);
                if (mostRecentVersion.equals(runningVersion)) {
                    loggerAgentApplication.info("You are running the most recent WiFiMon Version");
                } else {
                    loggerAgentApplication.info("There is a new WiFiMon version available!");
                }
            } catch (Exception e) {
                loggerAgentApplication.info(e.toString());
            }
        }
    }

    @Component
    class VersionCheckProcess {
        @Value("${version.check}")
        private String versionCheck;

        public String getVersionCheck() {
            return versionCheck;
        }

        @Value("${vmUrl}")
        private String vmUrl;

        public String getVmUrl() {
            return vmUrl;
        }
    }
}
