package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleUpdatedDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateResponse {

    private int status;
    private ArticleUpdatedDTO articleUpdatedDTO;

    public static ArticleUpdateResponse toArticleUpdateResponse(int status, ArticleUpdatedDTO dto) {
        return ArticleUpdateResponse.builder()
                .status(status)
                .articleUpdatedDTO(dto)
                .build();
    }
}
