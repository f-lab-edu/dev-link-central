package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCommentUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentUpdatedResponse {

    private Long id;
    private String contents;
    private String writerNickname;

    public static ArticleCommentUpdatedResponse toArticleCommentUpdateResponse(ArticleCommentUpdateDTO commentUpdateDTO) {
        return new ArticleCommentUpdatedResponse(
                commentUpdateDTO.getId(),
                commentUpdateDTO.getContents(),
                commentUpdateDTO.getNickname()
        );
    }
}
