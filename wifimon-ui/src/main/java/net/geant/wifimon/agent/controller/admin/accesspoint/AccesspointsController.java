package net.geant.wifimon.agent.controller.admin.accesspoint;

import net.geant.wifimon.agent.service.AccesspointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kokkinos on 8/3/2017.
 */

@Controller
public class AccesspointsController {

    public static final String AP_VIEW = "admin/accesspoints";

    private final AccesspointService accesspointService;

    @Autowired
    public AccesspointsController(AccesspointService accesspointService) {
        this.accesspointService = accesspointService;
    }

    @RequestMapping("/admin/accesspoints")
    public ModelAndView getAccesspointsPage() {
        return new ModelAndView(AP_VIEW, "accesspoints", accesspointService.getAllAccesspoints());
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return "active";
    }
}
