package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.StudyGroup;
import dev.linkcentral.presentation.dto.*;
import dev.linkcentral.presentation.dto.request.StudyGroupCreateRequest;
import dev.linkcentral.presentation.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyGroupMapper {

    public StudyGroupIdsDTO toStudyGroupIdsDTO(List<Long> studyGroupIds) {
        return new StudyGroupIdsDTO(studyGroupIds);
    }

    public StudyGroupIdsResponse toStudyGroupIdsResponse(StudyGroupIdsDTO studyGroupIdsDTO) {
        return StudyGroupIdsResponse.builder()
                .studyGroupIds(studyGroupIdsDTO.getStudyGroupIds())
                .build();
    }

    public StudyGroupMemberInfoDTO toStudyGroupMemberInfoDTO(Member member) {
        return new StudyGroupMemberInfoDTO(member.getId());
    }

    public StudyGroupMemberInfoResponse toStudyGroupMemberResponse(StudyGroupMemberInfoDTO memberInfoDTO) {
        return new StudyGroupMemberInfoResponse(memberInfoDTO.getMemberId());
    }

    public StudyGroupDeletionDTO toStudyGroupDeletionDTO(boolean success, String message) {
        return new StudyGroupDeletionDTO(success, message);
    }

    public StudyGroupDeletionResponse toStudyGroupDeletionResponse(StudyGroupDeletionDTO dto) {
        return new StudyGroupDeletionResponse(
                dto.isSuccess(),
                dto.getMessage());
    }

    public StudyGroupCreateDTO toStudyGroupCreateDTO(StudyGroupCreateRequest studyGroupCreateRequest) {
        return new StudyGroupCreateDTO(
                studyGroupCreateRequest.getGroupName(),
                studyGroupCreateRequest.getStudyTopic());
    }

    public StudyGroupRegistrationDTO toStudyGroupRegistrationDTO(StudyGroup studyGroup) {
        return new StudyGroupRegistrationDTO(
                studyGroup.getId(),
                studyGroup.getGroupName(),
                studyGroup.getStudyTopic());
    }

    public StudyGroupCreateResponse toStudyGroupCreateResponse(StudyGroupRegistrationDTO registrationDTO) {
        return new StudyGroupCreateResponse(
                registrationDTO.getId(),
                registrationDTO.getGroupName(),
                registrationDTO.getStudyTopic());
    }

    public StudyGroupDetailsDTO toStudyGroupDetailsDTO(StudyGroup studyGroup, boolean isLeader) {
        return StudyGroupDetailsDTO.builder()
                .id(studyGroup.getId())
                .groupName(studyGroup.getGroupName())
                .studyTopic(studyGroup.getStudyTopic())
                .leaderStatus(isLeader)
                .build();
    }

    public StudyGroupDetailsResponse toStudyGroupDetailsResponse(StudyGroupDetailsDTO studyGroup) {
        return StudyGroupDetailsResponse.builder()
                .id(studyGroup.getId())
                .groupName(studyGroup.getGroupName())
                .studyTopic(studyGroup.getStudyTopic())
                .leaderStatus(studyGroup.isLeaderStatus())
                .build();
    }

    public StudyGroupListJoinRequestsDTO toStudyGroupListJoinRequestsDTO(List<StudyGroupJoinRequestDTO> joinRequests) {
        return StudyGroupListJoinRequestsDTO.builder()
                .studyMemberRequests(joinRequests)
                .build();
    }

    public StudyGroupListJoinResponse toStudyGroupListJoinResponse(StudyGroupListJoinRequestsDTO listJoinRequestsDTO) {
        return StudyGroupListJoinResponse.builder()
                .studyMemberRequests(listJoinRequestsDTO.getStudyMemberRequests())
                .build();
    }

}
