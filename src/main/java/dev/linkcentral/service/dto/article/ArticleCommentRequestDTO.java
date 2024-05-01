package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentRequestDTO {

    private Long id;
    private Long articleId;
    private String contents;
    private String nickname;
    private LocalDateTime createdAt;

}
