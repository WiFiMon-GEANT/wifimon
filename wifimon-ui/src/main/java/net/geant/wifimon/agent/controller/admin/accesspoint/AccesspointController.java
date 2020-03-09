package net.geant.wifimon.agent.controller.admin.accesspoint;

import net.geant.wifimon.agent.model.AccesspointCreateFormModel;
import net.geant.wifimon.agent.service.AccesspointService;
import net.geant.wifimon.agent.validator.AccesspointCreateFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.NoSuchElementException;

/**
 * Created by kokkinos on 8/3/2017.
 */
@Controller
public class AccesspointController {

    public static final String CREATE_AP_VIEW = "admin/createAccesspoint";

    private final AccesspointService accesspointService;
    private final AccesspointCreateFormValidator accesspointCreateFormValidator;

    @Autowired
    public AccesspointController(AccesspointService accesspointService, AccesspointCreateFormValidator accesspointCreateFormValidator) {
        //public AccesspointController(AccesspointService accesspointService) {
        this.accesspointService = accesspointService;
        this.accesspointCreateFormValidator = accesspointCreateFormValidator;
    }

    @InitBinder("accesspointCreateModel")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(accesspointCreateFormValidator);
    }

    @RequestMapping("/admin/accesspoint/{apid}")
    public ModelAndView getAccesspointPage(@PathVariable final Long apid) {
        return new ModelAndView("accesspoint", "accesspoint", accesspointService.getAccesspointByApid(apid)
                .orElseThrow(() -> new NoSuchElementException(String.format("AP: %s not found", apid))));
    }

    @RequestMapping(value = "/admin/accesspoint/{apid}/delete")
    public String deleteAccesspoint(@PathVariable final Long apid) {
        accesspointService.delete(apid);
        return String.join("/", "redirect:", AccesspointsController.AP_VIEW);
    }

    @RequestMapping(value = "/admin/accesspoint/create", method = RequestMethod.GET)
    public String getAccesspointCreatePage(@ModelAttribute("accesspointCreateModel") final AccesspointCreateFormModel accesspointCreateFormModel) {
        return CREATE_AP_VIEW;
    }

    @RequestMapping(value = "/admin/accesspoint/create", method = RequestMethod.POST)
    public String handleAccesspointCreateForm(@Valid @ModelAttribute("accesspointCreateModel") final AccesspointCreateFormModel accesspointCreateFormModel,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return CREATE_AP_VIEW;
        accesspointService.create(accesspointCreateFormModel);
        return String.join("/", "redirect:", AccesspointsController.AP_VIEW);
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return "active";
    }
}
