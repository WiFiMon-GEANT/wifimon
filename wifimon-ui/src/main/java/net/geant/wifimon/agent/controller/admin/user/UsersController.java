package net.geant.wifimon.agent.controller.admin.user;

import net.geant.wifimon.agent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kanakisn on 4/24/16.
 */
@Controller
public class UsersController {

    public static final String USERS_VIEW = "admin/users";

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/admin/users")
    @Secured("ROLE_ADMIN")
    public ModelAndView getUsersPage() {
        return new ModelAndView(USERS_VIEW, "users", userService.getAllUsers());
    }

    @ModelAttribute("classActiveSettingsConfig")
    public String populateCssClass() {
        return "active";
    }
}