package dev.linkcentral.presentation.response.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDeleteResponse {

    private boolean success;
    private String message;

    public static ArticleDeleteResponse toArticleDeleteResponse() {
        return ArticleDeleteResponse.builder()
                .success(true)
                .message("성공적으로 삭제되었습니다.")
                .build();
    }
}
