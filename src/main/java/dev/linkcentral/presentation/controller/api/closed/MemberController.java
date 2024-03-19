package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.presentation.dto.request.MemberEditRequest;
import dev.linkcentral.presentation.dto.response.MemberEditResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 친구 목록 API
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        log.info("컨트롤러 들어왔는가???????");
        Member member = memberService.getCurrentMember();
        try {
            return ResponseEntity.ok(memberService.getCurrentUserInfo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public MemberEditResponse memberUpdate(@ModelAttribute MemberEditRequest memberEditDTO) {
        memberService.editMember(memberEditDTO);
        return new MemberEditResponse(HttpStatus.OK.value());
    }

    @DeleteMapping
    public ResponseEntity<String> softDeleteMember(@RequestParam String password) {
        Member member = memberService.getCurrentMember();
        boolean result = memberService.removeMember(member.getNickname(), password);

        if (result) {
            return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
        }
        return ResponseEntity.badRequest().body("회원 탈퇴 처리 중 오류가 발생했습니다.");
    }
}