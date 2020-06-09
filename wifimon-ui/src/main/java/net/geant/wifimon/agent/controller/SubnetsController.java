package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.repository.SubnetRepository;
import net.geant.wifimon.agent.validator.SubnetDtoValidator;
import net.geant.wifimon.model.dto.SubnetDto;
import net.geant.wifimon.model.entity.Subnet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by kanakisn on 27/02/16.
 */

@Controller
public class SubnetsController {

    private static Logger logger = Logger.getLogger(SubnetsController.class.getName());

    @Autowired
    SubnetDtoValidator subnetDtoValidator;

    @Autowired
    SubnetRepository subnetRepository;

    @InitBinder("sub")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(subnetDtoValidator);
    }

    @GetMapping(value = "/admin/subnets")
    public String subnets(@ModelAttribute("sub") final SubnetDto sub) {
        return "admin/subnets";
    }

    @PostMapping(value = "/admin/subnets")
    public String addSubnet(@ModelAttribute("sub") @Valid final SubnetDto sub,
                            final BindingResult bindingResult,
                            final ModelMap model) {
        if (bindingResult.hasErrors()) {
            return "admin/subnets";
        }
        Subnet subnet = new Subnet();
        subnet.setSubnet(sub.getSubnet());
        subnetRepository.save(subnet);
        model.clear();
        return "redirect:/admin/subnets";
    }

    @GetMapping(value = "/admin/subnets/delete/{id}")
    public String deleteSubnet(@PathVariable final String id) {
        try {
            subnetRepository.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        return "redirect:/admin/subnets";
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
