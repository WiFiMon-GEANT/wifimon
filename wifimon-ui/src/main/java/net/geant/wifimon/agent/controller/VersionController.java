package net.geant.wifimon.agent.controller;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

@Controller
public class VersionController {

    private static final String VERSION_CHECK = "version.check";
    private static final String VM_URL = "vmUrl";

    private static final Logger logger = Logger.getLogger(SubnetsController.class.getName());

    @Autowired
    Environment environment;

    @GetMapping("/secure/version")
    public String version(@RequestParam(name = "vers", required = false, defaultValue = "") String name, Model model) {
        String runningVersion = VersionController.class.getPackage().getImplementationVersion();
        String versionCheck = environment.getProperty(VERSION_CHECK);
        String vmUrl = environment.getProperty(VM_URL);

        String latestVersion = "Could not find most recent version";
        try {
            if ("yes".equals(versionCheck)) {
                InetAddress ipAddress = null;
                try {
                    ipAddress = InetAddress.getLocalHost();
                } catch (Exception e) {
                    logger.info("Could not find local ip address:" + e.getMessage());
                }
                String targetUrl = vmUrl + "?checkTrigger=user&version=" + runningVersion + "&ip=" + ipAddress;
                JSONObject json = new JSONObject(IOUtils.toString(new URL(targetUrl), Charset.forName("UTF-8")));
                latestVersion = json.getString("version");
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
        model.addAttribute("runningVersion", runningVersion);
        model.addAttribute("latestVersion", latestVersion);

        return "secure/version";
    }
}
