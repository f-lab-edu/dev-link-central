package dev.linkcentral.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FormController {

    @GetMapping("/members/login")
    public String login() {
        return "/members/login";
    }
}