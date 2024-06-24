package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleLikeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikedResponse {

    private boolean isLiked;
    private int totalLikes;

    public static ArticleLikedResponse toArticleLikeResponse(ArticleLikeDTO dto) {
        return new ArticleLikedResponse(
                dto.isLiked(),
                dto.getTotalLikes()
        );
    }
}
