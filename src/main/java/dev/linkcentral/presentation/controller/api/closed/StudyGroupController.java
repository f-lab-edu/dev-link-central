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
     * 현재 회원의 스터디 그룹 ID 목록을 가져옵니다.
     *
     * @return ResponseEntity<StudyGroupIdsResponse> 스터디 그룹 ID 응답
     */
    @GetMapping("/my-groups/ids")
    public ResponseEntity<StudyGroupIdsResponse> getStudyGroupIdsForMember() {
        StudyGroupIdsDTO studyGroupIdsDTO = studyGroupFacade.getStudyGroupIdsForMember();
        StudyGroupIdsResponse response = StudyGroupIdsResponse.toStudyGroupIdsResponse(studyGroupIdsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인된 회원의 정보를 가져옵니다.
     *
     * @return ResponseEntity<StudyGroupMemberInfoResponse> 회원 정보 응답
     */
    @GetMapping("/auth/member-info")
    public ResponseEntity<StudyGroupMemberInfoResponse> getCurrentMemberInfo() {
        StudyGroupMemberInfoDTO memberInfoDTO = studyGroupFacade.getCurrentMemberInfo();
        StudyGroupMemberInfoResponse response = StudyGroupMemberInfoResponse.toStudyGroupMemberResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹을 삭제합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return ResponseEntity<StudyGroupDeletionResponse> 스터디 그룹 삭제 응답
     */
    @DeleteMapping("/{studyGroupId}/leave")
    public ResponseEntity<StudyGroupDeletionResponse> deleteStudyGroup(@PathVariable Long studyGroupId) {
        StudyGroupDeleteDTO studyGroupDeleteDTO = studyGroupFacade.removeStudyGroupAsLeader(studyGroupId);
        StudyGroupDeletionResponse response = StudyGroupDeletionResponse.toStudyGroupDeletionResponse(studyGroupDeleteDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 스터디 그룹을 생성합니다.
     *
     * @param studyGroupCreateRequest 스터디 그룹 생성 요청
     * @return ResponseEntity<StudyGroupCreateResponse> 스터디 그룹 생성 응답
     */
    @PostMapping
    public ResponseEntity<StudyGroupCreateResponse> createStudyGroup(
            @Validated @RequestBody StudyGroupCreateRequest studyGroupCreateRequest) {

        StudyGroupCreateDTO studyGroupCreateDTO = StudyGroupCreateRequest.toStudyGroupCreateDTO(studyGroupCreateRequest);
        StudyGroupRegistrationDTO registrationDTO = studyGroupFacade.createStudyGroup(studyGroupCreateDTO);
        StudyGroupCreateResponse response = StudyGroupCreateResponse.toStudyGroupCreateResponse(registrationDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글 ID로 스터디 그룹 세부 정보를 가져옵니다.
     *
     * @param articleId 게시글 ID
     * @return ResponseEntity<StudyGroupDetailsResponse> 스터디 그룹 세부 정보 응답
     */
    @GetMapping("/details/{articleId}")
    public ResponseEntity<StudyGroupDetailsResponse> getStudyGroupDetails(@PathVariable Long articleId) {
        StudyGroupDetailsDTO studyGroupDetailsDTO = studyGroupFacade.getStudyGroupDetails(articleId);
        StudyGroupDetailsResponse response = StudyGroupDetailsResponse.toStudyGroupDetailsResponse(studyGroupDetailsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹에 참여 요청을 보냅니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return ResponseEntity<Void> 빈 응답
     */
    @PostMapping("/{studyGroupId}/join-requests")
    public ResponseEntity<Void> createJoinRequest(@PathVariable Long studyGroupId) {
        studyGroupFacade.requestJoinStudyGroup(studyGroupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 스터디 그룹의 참여 요청 목록을 가져옵니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return ResponseEntity<StudyGroupListJoinResponse> 참여 요청 목록 응답
     */
    @GetMapping("/{studyGroupId}/received-requests")
    public ResponseEntity<StudyGroupListJoinResponse> listStudyGroupJoinRequests(@PathVariable Long studyGroupId) {
        StudyGroupListJoinRequestsDTO listJoinRequestsDTO = studyGroupFacade.listStudyGroupJoinRequests(studyGroupId);
        StudyGroupListJoinResponse response = StudyGroupListJoinResponse.toStudyGroupListJoinResponse(listJoinRequestsDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 스터디 그룹 참여 요청을 수락합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param requestId 요청 ID
     * @return ResponseEntity<Void> 빈 응답
     */
    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyGroupFacade.acceptStudyGroupJoinRequest(studyGroupId, requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 스터디 그룹 참여 요청을 거절합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param requestId 요청 ID
     * @return ResponseEntity<Void> 빈 응답
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
     * @return ResponseEntity<StudyGroupCheckMembershipResponse> 스터디 그룹 보유 여부 응답
     */
    @GetMapping("/exists")
    public ResponseEntity<StudyGroupCheckMembershipResponse> checkIfUserHasStudyGroup(@RequestParam Long userId) {
        StudyGroupCheckMembershipDTO membershipDTO = studyGroupFacade.checkMembership(userId);
        StudyGroupCheckMembershipResponse response = StudyGroupCheckMembershipResponse.toStudyGroupCheckMembershipResponse(membershipDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 사용자가 참여하고 있는 스터디 그룹 목록을 가져옵니다.
     *
     * @return ResponseEntity<AcceptedStudyGroupDetailsResponse> 참여 스터디 그룹 목록 응답
     */
    @GetMapping("/current-accepted")
    public ResponseEntity<AcceptedStudyGroupDetailsResponse> getCurrentUserAcceptedStudyGroups() {
        List<AcceptedStudyGroupDetailsDTO> acceptedStudyGroupDetailsDTOS = studyGroupFacade.getCurrentUserAcceptedStudyGroups();
        AcceptedStudyGroupDetailsResponse response = AcceptedStudyGroupDetailsResponse.toAcceptedStudyGroupDetailsResponse(acceptedStudyGroupDetailsDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 사용자의 스터디 그룹과 구성원 정보를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return ResponseEntity<StudyGroupMembersDetailResponse> 스터디 그룹 및 구성원 정보 응답
     */
    @GetMapping("/user/{userId}/groups-with-members")
    public ResponseEntity<StudyGroupMembersDetailResponse> getStudyGroupsAndMembers(@PathVariable Long userId) {
        List<StudyGroupMembersDetailDTO> groupMembersDetailDTOS = studyGroupFacade.getStudyGroupsAndMembers(userId);
        StudyGroupMembersDetailResponse response = StudyGroupMembersDetailResponse.toStudyGroupMembersDetailResponse(groupMembersDetailDTOS);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 스터디 그룹의 회원을 추방합니다.
     *
     * @param groupId 스터디 그룹 ID
     * @param memberId 회원 ID
     * @return ResponseEntity<Void> 빈 응답
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
     * @return ResponseEntity<StudyGroupExistsResponse> 스터디 그룹 생성 여부 응답
     */
    @GetMapping("/user/{userId}/group-existence")
    public ResponseEntity<StudyGroupExistsResponse> hasUserCreatedStudyGroup(@PathVariable Long userId) {
        StudyGroupExistsDTO studyGroupExistsDTO = studyGroupFacade.hasUserCreatedStudyGroup(userId);
        StudyGroupExistsResponse response = StudyGroupExistsResponse.toStudyGroupExistsResponse(studyGroupExistsDTO);
        return ResponseEntity.ok(response);
    }
}
