package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.service.VisualOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kokkinos on 27/6/2017.
 */

@Controller
public class VisualoptionsController {

    public static final String VO_VIEW = "admin/options";

    private final VisualOptionsService visualOptionsService;

    @Autowired
    public VisualoptionsController(VisualOptionsService visualOptionsService) {
        this.visualOptionsService = visualOptionsService;
    }

    @RequestMapping("/admin/options")
    public ModelAndView getVisualoptionsPage() {
        return new ModelAndView(VO_VIEW, "options", visualOptionsService.getLastVisualOption());
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return "active";
    }
}