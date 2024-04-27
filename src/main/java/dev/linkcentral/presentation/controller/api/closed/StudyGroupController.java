package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.*;
import dev.linkcentral.presentation.dto.request.StudyGroupCreateRequest;
import dev.linkcentral.presentation.dto.request.StudyGroupInfoRequest;
import dev.linkcentral.presentation.dto.request.StudyGroupWithMembersRequest;
import dev.linkcentral.presentation.dto.response.*;
import dev.linkcentral.service.facade.StudyGroupFacade;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/study-group")
public class StudyGroupController {

    private final StudyGroupFacade studyGroupFacade;
    private final StudyGroupMapper studyGroupMapper;

    @GetMapping("/study-group-id")
    public ResponseEntity<StudyGroupIdsResponse> getStudyGroupIdsForMember() {
        StudyGroupIdsDTO studyGroupIdsDTO = studyGroupFacade.getStudyGroupIdsForMember();
        StudyGroupIdsResponse response = studyGroupMapper.toStudyGroupIdsResponse(studyGroupIdsDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/member-info")
    public ResponseEntity<StudyGroupMemberInfoResponse> getCurrentMemberInfo() {
        StudyGroupMemberInfoDTO memberInfoDTO = studyGroupFacade.getCurrentMemberInfo();
        StudyGroupMemberInfoResponse response = studyGroupMapper.toStudyGroupMemberResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studyGroupId}/leave")
    public ResponseEntity<StudyGroupDeletionResponse> leaveStudyGroup(@PathVariable Long studyGroupId) {
        StudyGroupDeletionDTO studyGroupDeletionDTO = studyGroupFacade.removeStudyGroupAsLeader(studyGroupId);
        StudyGroupDeletionResponse response = studyGroupMapper.toStudyGroupDeletionResponse(studyGroupDeletionDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StudyGroupCreateResponse> createStudyGroup(
            @Validated @RequestBody StudyGroupCreateRequest studyGroupCreateRequest) {

        StudyGroupCreateDTO studyGroupCreateDTO = studyGroupMapper.toStudyGroupCreateDTO(studyGroupCreateRequest);
        StudyGroupRegistrationDTO registrationDTO = studyGroupFacade.createStudyGroup(studyGroupCreateDTO);
        StudyGroupCreateResponse response = studyGroupMapper.toStudyGroupCreateResponse(registrationDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{articleId}")
    public ResponseEntity<StudyGroupDetailsResponse> getStudyGroupDetails(@PathVariable Long articleId) {
        StudyGroupDetailsDTO studyGroupDetailsDTO = studyGroupFacade.getStudyGroupDetails(articleId);
        StudyGroupDetailsResponse response = studyGroupMapper.toStudyGroupDetailsResponse(studyGroupDetailsDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{studyGroupId}/join-requests")
    public ResponseEntity<Void> requestJoinStudyGroup(@PathVariable Long studyGroupId) {
        studyMemberService.requestJoinStudyGroup(studyGroupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studyGroupId}/received-requests")
    public ResponseEntity<StudyGroupListJoinResponse> listStudyGroupJoinRequests(@PathVariable Long studyGroupId) {
        StudyGroupListJoinRequestsDTO listJoinRequestsDTO = studyGroupFacade.listStudyGroupJoinRequests(studyGroupId);
        StudyGroupListJoinResponse response = studyGroupMapper.toStudyGroupListJoinResponse(listJoinRequestsDTO);
        return ResponseEntity.ok(response);
    }






    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/accept")
    public ResponseEntity<?> acceptJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyMemberService.acceptJoinRequest(studyGroupId, requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{studyGroupId}/membership-requests/{requestId}/reject")
    public ResponseEntity<?> rejectJoinRequest(@PathVariable Long studyGroupId, @PathVariable Long requestId) {
        studyMemberService.rejectJoinRequest(studyGroupId, requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkIfUserHasStudyGroup(@RequestParam Long userId) {
        boolean exists = studyGroupService.checkIfUserHasStudyGroup(userId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/current-accepted")
    public ResponseEntity<List<StudyGroupInfoRequest>> getCurrentUserAcceptedStudyGroups(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member currentMember = memberService.getCurrentMember();
        List<StudyGroupInfoRequest> groupInfoDTOs = studyGroupService.findAcceptedStudyGroupInfoDTOsByUserId(currentMember.getId());

        return ResponseEntity.ok(groupInfoDTOs);
    }

    @GetMapping("/user/{userId}/groups-with-members")
    public ResponseEntity<?> getStudyGroupsAndMembers(@PathVariable Long userId) {
        List<StudyGroupWithMembersRequest> groupsAndMembers = studyGroupService.getStudyGroupsAndMembers(userId);
        return ResponseEntity.ok(groupsAndMembers);
    }

    @DeleteMapping("/{groupId}/members/{memberId}/expel")
    public ResponseEntity<?> expelMember(@PathVariable Long groupId, @PathVariable Long memberId) {
        try {
            Member currentMember = memberService.getCurrentMember();
            studyGroupService.expelMember(groupId, memberId, currentMember.getId());
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
