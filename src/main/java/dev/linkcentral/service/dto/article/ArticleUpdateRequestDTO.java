package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateRequestDTO {

    private Long id;
    private Long writerId;
    private String writer;
    private String title;
    private String content;
}
