package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.database.repository.studygroup.StudyMemberRepository;
import dev.linkcentral.service.dto.studygroup.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudyGroupMapper {

    private final StudyMemberRepository studyMemberRepository;

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

    public StudyGroupMembersDetailDTO toStudyGroupMembersDetailDTO(StudyGroup group) {
        List<StudyMember> acceptedMembers = studyMemberRepository.findAllByStudyGroupAndStatus(group, StudyGroupStatus.ACCEPTED);

        List<StudyGroupMemberBasicInfoDTO> memberDtos = acceptedMembers.stream()
                .map(member -> new StudyGroupMemberBasicInfoDTO(
                        member.getMember().getId(),
                        member.getMember().getName()))
                .collect(Collectors.toList());

        return new StudyGroupMembersDetailDTO(
                group.getId(),
                group.getStudyLeaderId(),
                group.getGroupName(),
                memberDtos);
    }
}
