package dev.linkcentral.presentation.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/group-feed")
public class GroupFeedViewController {

    @GetMapping("/create")
    public String showCreateFeedForm() {
        return "groupfeed/save";
    }
}
