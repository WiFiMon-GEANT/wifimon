package net.geant.wifimon.agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class VersionController {

	private static final String USER_TRACKING = "user.tracking";
	private static final String VM_URL = "vmUrl";

	@Autowired
	Environment environment;

	@GetMapping("/secure/version")
	public String version(@RequestParam(name = "vers", required = false, defaultValue = "") String name, Model model) {
	    String vers = VersionController.class.getPackage().getImplementationVersion();
	    model.addAttribute("vers", vers);
	    String userTracking = environment.getProperty(USER_TRACKING);
	    model.addAttribute("user_tracking", userTracking);
	    String vmUrl = environment.getProperty(VM_URL);
	    model.addAttribute("vmUrl", vmUrl);
	    return "secure/version";
	}

}
