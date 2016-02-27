package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.data.Subnet;
import net.geant.wifimon.agent.repository.SubnetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by kanakisn on 27/02/16.
 */

@Controller
public class SubnetsController {

    @Autowired
    SubnetRepository subnetRepository;

    @RequestMapping(value = "/secure/subnets")
    public String subnets(final Subnet s) {
        return "secure/subnets";
    }

    @RequestMapping(value = "/secure/subnets", params = {"save"})
    public String addSubnet(final Subnet s, final BindingResult bindingResult, final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "secure/subnets";
        }
        subnetRepository.save(s);
        model.clear();
        return "redirect:/secure/subnets";
    }

    @ModelAttribute("subnets")
    public List populateSubnets() {
        return subnetRepository.findAll();
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateActiveHeader() {
        return "active";
    }

}
