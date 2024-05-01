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
import dev.linkcentral.service.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberMapper memberMapper;
    private final MemberFacade memberFacade;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getUserInfo() {
        MemberInfoDTO memberInfoDTO = memberFacade.getUserInfo();
        MemberInfoResponse response = memberMapper.toMemberInfoResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MemberEditResponse> updateMember(@RequestBody MemberEditRequest request) {
        MemberEditDTO memberEditDTO = memberMapper.toMemberEditCommand(request);
        memberFacade.updateMember(memberEditDTO);
        MemberEditResponse response = memberMapper.toupdateMemberResponse();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<MemberDeleteResponse> softDeleteMember(@RequestBody MemberDeleteRequest request) {
        MemberDeleteRequestDTO memberDeleteRequestDTO = memberMapper.toMemberDeleteRequestCommand(request);
        boolean softDeleteMember = memberFacade.softDeleteMember(memberDeleteRequestDTO);
        MemberDeleteResponse response = memberMapper.toSoftDeleteMemberResponse(softDeleteMember);
        return ResponseEntity.ok(response);
    }

}
