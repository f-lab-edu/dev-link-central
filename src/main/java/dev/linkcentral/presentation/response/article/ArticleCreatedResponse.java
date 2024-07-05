package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreatedResponse {

    private boolean success;
    private String message;
    private Long articleId;
    private String title;

    public static ArticleCreatedResponse toArticleCreateResponse(ArticleCreateDTO articleDTO) {
        return new ArticleCreatedResponse(
                true,
                "글이 성공적으로 작성되었습니다.",
                articleDTO.getWriterId(),
                articleDTO.getTitle()
        );
    }
}
