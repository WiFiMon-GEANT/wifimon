package net.geant.wifimon.agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

// added by me
import net.geant.wifimon.model.entity.User;


/**
 * Created by kanakisn on 11/17/15.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/")
    public String afterLogin(Model model) {

	User user = new User();

        return "redirect:/secure/overview";
    }
    
}
