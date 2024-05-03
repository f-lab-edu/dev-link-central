package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateResponse {

    private boolean success;
    private String message;
    private Long articleId;
    private String title;

    public static ArticleCreateResponse toArticleCreateResponse(ArticleCreateDTO articleDTO) {
        return new ArticleCreateResponse(
                true,
                "글이 성공적으로 작성되었습니다.",
                articleDTO.getWriterId(),
                articleDTO.getTitle()
        );
    }
}
