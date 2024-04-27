package dev.linkcentral.presentation.dto.response;

import dev.linkcentral.presentation.dto.request.AcceptedStudyGroupDetailsDTO;
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

}
