package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupUserDTO {

    private Long id;
    private String name;
}
