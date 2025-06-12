package dev.linkcentral.presentation.request.article;

import dev.linkcentral.service.dto.article.ArticleCommentRequestDTO;
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
public class ArticleCommentRequest {

    private Long id;

    private Long articleId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(min = 1, max = 500, message = "댓글 내용은 1자 이상 500자 이하이어야 합니다.")
    private String contents;

    private String nickname;

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
