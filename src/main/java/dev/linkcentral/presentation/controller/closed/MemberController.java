package dev.linkcentral.presentation.controller.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.request.MemberEditRequestDTO;
import dev.linkcentral.service.dto.response.MemberEditResponseDTO;
import dev.linkcentral.service.dto.response.MemberPasswordResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/edit-form")
    public ResponseEntity<?> editForm(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        String editFormUrl = "/members/edit";
        return ResponseEntity.ok(Collections.singletonMap("url", editFormUrl));
    }

    @ResponseBody
    @PostMapping("/check-current-password")
    public MemberPasswordResponseDTO checkPassword(@RequestParam String password) {
        Member member = memberService.getCurrentMember();
        boolean result = memberService.validatePassword(member.getNickname(), password);
        return new MemberPasswordResponseDTO(result);
    }

    @ResponseBody
    @PutMapping("/edit")
    public MemberEditResponseDTO memberUpdate(@ModelAttribute MemberEditRequestDTO memberEditDTO) {
        memberService.editMember(memberEditDTO);
        return new MemberEditResponseDTO(HttpStatus.OK.value());
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> softDeleteMember(@RequestParam String password) {
        Member member = memberService.getCurrentMember();
        boolean result = memberService.removeMember(member.getNickname(), password);

        if (result) {
            return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
        }
        return ResponseEntity.badRequest().body("회원 탈퇴 처리 중 오류가 발생했습니다.");
    }
}