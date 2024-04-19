package dev.linkcentral.presentation.dto.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentUpdateResponse {

    private Long id;
    private String contents;
    private String writerNickname;
}
