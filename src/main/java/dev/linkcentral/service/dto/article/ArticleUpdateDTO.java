package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateDTO {

    private Long id;
    private Long writerId;
    private String writer;
    private String title;
    private String content;

    public void updateWriterId(Long writerId) {
        this.writerId = writerId;
    }
}
