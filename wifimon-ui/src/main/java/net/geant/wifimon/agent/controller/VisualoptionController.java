package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.model.VisualOptionsUpdateFormModel;
import net.geant.wifimon.agent.repository.VisualOptionsRepository;
import net.geant.wifimon.agent.service.VisualOptionsService;
import net.geant.wifimon.agent.validator.VisualOptionsUpdateFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by kokkinos on 27/6/2017.
 */
@Controller
public class VisualoptionController {

    public static final String EDIT_VO_VIEW = "admin/editOptions";

    private final VisualOptionsService visualOptionsService;
    private final VisualOptionsUpdateFormValidator visualOptionsUpdateFormValidator;

    @Autowired
    public VisualoptionController(VisualOptionsService visualOptionsService, VisualOptionsUpdateFormValidator visualOptionsUpdateFormValidator) {
        this.visualOptionsService = visualOptionsService;
        this.visualOptionsUpdateFormValidator = visualOptionsUpdateFormValidator;
    }

    @Autowired
    VisualOptionsRepository visualOptionsRepository;

    @InitBinder("visualOptionsEditModel")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(visualOptionsUpdateFormValidator);
    }


    @RequestMapping(value = "/admin/editOptions", method = RequestMethod.GET)
    public String getVisualoptionsCreatePage(@ModelAttribute("visualOptionsEditModel") final VisualOptionsUpdateFormModel visualOptionsUpdateFormModel) {
        Integer count = visualOptionsRepository.countEntries();
        if (count > 0) {
            visualOptionsUpdateFormModel.setUserdata(visualOptionsRepository.getLastEntry().getUserdata());
            visualOptionsUpdateFormModel.setCorrelationmethod(visualOptionsRepository.getLastEntry().getCorrelationmethod());
            visualOptionsUpdateFormModel.setUservisualoption(visualOptionsRepository.getLastEntry().getUservisualoption());
        }
        return EDIT_VO_VIEW;
    }

    @RequestMapping(value = "/admin/editOptions", method = RequestMethod.POST)
    public String handleVisualoptionsCreateForm(@Valid @ModelAttribute("visualOptionsEditModel") final VisualOptionsUpdateFormModel visualOptionsUpdateFormModel,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return EDIT_VO_VIEW;
        Integer count = visualOptionsRepository.countEntries();
        if (count == 0) {
            visualOptionsService.create(visualOptionsUpdateFormModel);
        } else {
            Integer i = visualOptionsRepository.updateEntry(visualOptionsUpdateFormModel.getUserdata().toString(),
                    visualOptionsUpdateFormModel.getUservisualoption().toString(),
                    visualOptionsUpdateFormModel.getCorrelationmethod().toString());
        }
        return String.join("/", "redirect:", VisualoptionsController.VO_VIEW);
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return "active";
    }
}
