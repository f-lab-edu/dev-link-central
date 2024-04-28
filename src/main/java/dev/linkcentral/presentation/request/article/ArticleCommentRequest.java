package dev.linkcentral.presentation.request.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentRequest {

    private Long id;
    private Long articleId;
    private String contents;
    private String nickname;
    private LocalDateTime createdAt;

}
