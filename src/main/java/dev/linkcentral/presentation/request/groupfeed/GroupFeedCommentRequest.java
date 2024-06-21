package dev.linkcentral.presentation.request.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedCommentDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "피드 댓글 작성을 위한 요청 데이터 모델")
public class GroupFeedCommentRequest {

    @ApiModelProperty(value = "게시글의 내용", required = true)
    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    public static GroupFeedCommentDTO toGroupFeedCommentCommand(GroupFeedCommentRequest commentRequest) {
        return GroupFeedCommentDTO.builder()
                .content(commentRequest.getContent())
                .build();
    }
}
