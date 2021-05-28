package net.geant.wifimon.agent.controller.admin.user;

import net.geant.wifimon.agent.model.UserChangePasswordFormModel;
import net.geant.wifimon.agent.model.UserCreateFormModel;
import net.geant.wifimon.agent.service.UserService;
import net.geant.wifimon.agent.validator.UserChangePasswordFormValidator;
import net.geant.wifimon.agent.validator.UserCreateFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.NoSuchElementException;

/**
 * Created by kanakisn on 4/24/16.
 */
@Controller
public class UserController {

    public static final String CREATE_USER_VIEW = "admin/createUser";
    public static final String CHANGE_PASS_VIEW = "admin/changePassword";

    private final UserService userService;
    private final UserCreateFormValidator userCreateFormValidator;
    private final UserChangePasswordFormValidator userChangePasswordFormValidator;

    @Autowired
    public UserController(UserService userService, UserCreateFormValidator userCreateFormValidator, UserChangePasswordFormValidator userChangePasswordFormValidator) {
        this.userService = userService;
        this.userCreateFormValidator = userCreateFormValidator;
        this.userChangePasswordFormValidator = userChangePasswordFormValidator;
    }

    @InitBinder("userCreateModel")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @InitBinder("userChangePasswordModel")
    public void initBinderPassword(WebDataBinder binder) {
        binder.addValidators(userChangePasswordFormValidator);
    }

    @GetMapping("/admin/user/{id}")
    public ModelAndView getUserPage(@PathVariable final Long id) {
        return new ModelAndView("user", "user", userService.getUserById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("User: %s not found", id))));
    }

    @GetMapping(value = "/admin/user/{id}/delete")
    public String deleteUser(@PathVariable final Long id) {
        userService.delete(id);
        return String.join("/", "redirect:", UsersController.USERS_VIEW);
    }

    @GetMapping(value = "/admin/user/create")
    public String getUserCreatePage(@ModelAttribute("userCreateModel") final UserCreateFormModel userCreateFormModel) {
        return CREATE_USER_VIEW;
    }

    @PostMapping(value = "/admin/user/create")
    public String handleUserCreateForm(@Valid @ModelAttribute("userCreateModel") final UserCreateFormModel userCreateFormModel,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return CREATE_USER_VIEW;
        userService.create(userCreateFormModel);
        return String.join("/", "redirect:", UsersController.USERS_VIEW);
    }

    @GetMapping(value = "/admin/user/changePassword")
    public String getUserChangePasswordPage(@ModelAttribute("userChangePasswordModel") final UserChangePasswordFormModel userChangePasswordFormModel) {
        return CHANGE_PASS_VIEW;
    }

    @PostMapping(value = "/admin/user/changePassword")
    public String handleUserChangePasswordForm(@Valid @ModelAttribute("userChangePasswordModel") final UserChangePasswordFormModel userChangePasswordFormModel, BindingResult bindingResult, Model model) {
               if (bindingResult.hasErrors()) {
                   return CHANGE_PASS_VIEW;
               }
               userService.changePassword(userChangePasswordFormModel);
               model.addAttribute("modalDisplay", "block");
               return CHANGE_PASS_VIEW;
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return "active";
    }
}
