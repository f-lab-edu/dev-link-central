package dev.linkcentral.presentation.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikesCountedResponse {

    private int likesCount;

    public static ArticleLikesCountedResponse toArticleLikesCountResponse(int likesCount) {
        return new ArticleLikesCountedResponse(likesCount);
    }
}
