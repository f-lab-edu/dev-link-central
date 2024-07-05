package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.studygroup.StudyGroupCreateRequest;
import dev.linkcentral.presentation.response.studygroup.*;
import dev.linkcentral.service.dto.studygroup.*;
import dev.linkcentral.service.facade.StudyGroupFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/study-group")
public class StudyGroupController {

    private final StudyGroupFacade studyGroupFacade;

    /**
     * 현재 사용자의 스터디 그룹 ID 목록을 가져옵니다.
     *
     * @return 스터디 그룹 ID 목록 응답
     */
    @GetMapping("/my-groups/ids")
    public ResponseEntity<StudyGroupIdsResponse> getStudyGroupIdsForMember() {
        StudyGroupIdsDTO studyGroupIdsDTO = studyGroupFacade.getStudyGroupIdsForMember();
        StudyGroupIdsResponse response = StudyGroupIdsResponse.toStudyGroupIdsResponse(studyGroupIdsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 인증된 사용자의 정보를 가져옵니다.
     *
     * @return 사용자 정보 응답
     */
    @GetMapping("/auth/member-info")
    public ResponseEntity<StudyGroupMemberInfoResponse> getCurrentMemberInfo() {
        StudyGroupMemberInfoDTO memberInfoDTO = studyGroupFacade.getCurrentMemberInfo();
        StudyGroupMemberInfoResponse response = StudyGroupMemberInfoResponse.toStudyGroupMemberResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹에서 탈퇴합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return 스터디 그룹 삭제 응답
     */
    @DeleteMapping("/{studyGroupId}/leave")
    public ResponseEntity<StudyGroupDeletedResponse> deleteStudyGroup(@PathVariable Long studyGroupId) {
        StudyGroupDeleteDTO studyGroupDeleteDTO = studyGroupFacade.removeStudyGroupAsLeader(studyGroupId);
        StudyGroupDeletedResponse response = StudyGroupDeletedResponse.toStudyGroupDeletionResponse(studyGroupDeleteDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 스터디 그룹을 생성합니다.
     *
     * @param studyGroupCreateRequest 스터디 그룹 생성 요청
     * @return 스터디 그룹 생성 응답
     */
    @PostMapping
    public ResponseEntity<StudyGroupCreatedResponse> createStudyGroup(
            @Validated @RequestBody StudyGroupCreateRequest studyGroupCreateRequest) {

        StudyGroupCreateDTO studyGroupCreateDTO = StudyGroupCreateRequest.toStudyGroupCreateDTO(studyGroupCreateRequest);
        StudyGroupRegistrationDTO registrationDTO = studyGroupFacade.createStudyGroup(studyGroupCreateDTO);
        StudyGroupCreatedResponse response = StudyGroupCreatedResponse.toStudyGroupCreateResponse(registrationDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹의 세부 정보를 가져옵니다.
     *
     * @param articleId 게시글 ID
     * @return 스터디 그룹 세부 정보 응답
     */
    @GetMapping("/details/{articleId}")
    public ResponseEntity<StudyGroupDetailsResponse> getStudyGroupDetails(@PathVariable Long articleId) {
        StudyGroupDetailsDTO studyGroupDetailsDTO = studyGroupFacade.getStudyGroupDetails(articleId);
        StudyGroupDetailsResponse response = StudyGroupDetailsResponse.toStudyGroupDetailsResponse(studyGroupDetailsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹에 가입 요청을 보냅니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return 상태 없음 응답
     */
    @PostMapping("/{studyGroupId}/join-requests")
    public ResponseEntity<Void> createJoinRequest(@PathVariable Long studyGroupId) {
        studyGroupFacade.requestJoinStudyGroup(studyGroupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 스터디 그룹의 가입 요청 목록을 가져옵니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return 스터디 그룹 가입 요청 목록 응답
     */
    @GetMapping("/{studyGroupId}/received-requests")
    public ResponseEntity<StudyGroupListJoinResponse> listStudyGroupJoinRequests(@PathVariable Long studyGroupId) {
        StudyGroupListJoinRequestsDTO listJoinRequestsDTO = studyGroupFacade.listStudyGroupJoinRequests(studyGroupId);
        StudyGroupListJoinResponse response = StudyGroupListJoinResponse.toStudyGroupListJoinResponse(listJoinRequestsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹의 가입 요청을 수락합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param requestId 요청 ID
     * @return 상태 없음 응답
     */
    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyGroupFacade.acceptStudyGroupJoinRequest(studyGroupId, requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 스터디 그룹의 가입 요청을 거절합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param requestId 요청 ID
     * @return 상태 없음 응답
     */
    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyGroupFacade.rejectStudyGroupJoinRequest(studyGroupId, requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자가 스터디 그룹을 가지고 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 스터디 그룹 존재 여부 응답
     */
    @GetMapping("/exists")
    public ResponseEntity<StudyGroupCheckMembershipResponse> checkIfUserHasStudyGroup(@RequestParam Long userId) {
        StudyGroupCheckMembershipDTO membershipDTO = studyGroupFacade.checkMembership(userId);
        StudyGroupCheckMembershipResponse response = StudyGroupCheckMembershipResponse.toStudyGroupCheckMembershipResponse(membershipDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 사용자의 승인된 스터디 그룹 목록을 가져옵니다.
     *
     * @return 승인된 스터디 그룹 목록 응답
     */
    @GetMapping("/current-accepted")
    public ResponseEntity<AcceptedStudyGroupDetailsResponse> getCurrentUserAcceptedStudyGroups() {
        List<AcceptedStudyGroupDetailsDTO> acceptedStudyGroupDetailsDTOS = studyGroupFacade.getCurrentUserAcceptedStudyGroups();
        AcceptedStudyGroupDetailsResponse response = AcceptedStudyGroupDetailsResponse.toAcceptedStudyGroupDetailsResponse(acceptedStudyGroupDetailsDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 스터디 그룹 및 구성원 정보를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 스터디 그룹 및 구성원 정보 응답
     */
    @GetMapping("/user/{userId}/groups-with-members")
    public ResponseEntity<StudyGroupMembersDetailResponse> getStudyGroupsAndMembers(@PathVariable Long userId) {
        List<StudyGroupMembersDetailDTO> groupMembersDetailDTOS = studyGroupFacade.getStudyGroupsAndMembers(userId);
        StudyGroupMembersDetailResponse response = StudyGroupMembersDetailResponse.toStudyGroupMembersDetailResponse(groupMembersDetailDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹에서 구성원을 추방합니다.
     *
     * @param groupId 그룹 ID
     * @param memberId 회원 ID
     * @return 상태 없음 응답
     */
    @DeleteMapping("/{groupId}/members/{memberId}/expel")
    public ResponseEntity<Void> expelStudyGroupMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        studyGroupFacade.expelStudyGroupMember(groupId, memberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자가 스터디 그룹을 생성했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 스터디 그룹 생성 여부 응답
     */
    @GetMapping("/user/{userId}/group-existence")
    public ResponseEntity<StudyGroupExistsResponse> hasUserCreatedStudyGroup(@PathVariable Long userId) {
        StudyGroupExistsDTO studyGroupExistsDTO = studyGroupFacade.hasUserCreatedStudyGroup(userId);
        StudyGroupExistsResponse response = StudyGroupExistsResponse.toStudyGroupExistsResponse(studyGroupExistsDTO);
        return ResponseEntity.ok(response);
    }
}
