package dev.linkcentral.presentation.request.article;

import dev.linkcentral.service.dto.article.ArticleCommentRequestDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "게시글에 대한 댓글을 추가하기 위한 요청 데이터")
public class ArticleCommentRequest {

    private Long id;

    private Long articleId;

    @ApiModelProperty(value = "댓글 내용", required = true)
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 500, message = "댓글 내용은 1자 이상 500자 이하이어야 합니다.")
    private String contents;

    private String nickname;

    @ApiModelProperty(value = "댓글 작성 시간")
    private LocalDateTime createdAt;

    public static ArticleCommentRequestDTO toArticleCommentRequestCommand(ArticleCommentRequest commentRequest) {
        return new ArticleCommentRequestDTO(
                commentRequest.getId(),
                commentRequest.getArticleId(),
                commentRequest.getContents(),
                commentRequest.getNickname(),
                commentRequest.getCreatedAt()
        );
    }

    public static ArticleCommentRequestDTO toArticleCommentRequestCommand(ArticleCommentRequest commentRequest, Long id) {
        commentRequest.setArticleId(id);

        return new ArticleCommentRequestDTO(
                commentRequest.getId(),
                commentRequest.getArticleId(),
                commentRequest.getContents(),
                commentRequest.getNickname(),
                commentRequest.getCreatedAt()
        );
    }
}
