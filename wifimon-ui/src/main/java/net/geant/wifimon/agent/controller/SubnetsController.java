package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.data.Subnet;
import net.geant.wifimon.agent.repository.SubnetRepository;
import net.geant.wifimon.agent.validator.SubnetValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    @InitBinder("sub")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(subnetValidator);
//        binder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/secure/subnets")
    public String subnets(@ModelAttribute("sub") final Subnet sub) {
        return "secure/subnets";
    }

    @RequestMapping(value = "/secure/subnets", params = {"save"})
    public String addSubnet(@ModelAttribute("sub") @Valid final Subnet sub,
                            final BindingResult bindingResult,
                            final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "secure/subnets";
        }
        subnetRepository.save(sub);
        model.clear();
        return "redirect:/secure/subnets";
    }

    @RequestMapping(value = "/secure/subnets/delete/{id}")
    public String deleteSubnet(@PathVariable final String id, final BindingResult bindingResult, final ModelMap model) {
        subnetRepository.delete(Long.valueOf(id));
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
