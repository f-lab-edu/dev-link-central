package dev.linkcentral.presentation.request.studygroup;

import dev.linkcentral.service.dto.studygroup.StudyGroupCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupCreateRequest {

    @NotBlank(message = "그룹 이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 50, message = "그룹 이름은 2자 이상 50자 이하이어야 합니다.")
    private String groupName;

    @NotBlank(message = "스터디 주제는 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "스터디 주제는 3자 이상 100자 이하이어야 합니다.")
    private String studyTopic;

    public static StudyGroupCreateDTO toStudyGroupCreateDTO(StudyGroupCreateRequest studyGroupCreateRequest) {
        return new StudyGroupCreateDTO(
                studyGroupCreateRequest.getGroupName(),
                studyGroupCreateRequest.getStudyTopic());
    }

}
