package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.article.ArticleCommentDTO;
import dev.linkcentral.service.dto.article.ArticleCommentRequestDTO;
import dev.linkcentral.service.dto.article.ArticleCommentUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class ArticleCommentMapper {

    public ArticleComment toArticleCommentEntity(ArticleCommentDTO articleCommentDTO, String writerNickname,
                                                 Article article, Member member) {
        return ArticleComment.builder()
                .id(articleCommentDTO.getId())
                .article(article)
                .member(member)
                .content(articleCommentDTO.getContents())
                .writerNickname(writerNickname)
                .build();
    }

    public ArticleCommentDTO toArticleCommentDTO(ArticleCommentRequestDTO request, String writerNickname) {
        ArticleCommentDTO commentDTO = new ArticleCommentDTO();
        commentDTO.setId(request.getId());
        commentDTO.setArticleId(request.getArticleId());
        commentDTO.setContents(request.getContents());
        commentDTO.setWriterNickname(writerNickname);
        commentDTO.setCreatedAt(request.getCreatedAt());
        return commentDTO;
    }

    public ArticleCommentDTO toArticleCommentDTO(ArticleComment commentEntity) {
        return ArticleCommentDTO.builder()
                .id(commentEntity.getId())
                .articleId(commentEntity.getArticle().getId())
                .contents(commentEntity.getContent())
                .writerNickname(commentEntity.getMember().getNickname())
                .createdAt(commentEntity.getCreatedAt())
                .build();
    }

    public ArticleCommentUpdateDTO toArticleCommentUpdateDto(ArticleCommentRequestDTO request, Long commentId) {
        return new ArticleCommentUpdateDTO(
                commentId,
                request.getArticleId(),
                request.getContents(),
                request.getNickname(),
                request.getCreatedAt()
        );
    }

    public ArticleCommentUpdateDTO toArticleCommentUpdateDto(ArticleComment comment) {
        return new ArticleCommentUpdateDTO(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getCreatedAt()
        );
    }

}
