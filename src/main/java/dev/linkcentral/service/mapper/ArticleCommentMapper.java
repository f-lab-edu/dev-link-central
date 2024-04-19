package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.ArticleCommentDTO;
import dev.linkcentral.presentation.dto.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.dto.response.article.ArticleCommentResponse;
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

    public ArticleCommentResponse createCommentResponse(ArticleCommentDTO savedCommentDTO) {
        return new ArticleCommentResponse(
                savedCommentDTO.getId(),
                savedCommentDTO.getArticleId(),
                savedCommentDTO.getContents(),
                savedCommentDTO.getWriterNickname(),
                savedCommentDTO.getCreatedAt()
        );
    }

    public ArticleCommentDTO toArticleCommentDTO(ArticleCommentRequest request, String writerNickname) {
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
}
