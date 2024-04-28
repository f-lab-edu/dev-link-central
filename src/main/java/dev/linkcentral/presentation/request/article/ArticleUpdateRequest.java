package dev.linkcentral.presentation.request.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateRequest {

    private Long id;
    private String writer;
    private String title;
    private String content;

}
