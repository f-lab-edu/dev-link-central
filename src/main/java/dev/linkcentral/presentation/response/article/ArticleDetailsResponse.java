package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailsResponse {

    private boolean success;
    private String message;
    private ArticleDetailsDTO articleDetails;

}
