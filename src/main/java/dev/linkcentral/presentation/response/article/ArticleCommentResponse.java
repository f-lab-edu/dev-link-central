package dev.linkcentral.presentation.response.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentResponse {

    private Long id;
    private Long articleId;
    private String contents;
    private String writerNickname;
    private LocalDateTime createdAt;
}
