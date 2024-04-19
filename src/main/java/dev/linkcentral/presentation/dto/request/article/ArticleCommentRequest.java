package dev.linkcentral.presentation.dto.request.article;

import dev.linkcentral.database.entity.ArticleComment;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class ArticleCommentRequest {

    private Long id;
    private Long articleId;
    private String contents;
    private String nickname;
    private LocalDateTime createdAt;

    public static ArticleCommentRequest toCommentDTO(ArticleComment comment) {
        ArticleCommentRequest commentDTO = new ArticleCommentRequest();
        commentDTO.setId(comment.getId());
        commentDTO.setArticleId(comment.getArticle().getId()); // Article 엔티티에서 ID 가져오기
        commentDTO.setContents(comment.getContent());

        if (comment.getMember() != null) {
            commentDTO.setNickname(comment.getMember().getNickname());
        }
        commentDTO.setCreatedAt(comment.getCreatedAt());
        return commentDTO;
    }

}