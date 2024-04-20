package dev.linkcentral.presentation.dto.response.article;

import dev.linkcentral.presentation.dto.ArticleDetailsDTO;
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
