package dev.linkcentral.service.mapper;

import dev.linkcentral.presentation.dto.StudyGroupIdsDTO;
import dev.linkcentral.presentation.dto.response.StudyGroupIdsResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyGroupMapper {

    public StudyGroupIdsDTO toStudyGroupIdsDTO(List<Long> studyGroupIds) {
        return new StudyGroupIdsDTO(studyGroupIds);
    }

    public StudyGroupIdsResponse toStudyGroupIdsResponse(StudyGroupIdsDTO dto) {
        return StudyGroupIdsResponse.builder()
                .studyGroupIds(dto.getStudyGroupIds())
                .build();
    }

}
