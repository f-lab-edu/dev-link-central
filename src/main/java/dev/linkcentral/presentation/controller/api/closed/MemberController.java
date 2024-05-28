package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.member.MemberDeleteRequest;
import dev.linkcentral.presentation.request.member.MemberEditRequest;
import dev.linkcentral.presentation.response.member.MemberDeleteResponse;
import dev.linkcentral.presentation.response.member.MemberEditResponse;
import dev.linkcentral.presentation.response.member.MemberInfoResponse;
import dev.linkcentral.service.dto.member.MemberDeleteRequestDTO;
import dev.linkcentral.service.dto.member.MemberEditDTO;
import dev.linkcentral.service.dto.member.MemberInfoDTO;
import dev.linkcentral.service.facade.MemberFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberFacade memberFacade;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo() {
        MemberInfoDTO memberInfoDTO = memberFacade.getMemberInfo();
        MemberInfoResponse response = MemberInfoResponse.toMemberInfoResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MemberEditResponse> updateMember(@Validated @RequestBody MemberEditRequest editRequest) {
        MemberEditDTO memberEditDTO = MemberEditRequest.toMemberEditCommand(editRequest);
        memberFacade.updateMember(memberEditDTO);
        MemberEditResponse response = MemberEditResponse.toUpdateMemberResponse();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<MemberDeleteResponse> softDeleteMember(@Validated @RequestBody MemberDeleteRequest deleteRequest) {
        MemberDeleteRequestDTO memberDeleteRequestDTO = MemberDeleteRequest.toMemberDeleteRequestCommand(deleteRequest);
        boolean softDeleteMember = memberFacade.softDeleteMember(memberDeleteRequestDTO);
        MemberDeleteResponse response = MemberDeleteResponse.toSoftDeleteMemberResponse(softDeleteMember);
        return ResponseEntity.ok(response);
    }
}
