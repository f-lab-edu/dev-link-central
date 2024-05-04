package dev.linkcentral.presentation.response.studygroup;

import dev.linkcentral.service.dto.studygroup.AcceptedStudyGroupDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptedStudyGroupDetailsResponse {

    List<AcceptedStudyGroupDetailsDTO> acceptedStudyGroupDetails;

    public static AcceptedStudyGroupDetailsResponse toAcceptedStudyGroupDetailsResponse(List<AcceptedStudyGroupDetailsDTO> groupDetailsDTOS) {
        return new AcceptedStudyGroupDetailsResponse(groupDetailsDTOS);
    }
}
