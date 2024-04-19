package dev.linkcentral.presentation.dto.response.article;

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

}
