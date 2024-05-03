package dev.linkcentral.presentation.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DefaultViewController {

    @GetMapping("/")
    public String showHome() {
        return "/home";
    }
}