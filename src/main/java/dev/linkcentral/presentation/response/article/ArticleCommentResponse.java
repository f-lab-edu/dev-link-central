package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCommentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentResponse {

    private Long id;
    private Long articleId;
    private String contents;
    private String writerNickname;
    private String createdAt;

    public static ArticleCommentResponse toCommentResponse(ArticleCommentDTO savedCommentDTO) {
        return new ArticleCommentResponse(
                savedCommentDTO.getId(),
                savedCommentDTO.getArticleId(),
                savedCommentDTO.getContents(),
                savedCommentDTO.getWriterNickname(),
                savedCommentDTO.getCreatedAt()
        );
    }
}
