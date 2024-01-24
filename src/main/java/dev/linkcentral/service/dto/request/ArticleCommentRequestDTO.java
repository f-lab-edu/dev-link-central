package dev.linkcentral.service.dto.request;

import dev.linkcentral.database.entity.ArticleComment;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class ArticleCommentRequestDTO {

    private Long id;
    private Long articleId;
    private String contents;
    private String nickname;
    private LocalDateTime createdAt;

    public static ArticleCommentRequestDTO toCommentDTO(ArticleComment comment, Long articleId) {
        ArticleCommentRequestDTO commentDTO = new ArticleCommentRequestDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setArticleId(articleId);
        commentDTO.setContents(comment.getContent());
        commentDTO.setNickname(comment.getWriterNickname());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        return commentDTO;
    }

}
