package net.geant.wifimon.agent.validator;

import net.geant.wifimon.agent.model.VisualOptionsUpdateFormModel;
import net.geant.wifimon.agent.service.VisualOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by kokkinos on 27/6/2017.
 */
@Component
public class VisualOptionsUpdateFormValidator implements Validator {

    private final VisualOptionsService visualOptionsService;

    @Autowired
    public VisualOptionsUpdateFormValidator(VisualOptionsService visualOptionsService) {
        this.visualOptionsService = visualOptionsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(VisualOptionsUpdateFormModel.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
	    VisualOptionsUpdateFormModel form =  (VisualOptionsUpdateFormModel) target;
    }

}
