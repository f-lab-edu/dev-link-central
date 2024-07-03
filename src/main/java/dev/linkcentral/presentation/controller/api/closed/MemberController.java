package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.member.MemberDeleteRequest;
import dev.linkcentral.presentation.request.member.MemberEditRequest;
import dev.linkcentral.presentation.response.member.MemberDeletedResponse;
import dev.linkcentral.presentation.response.member.MemberEditedResponse;
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

    /**
     * 현재 회원의 정보를 반환합니다.
     *
     * @return 회원 정보 응답
     */
    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo() {
        MemberInfoDTO memberInfoDTO = memberFacade.getMemberInfo();
        MemberInfoResponse response = MemberInfoResponse.toMemberInfoResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원 정보를 업데이트합니다.
     *
     * @param editRequest 회원 수정 요청
     * @return 회원 수정 응답
     */
    @PutMapping
    public ResponseEntity<MemberEditedResponse> updateMember(@Validated @RequestBody MemberEditRequest editRequest) {
        MemberEditDTO memberEditDTO = MemberEditRequest.toMemberEditCommand(editRequest);
        memberFacade.updateMember(memberEditDTO);
        MemberEditedResponse response = MemberEditedResponse.toUpdateMemberResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * 회원을 소프트 삭제합니다.
     *
     * @param deleteRequest 회원 삭제 요청
     * @return 회원 삭제 응답
     */
    @DeleteMapping
    public ResponseEntity<MemberDeletedResponse> softDeleteMember(@Validated @RequestBody MemberDeleteRequest deleteRequest) {
        MemberDeleteRequestDTO memberDeleteRequestDTO = MemberDeleteRequest.toMemberDeleteRequestCommand(deleteRequest);
        boolean softDeleteMember = memberFacade.softDeleteMember(memberDeleteRequestDTO);
        MemberDeletedResponse response = MemberDeletedResponse.toSoftDeleteMemberResponse(softDeleteMember);
        return ResponseEntity.ok(response);
    }
}
