package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleLikeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikeResponse {

    private boolean isLiked;
    private int totalLikes;

    public static ArticleLikeResponse toArticleLikeResponse(ArticleLikeDTO dto) {
        return new ArticleLikeResponse(
                dto.isLiked(),
                dto.getTotalLikes()
        );
    }
}
