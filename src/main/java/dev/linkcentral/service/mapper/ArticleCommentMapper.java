package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.article.ArticleCommentDTO;
import dev.linkcentral.service.dto.article.ArticleCommentRequestDTO;
import dev.linkcentral.service.dto.article.ArticleCommentUpdateDTO;
import dev.linkcentral.service.dto.article.ArticleCommentViewDTO;
import dev.linkcentral.presentation.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.response.article.ArticleCommentPageResponse;
import dev.linkcentral.presentation.response.article.ArticleCommentResponse;
import dev.linkcentral.presentation.response.article.ArticleCommentUpdateResponse;
import org.springframework.data.domain.Page;
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

    public ArticleCommentUpdateResponse toArticleCommentUpdateResponse(ArticleCommentUpdateDTO commentUpdateDTO) {
        return new ArticleCommentUpdateResponse(
                commentUpdateDTO.getId(),
                commentUpdateDTO.getContents(),
                commentUpdateDTO.getNickname()
        );
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

    public ArticleCommentPageResponse toArticleCommentPageResponse(Page<ArticleCommentViewDTO> commentsPage) {
        return new ArticleCommentPageResponse(
                commentsPage.getContent(),
                commentsPage.getNumber(),
                commentsPage.getTotalPages()
        );
    }

}
