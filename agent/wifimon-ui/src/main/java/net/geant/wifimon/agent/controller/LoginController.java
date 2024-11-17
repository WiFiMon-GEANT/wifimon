package net.geant.wifimon.agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by kanakisn on 11/17/15.
 */
@Controller
public class LoginController {

    @GetMapping(value = "/")
    public String afterLogin(Model model) {

        return "redirect:/secure/overview";
    }
}
