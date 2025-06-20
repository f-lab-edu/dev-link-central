package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.service.dto.profile.ProfileDetailsDTO;
import dev.linkcentral.service.facade.ProfileFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 프로필 관련 뷰 컨트롤러
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/profile")
public class ProfileViewController {

    private final ProfileFacade profileFacade;

    /**
     * 프로필 페이지를 보여줍니다.
     *
     * @param memberId 회원 ID (선택 사항)
     * @param model 뷰에 전달할 데이터 모델
     * @return "profile/view" 템플릿 이름
     */
    @GetMapping("/view")
    public String showProfile(@RequestParam(value = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            model.addAttribute("error", "memberId가 제공되지 않았습니다.");
            return "profile/view";
        }

        ProfileDetailsDTO profile = profileFacade.getProfile(memberId);
        model.addAttribute("profile", profile);

        MemberCurrentDTO memberCurrentDTO = profileFacade.getMemberById(memberId);
        if (memberCurrentDTO.getMemberId() != null) {
            model.addAttribute("loggedInUserName", memberCurrentDTO.getName());
            model.addAttribute("viewedMemberId", memberId);
        }
        return "profile/view";
    }

    /**
     * 프로필 수정 폼을 보여줍니다.
     *
     * @param memberId 회원 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return "profile/edit" 템플릿 이름
     */
    @GetMapping("/edit")
    public String showProfileEditForm(@RequestParam Long memberId, Model model) {
        ProfileDetailsDTO profile = profileFacade.getProfile(memberId);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }
}
