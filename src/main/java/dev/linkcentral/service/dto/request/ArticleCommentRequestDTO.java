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

    public static ArticleCommentRequestDTO toCommentDTO(ArticleComment comment) {
        ArticleCommentRequestDTO commentDTO = new ArticleCommentRequestDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setArticleId(comment.getArticle().getId()); // Article 엔티티에서 ID 가져오기
        commentDTO.setContents(comment.getContent());
        commentDTO.setNickname(comment.getMember().getNickname()); // Member 엔티티에서 닉네임 가져오기
        commentDTO.setCreatedAt(comment.getCreatedAt());
        return commentDTO;
    }

}