package net.geant.wifimon.agent.controller.admin.user;

import net.geant.wifimon.agent.model.UserCreateFormModel;
import net.geant.wifimon.agent.service.UserService;
import net.geant.wifimon.agent.validator.UserCreateFormValidator;
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
 * Created by kanakisn on 4/24/16.
 */
@Controller
public class UserController {

    public static final String CREATE_USER_VIEW = "admin/createUser";

    private final UserService userService;
    private final UserCreateFormValidator userCreateFormValidator;

    @Autowired
    public UserController(UserService userService, UserCreateFormValidator userCreateFormValidator) {
        this.userService = userService;
        this.userCreateFormValidator = userCreateFormValidator;
    }

    @InitBinder("userCreateModel")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @RequestMapping("/admin/user/{id}")
    public ModelAndView getUserPage(@PathVariable final Long id) {
        return new ModelAndView("user", "user", userService.getUserById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("User: %s not found", id))));
    }

    @RequestMapping(value = "/admin/user/{id}/delete")
    public String deleteUser(@PathVariable final Long id) {
        userService.delete(id);
        return String.join("/", "redirect:", UsersController.USERS_VIEW);
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.GET)
    public String getUserCreatePage(@ModelAttribute("userCreateModel") final UserCreateFormModel userCreateFormModel) {
        return CREATE_USER_VIEW;
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String handleUserCreateForm(@Valid @ModelAttribute("userCreateModel") final UserCreateFormModel userCreateFormModel,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return CREATE_USER_VIEW;
        userService.create(userCreateFormModel);
        return String.join("/", "redirect:", UsersController.USERS_VIEW);
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return  "active";
    }

}