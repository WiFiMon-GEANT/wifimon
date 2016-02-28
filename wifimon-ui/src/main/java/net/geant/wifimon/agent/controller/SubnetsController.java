package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.data.Subnet;
import net.geant.wifimon.agent.repository.SubnetRepository;
import net.geant.wifimon.agent.validator.SubnetValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by kanakisn on 27/02/16.
 */

@Controller
public class SubnetsController {

    @Autowired
    SubnetValidator subnetValidator;

    @Autowired
    SubnetRepository subnetRepository;

    @InitBinder("subnet")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(subnetValidator);
//        binder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/secure/subnets")
    public String subnets(@ModelAttribute final Subnet subnet) {
        return "secure/subnets";
    }

    @RequestMapping(value = "/secure/subnets", params = {"save"})
    public String addSubnet(@ModelAttribute @Valid final Subnet subnet,
                            final BindingResult bindingResult,
                            final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "secure/subnets";
        }
        subnetRepository.save(subnet);
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
