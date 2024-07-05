package dev.linkcentral.presentation.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMemberInfoResponse {

    private Long memberId;

    public static ArticleMemberInfoResponse toArticleMemberResponse(Long memberId) {
        return new ArticleMemberInfoResponse(memberId);
    }
}
