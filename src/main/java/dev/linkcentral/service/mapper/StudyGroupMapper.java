package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.service.dto.studygroup.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyGroupMapper {

    public StudyGroupIdsDTO toStudyGroupIdsDTO(List<Long> studyGroupIds) {
        return new StudyGroupIdsDTO(studyGroupIds);
    }

    public StudyGroupMemberInfoDTO toStudyGroupMemberInfoDTO(Member member) {
        return new StudyGroupMemberInfoDTO(member.getId());
    }

    public StudyGroupDeleteDTO toStudyGroupDeletionDTO(boolean success, String message) {
        return new StudyGroupDeleteDTO(success, message);
    }

    public StudyGroupRegistrationDTO toStudyGroupRegistrationDTO(StudyGroup studyGroup) {
        return new StudyGroupRegistrationDTO(
                studyGroup.getId(),
                studyGroup.getGroupName(),
                studyGroup.getStudyTopic());
    }

    public StudyGroupDetailsDTO toStudyGroupDetailsDTO(StudyGroup studyGroup, boolean isLeader) {
        return StudyGroupDetailsDTO.builder()
                .id(studyGroup.getId())
                .groupName(studyGroup.getGroupName())
                .studyTopic(studyGroup.getStudyTopic())
                .leaderStatus(isLeader)
                .build();
    }

    public StudyGroupListJoinRequestsDTO toStudyGroupListJoinRequestsDTO(List<StudyGroupJoinRequestDTO> joinRequests) {
        return StudyGroupListJoinRequestsDTO.builder()
                .studyMemberRequests(joinRequests)
                .build();
    }

    public StudyGroupCheckMembershipDTO toStudyGroupCheckMembershipDTO(boolean exists) {
        return new StudyGroupCheckMembershipDTO(exists);
    }

    public StudyGroupMemberDTO toStudyGroupMemberDTO(Member currentMember) {
        return new StudyGroupMemberDTO(currentMember);
    }

    public StudyGroupExistsDTO toStudyGroupExistsDTO(boolean exists, Long groupId) {
        return new StudyGroupExistsDTO(exists, groupId);
    }

    public AcceptedStudyGroupDetailsDTO toAcceptedStudyGroupDetailsDTO(StudyGroup studyGroup, List<StudyGroupUserDTO> members) {
        return new AcceptedStudyGroupDetailsDTO(
                studyGroup.getId(),
                studyGroup.getGroupName(),
                studyGroup.getStudyTopic(),
                members.size(),
                studyGroup.getStudyLeaderId(),
                members
        );
    }
}
