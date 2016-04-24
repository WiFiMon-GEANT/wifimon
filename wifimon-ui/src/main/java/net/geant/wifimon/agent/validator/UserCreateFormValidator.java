package net.geant.wifimon.agent.validator;

import net.geant.wifimon.agent.model.UserCreateFormModel;
import net.geant.wifimon.agent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by kanakisn on 4/24/16.
 */
@Component
public class UserCreateFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserCreateFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserCreateFormModel.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateFormModel form = (UserCreateFormModel) target;
        validatePasswords(errors, form);
        validateEmail(errors, form);
    }

    private void validatePasswords(Errors errors, UserCreateFormModel form) {
        if (!form.getPassword().equals(form.getPasswordRepeated())) {
            errors.reject("passwordRepeated", "Passwords do not match");
        }
    }

    private void validateEmail(Errors errors, UserCreateFormModel form) {
        if (userService.getUserByEmail(form.getEmail()).isPresent()) {
            errors.reject("email", "User with this email already exists");
        }
    }

}