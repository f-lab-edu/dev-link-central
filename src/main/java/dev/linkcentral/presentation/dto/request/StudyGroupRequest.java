package dev.linkcentral.presentation.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(description = "스터디 그룹 생성을 위한 요청 데이터")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroupRequest {

    @ApiModelProperty(value = "스터디 그룹의 이름", required = true)
    @NotBlank(message = "그룹 이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 50, message = "그룹 이름은 2자 이상 50자 이하이어야 합니다.")
    private String groupName;

    @ApiModelProperty(value = "스터디 주제", required = true)
    @NotBlank(message = "스터디 주제는 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "스터디 주제는 3자 이상 100자 이하이어야 합니다.")
    private String studyTopic;

}
