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
}
