package dev.linkcentral.presentation.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/list")
    public String showGroupFeedListPage() {
        return "groupfeed/view";
    }

    @GetMapping("/my-feeds")
    public String showMyFeedListPage() {
        return "groupfeed/my-feeds";
    }

    @GetMapping("/detail/{feedId}")
    public String showGroupFeedDetails(@PathVariable Long feedId, Model model) {
        model.addAttribute("feedId", feedId);
        return "groupfeed/detail";
    }

    @GetMapping("/update/{feedId}")
    public String showUpdateFeedForm(@PathVariable Long feedId, Model model) {
        model.addAttribute("feedId", feedId);
        return "groupfeed/update";
    }
}
