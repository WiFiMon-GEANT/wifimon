package net.geant.wifimon.agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VersionController {

	@GetMapping("/secure/version")
	public String version(@RequestParam(name = "vers", required = false, defaultValue = "") String name, Model model) {
	    String vers = VersionController.class.getPackage().getImplementationVersion();
	    model.addAttribute("vers", vers);
	    return "secure/version";
	}

}
