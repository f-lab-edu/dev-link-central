package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.service.dto.member.MemberEditFormDTO;
import dev.linkcentral.service.facade.MemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 회원 관련 뷰 컨트롤러
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/member")
public class MemberViewController {

    private final MemberFacade memberFacade;

    /**
     * 홈 페이지를 보여줍니다.
     *
     * @return "/home" 템플릿 이름
     */
    @GetMapping("/")
    public String showHomePage() {
        return "/home";
    }

    /**
     * 로그인 페이지를 보여줍니다.
     *
     * @return "/members/login" 템플릿 이름
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "/members/login";
    }

    /**
     * 회원 가입 폼을 보여줍니다.
     *
     * @return "/members/join" 템플릿 이름
     */
    @GetMapping("/join-form")
    public String showJoinForm() {
        return "/members/join";
    }

    /**
     * 비밀번호 재설정 폼을 보여줍니다.
     *
     * @return "/members/reset-password" 템플릿 이름
     */
    @GetMapping("reset-password")
    public String showResetPasswordForm() {
        return "/members/reset-password";
    }

    /**
     * 회원 정보 수정 폼을 보여줍니다.
     *
     * @param model 뷰에 전달할 데이터 모델
     * @return "/members/edit" 템플릿 이름 또는 "redirect:/login" 리다이렉트 URL
     */
    @GetMapping("/edit-form")
    public String showMemberEditForm(Model model) {
        MemberEditFormDTO memberDTO = memberFacade.memberEditForm();
        if (memberDTO.getMember() == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", memberDTO.getMember());
        return "/members/edit";
    }

    /**
     * 회원 삭제 페이지를 보여줍니다.
     *
     * @return "/members/delete" 템플릿 이름
     */
    @GetMapping("/delete-page")
    public String showDeleteMemberPage() {
        return "/members/delete";
    }
}
