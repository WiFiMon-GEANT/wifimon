package net.geant.wifimon.agent.validator;

        import net.geant.wifimon.agent.model.UserChangePasswordFormModel;
        import net.geant.wifimon.agent.service.UserService;
        import net.geant.wifimon.model.entity.User;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
        import org.springframework.stereotype.Component;
        import org.springframework.validation.Errors;
        import org.springframework.validation.Validator;

@Component
public class UserChangePasswordFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserChangePasswordFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserChangePasswordFormModel.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserChangePasswordFormModel form = (UserChangePasswordFormModel) target;
        validateOldPassword(errors, form);
        validatePasswords(errors, form);
    }

    private void validatePasswords(Errors errors, UserChangePasswordFormModel form) {
        if (!form.getPassword().equals(form.getPasswordRepeated())) {
            errors.rejectValue("passwordRepeated", "password.identical", "Passwords do not match");
        }
       if (form.getOldPassword().equals(form.getPassword())) {
            errors.rejectValue("password", "password.identical", "New password cannot be the same with the previous one");
       }
    }

    private void validateOldPassword(Errors errors, UserChangePasswordFormModel form) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(userEmail).orElse(null);
        boolean passwordMatch = new BCryptPasswordEncoder().matches(form.getOldPassword(), user.getPasswordHash());
        if(!passwordMatch) {
            errors.rejectValue("oldPassword", "password.identical", "Password is not correct");
        }
    }
}
