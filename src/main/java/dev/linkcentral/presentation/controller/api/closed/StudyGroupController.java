package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.StudyGroup;
import dev.linkcentral.presentation.dto.request.studygroup.StudyGroupInfoRequest;
import dev.linkcentral.presentation.dto.request.studygroup.StudyGroupRequest;
import dev.linkcentral.presentation.dto.request.studygroup.StudyGroupWithMembersRequest;
import dev.linkcentral.presentation.dto.request.studygroup.StudyMemberRequest;
import dev.linkcentral.presentation.dto.response.studygroup.StudyGroupCreateResponse;
import dev.linkcentral.presentation.dto.response.studygroup.StudyGroupDetailsResponse;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.StudyGroupService;
import dev.linkcentral.service.StudyMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/study-group")
public class StudyGroupController {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final StudyGroupService studyGroupService;
    private final StudyMemberService studyMemberService;

    @GetMapping("/study-group-id")
    public ResponseEntity<List<Long>> getStudyGroupIdsForMember() {
        Member member = memberService.getCurrentMember();
        List<Long> studyGroupIds = studyMemberService.getStudyGroupIdsByMemberId(member.getId());
        return ResponseEntity.ok(studyGroupIds);
    }

    @GetMapping("/auth/member-info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member member = memberService.getCurrentMember();
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(Collections.singletonMap("memberId", member.getId()));
    }

    @DeleteMapping("/{studyGroupId}/leave")
    public ResponseEntity<?> leaveStudyGroup(@PathVariable Long studyGroupId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupService.getStudyGroupById(studyGroupId);

        if (studyGroup == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("스터디 그룹을 찾을 수 없습니다.");
        }

        if (studyGroup.getStudyLeaderId().equals(currentMember.getId())) {
            studyMemberService.removeAllMembersByStudyGroupId(studyGroupId);
            studyGroupService.deleteStudyGroup(studyGroupId, currentMember.getId());
            return ResponseEntity.ok().body("스터디 그룹이 삭제되었습니다.");
        }

        boolean isLeft = studyGroupService.leaveStudyGroupAsMember(studyGroupId, currentMember.getId());
        if (isLeft) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("스터디 그룹 또는 멤버를 찾을 수 없습니다.");
    }

    @PostMapping
    public ResponseEntity<StudyGroupCreateResponse> createStudyGroup(@RequestBody StudyGroupRequest studyGroupDto) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupService.createStudyGroup(
                studyGroupDto.getGroupName(),
                studyGroupDto.getStudyTopic(),
                currentMember.getId());

        StudyGroupCreateResponse studyGroupResponse = new StudyGroupCreateResponse(
                studyGroup.getId(),
                studyGroup.getGroupName(),
                studyGroup.getStudyTopic());
        return ResponseEntity.ok(studyGroupResponse);
    }

    @GetMapping("/details/{articleId}")
    public ResponseEntity<StudyGroupDetailsResponse> getStudyGroupDetails(@PathVariable Long articleId) {
        Member currentMember = memberService.getCurrentMember();

        Article article = articleService.getArticleById(articleId);
        StudyGroup studyGroup = studyGroupService.findStudyGroupByLeaderId(article.getMember().getId());

        boolean isLeader = studyGroup.getStudyLeaderId().equals(currentMember.getId());

        StudyGroupDetailsResponse responseDTO = StudyGroupDetailsResponse.builder()
                .id(studyGroup.getId())
                .groupName(studyGroup.getGroupName())
                .studyTopic(studyGroup.getStudyTopic())
                .leaderStatus(isLeader)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/{studyGroupId}/join-requests")
    public ResponseEntity<?> requestJoinStudyGroup(@PathVariable Long studyGroupId) {
        studyMemberService.requestJoinStudyGroup(studyGroupId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studyGroupId}/received-requests")
    public ResponseEntity<?> listJoinRequests(@PathVariable Long studyGroupId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupService.getStudyGroupById(studyGroupId);
        if (!studyGroup.getStudyLeaderId().equals(currentMember.getId())) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<StudyMemberRequest> joinRequests = studyMemberService.listJoinRequestsForStudyGroup(studyGroupId);
        return ResponseEntity.ok(joinRequests);
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
