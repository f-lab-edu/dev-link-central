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
     * 멤버 정보를 가져옵니다.
     *
     * @return 멤버 정보 응답
     */
    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo() {
        MemberInfoDTO memberInfoDTO = memberFacade.getMemberInfo();
        MemberInfoResponse response = MemberInfoResponse.toMemberInfoResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 멤버 정보를 업데이트합니다.
     *
     * @param editRequest 멤버 업데이트 요청
     * @return 멤버 업데이트 응답
     */
    @PutMapping
    public ResponseEntity<MemberEditedResponse> updateMember(@Validated @RequestBody MemberEditRequest editRequest) {
        MemberEditDTO memberEditDTO = MemberEditRequest.toMemberEditCommand(editRequest);
        memberFacade.updateMember(memberEditDTO);
        MemberEditedResponse response = MemberEditedResponse.toUpdateMemberResponse();
        return ResponseEntity.ok(response);
    }

    /**
     * 멤버를 소프트 삭제합니다.
     *
     * @param deleteRequest 멤버 삭제 요청
     * @return 멤버 삭제 응답
     */
    @DeleteMapping
    public ResponseEntity<MemberDeletedResponse> softDeleteMember(@Validated @RequestBody MemberDeleteRequest deleteRequest) {
        MemberDeleteRequestDTO memberDeleteRequestDTO = MemberDeleteRequest.toMemberDeleteRequestCommand(deleteRequest);
        boolean softDeleteMember = memberFacade.softDeleteMember(memberDeleteRequestDTO);
        MemberDeletedResponse response = MemberDeletedResponse.toSoftDeleteMemberResponse(softDeleteMember);
        return ResponseEntity.ok(response);
    }
}
