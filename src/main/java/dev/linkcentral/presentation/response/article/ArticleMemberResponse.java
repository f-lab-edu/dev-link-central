package dev.linkcentral.presentation.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMemberResponse {

    private Long memberId;

    public static ArticleMemberResponse toArticleMemberResponse(Long memberId) {
        return new ArticleMemberResponse(memberId);
    }
}
