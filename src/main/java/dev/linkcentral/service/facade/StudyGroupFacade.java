package dev.linkcentral.service.facade;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.StudyGroup;
import dev.linkcentral.presentation.dto.StudyGroupDeletionDTO;
import dev.linkcentral.presentation.dto.StudyGroupIdsDTO;
import dev.linkcentral.presentation.dto.StudyGroupMemberInfoDTO;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.StudyGroupService;
import dev.linkcentral.service.StudyMemberService;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StudyGroupFacade {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final StudyGroupService studyGroupService;
    private final StudyMemberService studyMemberService;
    private final StudyGroupMapper studyGroupMapper;

    public StudyGroupIdsDTO getStudyGroupIdsForMember() {
        Member currentMember = memberService.getCurrentMember();
        List<Long> studyGroupIds = studyMemberService.getStudyGroupIdsByMemberId(currentMember.getId());
        return studyGroupMapper.toStudyGroupIdsDTO(studyGroupIds);
    }

    public StudyGroupMemberInfoDTO getCurrentMemberInfo() {
        Member currentMember = memberService.getCurrentMember();
        return studyGroupMapper.toStudyGroupMemberInfoDTO(currentMember);
    }

    public StudyGroupDeletionDTO removeStudyGroupAsLeader(Long studyGroupId) {
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

    private StudyGroupDeletionDTO leaveStudyGroupAsMember(Long studyGroupId, Member currentMember) {
        boolean isLeft = studyGroupService.leaveStudyGroupAsMember(studyGroupId, currentMember.getId());
        if (isLeft) {
            return studyGroupMapper.toStudyGroupDeletionDTO(true, "스터디 그룹에서 탈퇴하였습니다.");
        }
        return studyGroupMapper.toStudyGroupDeletionDTO(false, "스터디 그룹 또는 멤버를 찾을 수 없습니다.");
    }


}
