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

    @GetMapping("/my-groups/ids")
    public ResponseEntity<StudyGroupIdsResponse> getStudyGroupIdsForMember() {
        StudyGroupIdsDTO studyGroupIdsDTO = studyGroupFacade.getStudyGroupIdsForMember();
        StudyGroupIdsResponse response = StudyGroupIdsResponse.toStudyGroupIdsResponse(studyGroupIdsDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/member-info")
    public ResponseEntity<StudyGroupMemberInfoResponse> getCurrentMemberInfo() {
        StudyGroupMemberInfoDTO memberInfoDTO = studyGroupFacade.getCurrentMemberInfo();
        StudyGroupMemberInfoResponse response = StudyGroupMemberInfoResponse.toStudyGroupMemberResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studyGroupId}/leave")
    public ResponseEntity<StudyGroupDeletionResponse> deleteStudyGroup(@PathVariable Long studyGroupId) {
        StudyGroupDeleteDTO studyGroupDeleteDTO = studyGroupFacade.removeStudyGroupAsLeader(studyGroupId);
        StudyGroupDeletionResponse response = StudyGroupDeletionResponse.toStudyGroupDeletionResponse(studyGroupDeleteDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StudyGroupCreateResponse> createStudyGroup(
            @Validated @RequestBody StudyGroupCreateRequest studyGroupCreateRequest) {

        StudyGroupCreateDTO studyGroupCreateDTO = StudyGroupCreateRequest.toStudyGroupCreateDTO(studyGroupCreateRequest);
        StudyGroupRegistrationDTO registrationDTO = studyGroupFacade.createStudyGroup(studyGroupCreateDTO);
        StudyGroupCreateResponse response = StudyGroupCreateResponse.toStudyGroupCreateResponse(registrationDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{articleId}")
    public ResponseEntity<StudyGroupDetailsResponse> getStudyGroupDetails(@PathVariable Long articleId) {
        StudyGroupDetailsDTO studyGroupDetailsDTO = studyGroupFacade.getStudyGroupDetails(articleId);
        StudyGroupDetailsResponse response = StudyGroupDetailsResponse.toStudyGroupDetailsResponse(studyGroupDetailsDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{studyGroupId}/join-requests")
    public ResponseEntity<Void> createJoinRequest(@PathVariable Long studyGroupId) {
        studyGroupFacade.requestJoinStudyGroup(studyGroupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studyGroupId}/received-requests")
    public ResponseEntity<StudyGroupListJoinResponse> listStudyGroupJoinRequests(@PathVariable Long studyGroupId) {
        StudyGroupListJoinRequestsDTO listJoinRequestsDTO = studyGroupFacade.listStudyGroupJoinRequests(studyGroupId);
        StudyGroupListJoinResponse response = StudyGroupListJoinResponse.toStudyGroupListJoinResponse(listJoinRequestsDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyGroupFacade.acceptStudyGroupJoinRequest(studyGroupId, requestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyGroupFacade.rejectStudyGroupJoinRequest(studyGroupId, requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<StudyGroupCheckMembershipResponse> checkIfUserHasStudyGroup(@RequestParam Long userId) {
        StudyGroupCheckMembershipDTO membershipDTO = studyGroupFacade.checkMembership(userId);
        StudyGroupCheckMembershipResponse response = StudyGroupCheckMembershipResponse.toStudyGroupCheckMembershipResponse(membershipDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-accepted")
    public ResponseEntity<AcceptedStudyGroupDetailsResponse> getCurrentUserAcceptedStudyGroups() {
        List<AcceptedStudyGroupDetailsDTO> acceptedStudyGroupDetailsDTOS = studyGroupFacade.getCurrentUserAcceptedStudyGroups();
        AcceptedStudyGroupDetailsResponse response = AcceptedStudyGroupDetailsResponse.toAcceptedStudyGroupDetailsResponse(acceptedStudyGroupDetailsDTOS);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/groups-with-members")
    public ResponseEntity<StudyGroupMembersDetailResponse> getStudyGroupsAndMembers(@PathVariable Long userId) {
        List<StudyGroupMembersDetailDTO> groupMembersDetailDTOS = studyGroupFacade.getStudyGroupsAndMembers(userId);
        StudyGroupMembersDetailResponse response = StudyGroupMembersDetailResponse.toStudyGroupMembersDetailResponse(groupMembersDetailDTOS);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupId}/members/{memberId}/expel")
    public ResponseEntity<Void> expelStudyGroupMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        studyGroupFacade.expelStudyGroupMember(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
}
