package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCommentUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentUpdateResponse {

    private Long id;
    private String contents;
    private String writerNickname;

    public static ArticleCommentUpdateResponse toArticleCommentUpdateResponse(ArticleCommentUpdateDTO commentUpdateDTO) {
        return new ArticleCommentUpdateResponse(
                commentUpdateDTO.getId(),
                commentUpdateDTO.getContents(),
                commentUpdateDTO.getNickname()
        );
    }
}
