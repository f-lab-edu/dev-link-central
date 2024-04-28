package dev.linkcentral.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCreateDTO {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int views;
}
