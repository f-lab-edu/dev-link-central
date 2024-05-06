package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.service.dto.studygroup.StudyGroupMemberDTO;
import dev.linkcentral.service.facade.StudyGroupFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/study-group")
public class StudyGroupViewController {

    private final StudyGroupFacade studyGroupFacade;

    @GetMapping("/")
    public String showStudyGroupPage(@RequestParam(value = "memberId") Long memberId, Model model) {
        StudyGroupMemberDTO currentMember = studyGroupFacade.getMemberById(memberId);
        model.addAttribute("member", currentMember.getMember());

        boolean isStudyGroupCreated = studyGroupFacade.studyGroupPage(memberId);
        model.addAttribute("hasStudyGroup", isStudyGroupCreated);

        if (!isStudyGroupCreated) {
            model.addAttribute("showCreateButton", true);
        } else {
            Long studyGroupId = studyGroupFacade.findStudyGroupIdByLeaderId(memberId);
            model.addAttribute("studyGroupId", studyGroupId);
        }
        return "studys/study-group";
    }

    @GetMapping("/create")
    public String showCreateStudyGroupForm() {
        return "/studys/create";
    }

}
