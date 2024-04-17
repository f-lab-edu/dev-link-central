package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.mapper.MemberMapper;
import dev.linkcentral.presentation.dto.MemberEditDTO;
import dev.linkcentral.presentation.dto.request.MemberEditRequest;
import dev.linkcentral.presentation.dto.response.MemberEditResponse;
import dev.linkcentral.service.MemberService;
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
    private final MemberMapper memberMapper;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        Member member = memberService.getCurrentMember();
        try {
            return ResponseEntity.ok(memberService.getCurrentUserInfo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping
    public ResponseEntity<MemberEditResponse> memberUpdate(@RequestBody MemberEditRequest request) {
        MemberEditDTO memberEditDTO = memberMapper.toMemberEditDTO(request);
        memberService.editMember(memberEditDTO);
        return ResponseEntity.ok(new MemberEditResponse(200, "Update successful"));
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
