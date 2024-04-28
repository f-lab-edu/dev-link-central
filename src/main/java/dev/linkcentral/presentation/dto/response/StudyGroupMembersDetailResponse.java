package dev.linkcentral.presentation.dto.response;

import dev.linkcentral.presentation.dto.request.StudyGroupMembersDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupMembersDetailResponse {

    List<StudyGroupMembersDetailDTO> groupMembersDetailDTOS;

}
