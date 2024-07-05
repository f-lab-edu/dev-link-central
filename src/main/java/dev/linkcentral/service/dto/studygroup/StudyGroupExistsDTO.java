package dev.linkcentral.service.dto.studygroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupExistsDTO {

    private boolean exists;
    private Long groupId;
}
