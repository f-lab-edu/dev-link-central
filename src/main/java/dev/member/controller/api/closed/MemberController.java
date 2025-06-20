package dev.member.controller.api.closed;

import dev.common.dto.ApiResponse;
import dev.member.controller.request.DeleteMemberRequest;
import dev.member.controller.request.UpdateMemberRequest;
import dev.member.service.MemberAuthService;
import dev.member.service.MemberService;
import dev.member.service.dto.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberAuthService memberAuthService;

    @GetMapping
    public ResponseEntity<ApiResponse<MemberInfoResponse>> info() {
        return ResponseEntity.ok(ApiResponse.createApiResponse(memberAuthService.info()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> update(
            @Validated @RequestBody UpdateMemberRequest updateRequest
    ) {
        memberService.update(updateRequest);
        return ResponseEntity.ok(ApiResponse.createEmptyApiResponse());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @Validated @RequestBody DeleteMemberRequest deleteRequest
    ) {
        memberService.softDelete(deleteRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.createEmptyApiResponse());
    }
}
