package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleViewDTO {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long writerId;
    private String createdAt;
    private LocalDateTime modifiedAt;
    private int views;

}
