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
        VisualOptionsUpdateFormModel form = (VisualOptionsUpdateFormModel) target;
    }

    /*private void validateRadiuslife(Errors errors, VisualOptionsUpdateFormModel form) {
        final String fpRegex1 = "\\d{1}";
        final String fpRegex2 = "\\d{2}";
        *//*try{
            Integer number = Integer.parseInt(form.getRadiuslife().toString());
            if (Pattern.matches(fpRegex1, form.getRadiuslife().toString()) || Pattern.matches(fpRegex2, form.getRadiuslife().toString())) {
                if (form.getRadiuslife() < 1 || form.getRadiuslife() > 24){
                    errors.rejectValue("radiuslife", "radiuslife.wrong", "Radius Life should be an Integer between 1 and 24");
                }
            }else{
                errors.rejectValue("radiuslife", "radiuslife.wrong", "Radius Life should be an Integer between 1 and 24");
            }
        }catch (NumberFormatException|NullPointerException ex) {
            System.out.println("Inserted Radius Life value different than Integer");
            errors.rejectValue("radiuslife", "radiuslife.notnumber", "Radius Life is not an Integer");
        }*//*
        if (Pattern.matches(fpRegex1, form.getRadiuslife().toString()) || Pattern.matches(fpRegex2, form.getRadiuslife().toString())) {
            if (form.getRadiuslife() < 1 || form.getRadiuslife() > 24){
                errors.rejectValue("radiuslife", "radiuslife.wrong", "Radius Life should be an Integer between 1 and 24");
            }
        }else{
            errors.rejectValue("radiuslife", "radiuslife.wrong", "Radius Life should be an Integer between 1 and 24");
        }
    }*/
}
