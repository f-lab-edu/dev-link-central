package dev.linkcentral.service.facade;

import dev.member.entity.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.service.ArticleService;
import dev.member.service.MemberService;
import dev.linkcentral.service.StudyGroupService;
import dev.linkcentral.service.StudyMemberService;
import dev.linkcentral.service.dto.article.ArticleDetailsDTO;
import dev.linkcentral.service.dto.studygroup.*;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudyGroupFacade {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final StudyGroupMapper studyGroupMapper;
    private final StudyGroupService studyGroupService;
    private final StudyMemberService studyMemberService;

    public StudyGroupIdsDTO getStudyGroupIdsForMember() {
        Member currentMember = memberService.getCurrentMember();
        List<Long> studyGroupIds = studyMemberService.getStudyGroupIdsByMemberId(currentMember.getId());
        return studyGroupMapper.toStudyGroupIdsDTO(studyGroupIds);
    }

    public StudyGroupMemberInfoDTO getCurrentMemberInfo() {
        Member currentMember = memberService.getCurrentMember();
        return studyGroupMapper.toStudyGroupMemberInfoDTO(currentMember);
    }

    public StudyGroupDeleteDTO removeStudyGroupAsLeader(Long studyGroupId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupService.getStudyGroupById(studyGroupId);

        if (studyGroup == null) {
            return studyGroupMapper.toStudyGroupDeletionDTO(false, "스터디 그룹을 찾을 수 없습니다.");
        }

        if (studyGroup.getStudyLeaderId().equals(currentMember.getId())) {
            studyMemberService.removeAllMembersByStudyGroupId(studyGroupId);
            studyGroupService.deleteStudyGroup(studyGroupId, currentMember.getId());
            return studyGroupMapper.toStudyGroupDeletionDTO(true, "스터디 그룹이 삭제되었습니다.");
        }
        return leaveStudyGroupAsMember(studyGroupId, currentMember);
    }

    private StudyGroupDeleteDTO leaveStudyGroupAsMember(Long studyGroupId, Member currentMember) {
        boolean isLeft = studyGroupService.leaveStudyGroupAsMember(studyGroupId, currentMember.getId());
        if (isLeft) {
            return studyGroupMapper.toStudyGroupDeletionDTO(true, "스터디 그룹에서 탈퇴하였습니다.");
        }
        return studyGroupMapper.toStudyGroupDeletionDTO(false, "스터디 그룹 또는 멤버를 찾을 수 없습니다.");
    }

    public StudyGroupRegistrationDTO createStudyGroup(StudyGroupCreateDTO studyGroupCreateDTO) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupService.createStudyGroup(
                studyGroupCreateDTO.getGroupName(),
                studyGroupCreateDTO.getStudyTopic(),
                currentMember.getId());
        return studyGroupMapper.toStudyGroupRegistrationDTO(studyGroup);
    }

    public StudyGroupDetailsDTO getStudyGroupDetails(Long articleId) {
        Member currentMember = memberService.getCurrentMember();
        ArticleDetailsDTO article = articleService.getArticleById(articleId);

        StudyGroup studyGroup = studyGroupService.findStudyGroupByLeaderId(article.getMember().getId());
        boolean isLeader = studyGroup.getStudyLeaderId().equals(currentMember.getId());

        return studyGroupMapper.toStudyGroupDetailsDTO(studyGroup, isLeader);
    }

    public StudyGroupListJoinRequestsDTO listStudyGroupJoinRequests(Long studyGroupId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupService.getStudyGroupById(studyGroupId);

        if (!studyGroup.getStudyLeaderId().equals(currentMember.getId())) {
            return new StudyGroupListJoinRequestsDTO(Collections.emptyList());
        }

        List<StudyGroupJoinRequestDTO> requestDTOList = studyMemberService.listJoinRequestsForStudyGroup(studyGroupId);
        return studyGroupMapper.toStudyGroupListJoinRequestsDTO(requestDTOList);
    }

    public StudyGroupCheckMembershipDTO checkMembership(Long userId) {
        boolean exists = studyGroupService.checkIfUserHasStudyGroup(userId);
        return studyGroupMapper.toStudyGroupCheckMembershipDTO(exists);
    }

    public List<AcceptedStudyGroupDetailsDTO> getCurrentUserAcceptedStudyGroups() {
        Member currentMember = memberService.getCurrentMember();
        return studyGroupService.getAcceptedGroupsByUser(currentMember.getId());
    }

    public List<StudyGroupMembersDetailDTO> getStudyGroupsAndMembers(Long userId) {
        return studyGroupService.getStudyGroupsAndMembers(userId);
    }

    public void expelStudyGroupMember(Long groupId, Long memberId) {
        Member currentMember = memberService.getCurrentMember();
        studyGroupService.expelMember(groupId, memberId, currentMember.getId());
    }

    public void rejectStudyGroupJoinRequest(Long studyGroupId, Long requestId) {
        studyMemberService.rejectJoinRequest(studyGroupId, requestId);
    }

    public void acceptStudyGroupJoinRequest(Long studyGroupId, Long requestId) {
        studyMemberService.acceptJoinRequest(studyGroupId, requestId);
    }

    public void requestJoinStudyGroup(Long studyGroupId) {
        studyMemberService.requestJoinStudyGroup(studyGroupId);
    }

    public StudyGroupMemberDTO getMemberById(Long memberId) {
        Member currentMember = memberService.getMemberById(memberId);
        return studyGroupMapper.toStudyGroupMemberDTO(currentMember);
    }

    public StudyGroupExistsDTO hasUserCreatedStudyGroup(Long userId) {
        return studyGroupService.userHasStudyGroup(userId);
    }
}
