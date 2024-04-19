package dev.linkcentral.presentation.dto.response.studygroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class studyGroupMemberResponse {

    private Long id;
    private String name;
}
