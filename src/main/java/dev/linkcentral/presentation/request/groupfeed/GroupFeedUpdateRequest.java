package dev.linkcentral.presentation.request.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedUpdateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "피드 수정을 위한 요청 데이터 모델")
public class GroupFeedUpdateRequest {

    @ApiModelProperty(value = "피드 ID", required = true)
    private Long feedId;

    @ApiModelProperty(value = "게시글의 제목", required = true)
    @NotBlank(message = "게시글 제목은 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @ApiModelProperty(value = "게시글의 내용", required = true)
    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    @ApiModelProperty(value = "피드 이미지 파일")
    private MultipartFile image;

    public static GroupFeedUpdateDTO toGroupFeedUpdateCommand(Long feedId, GroupFeedUpdateRequest updateRequest) {
        return GroupFeedUpdateDTO.builder()
                .feedId(feedId)
                .title(updateRequest.getTitle())
                .content(updateRequest.getContent())
                .imageFile(updateRequest.getImage())
                .build();
    }
}
