package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.StudyGroupDeletionDTO;
import dev.linkcentral.presentation.dto.StudyGroupIdsDTO;
import dev.linkcentral.presentation.dto.StudyGroupMemberInfoDTO;
import dev.linkcentral.presentation.dto.response.StudyGroupDeletionResponse;
import dev.linkcentral.presentation.dto.response.StudyGroupIdsResponse;
import dev.linkcentral.presentation.dto.response.StudyGroupMemberInfoResponse;
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
}
