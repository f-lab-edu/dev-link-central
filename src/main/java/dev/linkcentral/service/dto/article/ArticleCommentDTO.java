package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentDTO {

    private Long id;
    private Long articleId;
    private String contents;
    private String writerNickname;
    private LocalDateTime createdAt;
}
