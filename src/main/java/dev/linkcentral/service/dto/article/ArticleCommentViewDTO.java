package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentViewDTO {

    private Long id;
    private Long articleId;
    private String contents;
    private String nickname;
    private String createdAt;

}
