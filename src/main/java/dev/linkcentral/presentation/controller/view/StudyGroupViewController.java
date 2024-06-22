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

/**
 * 스터디 그룹 관련 뷰 컨트롤러
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/study-group")
public class StudyGroupViewController {

    private final StudyGroupFacade studyGroupFacade;

    /**
     * 스터디 그룹 페이지를 보여줍니다.
     *
     * @param memberId 회원 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return "studys/study-group" 템플릿 이름
     */
    @GetMapping("/")
    public String showStudyGroupPage(@RequestParam(value = "memberId") Long memberId, Model model) {
        StudyGroupMemberDTO currentMember = studyGroupFacade.getMemberById(memberId);
        model.addAttribute("member", currentMember.getMember());
        return "studys/study-group";
    }

    /**
     * 스터디 그룹 생성 폼을 보여줍니다.
     *
     * @return "/studys/create" 템플릿 이름
     */
    @GetMapping("/create")
    public String showCreateStudyGroupForm() {
        return "/studys/create";
    }
}
