package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.StudyGroup;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/study-group")
public class StudyGroupViewController {

    private final MemberService memberService;
    private final StudyGroupService studyGroupService;

    @GetMapping("/")
    public String studyGroupPage(@RequestParam(value = "memberId") Long memberId, Model model) {
        Member currentMember = memberService.getMemberById(memberId);
        model.addAttribute("member", currentMember);

        boolean isStudyGroupCreated = studyGroupService.isStudyGroupCreatedForLeader(memberId);
        model.addAttribute("hasStudyGroup", isStudyGroupCreated);

        if (!isStudyGroupCreated) {
            model.addAttribute("showCreateButton", true);
        } else {
            Long studyGroupId = studyGroupService.findStudyGroupIdByLeaderId(memberId);
            model.addAttribute("studyGroupId", studyGroupId);
        }

        List<StudyGroup> studyGroups = studyGroupService.findStudyGroupsByUserId(memberId);
        model.addAttribute("studyGroups", studyGroups);

        return "studys/study-group";
    }

    @GetMapping("/create")
    public String createStudyGroupForm() {
        return "/studys/create";
    }

}
