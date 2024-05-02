package dev.linkcentral.presentation.request.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "게시글 업데이트를 위한 요청 데이터")
public class ArticleUpdateRequest {

    @ApiModelProperty(value = "게시글의 고유 식별자", required = true)
    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long id;

    @ApiModelProperty(value = "게시글 작성자", required = true)
    @NotBlank(message = "게시글 작성자는 필수입니다.")
    private String writer;

    @ApiModelProperty(value = "게시글의 제목", required = true)
    @NotBlank(message = "게시글 제목은 필수입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @ApiModelProperty(value = "게시글의 내용", required = true)
    @NotBlank(message = "게시글 내용은 필수입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

}
