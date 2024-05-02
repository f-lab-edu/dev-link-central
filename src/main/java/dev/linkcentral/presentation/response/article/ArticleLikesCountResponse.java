package dev.linkcentral.presentation.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikesCountResponse {

    private int likesCount;

    public static ArticleLikesCountResponse toArticleLikesCountResponse(int likesCount) {
        return new ArticleLikesCountResponse(likesCount);
    }
}
