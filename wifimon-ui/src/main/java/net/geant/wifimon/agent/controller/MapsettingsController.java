package net.geant.wifimon.agent.controller;

import net.geant.wifimon.agent.model.MapSettingsUpdateFormModel;
import net.geant.wifimon.agent.repository.MapSettingsRepository;
import net.geant.wifimon.agent.service.MapSettingsService;
import net.geant.wifimon.agent.validator.MapSettingsUpdateFormValidator;
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
 * Created by kokkinos on 3/7/2017.
 */

@Controller
public class MapsettingsController {

    public static final String EDIT_MS_VIEW = "secure/editMapSettings";

    private final MapSettingsService mapSettingsService;
    private final MapSettingsUpdateFormValidator mapSettingsUpdateFormValidator;

    @Autowired
    public MapsettingsController(MapSettingsService mapSettingsService, MapSettingsUpdateFormValidator mapSettingsUpdateFormValidator) {
        this.mapSettingsService = mapSettingsService;
        this.mapSettingsUpdateFormValidator = mapSettingsUpdateFormValidator;
    }

    @Autowired
    MapSettingsRepository mapSettingsRepository;

    @InitBinder("mapSettingsEditModel")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(mapSettingsUpdateFormValidator);
    }


    @RequestMapping(value = "/secure/editMapSettings", method = RequestMethod.GET)
    public String getMapsettingsCreatePage(@ModelAttribute("mapSettingsEditModel") final MapSettingsUpdateFormModel mapSettingsUpdateFormModel) {
        Integer count  = mapSettingsRepository.countEntries();
        if (count > 0){
            mapSettingsUpdateFormModel.setMapzoom(mapSettingsRepository.getLastEntry().getMapzoom());
            mapSettingsUpdateFormModel.setMaplatitude(mapSettingsRepository.getLastEntry().getMaplatitude());
            mapSettingsUpdateFormModel.setMaplongitude(mapSettingsRepository.getLastEntry().getMaplongitude());
        }
        return EDIT_MS_VIEW;
    }

    @RequestMapping(value = "/secure/editMapSettings", method = RequestMethod.POST)
    public String handleMapsettingsCreateForm(@Valid @ModelAttribute("mapSettingsEditModel") final MapSettingsUpdateFormModel mapSettingsUpdateFormModel,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return EDIT_MS_VIEW;
        Integer count  = mapSettingsRepository.countEntries();
        if (count == 0){
            mapSettingsService.create(mapSettingsUpdateFormModel);
        }else{
            Integer i = mapSettingsRepository.updateEntry(mapSettingsUpdateFormModel.getMapzoom(), mapSettingsUpdateFormModel.getMaplatitude(), mapSettingsUpdateFormModel.getMaplongitude());
        }
        return String.join("/", "redirect:", "secure/map");
    }

    @ModelAttribute("classActiveSettingsMap")
    public String populateCssClass() {
        return  "active";
    }


}
