package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleComment;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.request.article.ArticleCommentRequest;
import dev.linkcentral.presentation.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.request.article.ArticleUpdateRequest;
import dev.linkcentral.presentation.response.article.ArticleCreateResponse;
import dev.linkcentral.presentation.response.article.ArticleDeleteResponse;
import dev.linkcentral.presentation.response.article.ArticleDetailsResponse;
import dev.linkcentral.presentation.response.article.ArticleLikeResponse;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    public ArticleCreateDTO toArticleCreateDTO(ArticleCreateRequestDTO request, Member member) {
        return ArticleCreateDTO.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(member.getNickname())
                .writerId(member.getId())
                .build();
    }

    public ArticleCreateDTO toArticleCreateDTO(Article article) {
        return ArticleCreateDTO.builder()
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .writerId(article.getMember().getId())
                .build();
    }

    public Article toArticleEntity(ArticleCreateDTO dto, Member member) {
        return Article.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member.getNickname())
                .member(member)
                .build();
    }

    public ArticleCreateResponse toArticleCreateResponse(ArticleCreateDTO articleDTO) {
        return new ArticleCreateResponse(
                true,
                "글이 성공적으로 작성되었습니다.",
                articleDTO.getWriterId(),
                articleDTO.getTitle()
        );
    }

    public ArticleUpdateDTO toArticleUpdateDTO(ArticleUpdateRequestDTO updateRequestDTO) {
        return new ArticleUpdateDTO(
                updateRequestDTO.getId(),
                updateRequestDTO.getWriter(),
                updateRequestDTO.getTitle(),
                updateRequestDTO.getContent()
        );
    }

    public Article toUpdateEntity(ArticleUpdateDTO articleDTO) {
        return Article.builder()
                .id(articleDTO.getId())
                .title(articleDTO.getTitle())
                .content(articleDTO.getContent())
                .writer(articleDTO.getWriter())
                .build();
    }

    public ArticleUpdatedDTO updateArticleAndReturnDTO(Article article) {
        ArticleUpdatedDTO updatedDTO = ArticleUpdatedDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .build();

        if (article.getMember() != null) {
            updatedDTO.setWriterId(article.getMember().getId());
        }
        return updatedDTO;
    }

    public ArticleDeleteResponse toArticleDeleteResponse() {
        return ArticleDeleteResponse.builder()
                .success(true)
                .message("성공적으로 삭제되었습니다.")
                .build();
    }

    public ArticleLikeResponse toArticleLikeResponse(ArticleLikeDTO dto) {
        return new ArticleLikeResponse(
                dto.isLiked(),
                dto.getTotalLikes()
        );
    }

    public ArticleDetailsDTO toArticleDetailsDTO(Article article) {
        return new ArticleDetailsDTO(
                article.getId(),
                article.getMember(),
                article.getTitle(),
                article.getContent(),
                article.getWriter(),
                article.getCreatedAt(),
                article.getModifiedAt()
        );
    }

    public ArticleDetailsResponse toArticleDetailsResponse(ArticleDetailsDTO articleDetailsDTO) {
        return new ArticleDetailsResponse(
                true,
                "게시글 조회 성공",
                articleDetailsDTO
        );
    }

    public ArticleViewDTO toArticleDTO(Article article) {
        ArticleViewDTO articleViewDTO = ArticleViewDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .build();

        // Member 객체가 존재하는 경우에만 writerId 설정
        if (article.getMember() != null) {
            articleViewDTO.setWriterId(article.getMember().getId());
        }
        return articleViewDTO;
    }

    public ArticleViewDTO toDetailedArticleDTO(Article article, int views) {
        return ArticleViewDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .views(views)
                .build();
    }

    public ArticleCommentViewDTO toCommentDTO(ArticleComment comment) {
        ArticleCommentViewDTO commentDTO = new ArticleCommentViewDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setArticleId(comment.getArticle().getId());
        commentDTO.setContents(comment.getContent());

        if (comment.getMember() != null) {
            commentDTO.setNickname(comment.getMember().getNickname());
        }
        commentDTO.setCreatedAt(comment.getCreatedAt());
        return commentDTO;
    }

    public ArticleCreateRequestDTO toArticleCreateCommand(ArticleCreateRequest articleCreateRequest) {
        return ArticleCreateRequestDTO.builder()
                .id(articleCreateRequest.getId())
                .title(articleCreateRequest.getTitle())
                .content(articleCreateRequest.getContent())
                .writer(articleCreateRequest.getWriter())
                .writerId(articleCreateRequest.getWriterId())
                .createdAt(articleCreateRequest.getCreatedAt())
                .modifiedAt(articleCreateRequest.getModifiedAt())
                .views(articleCreateRequest.getViews())
                .build();
    }

    public ArticleUpdateRequestDTO toArticleUpdateRequestCommand(ArticleUpdateRequest updateRequest) {
        return ArticleUpdateRequestDTO.builder()
                .id(updateRequest.getId())
                .writer(updateRequest.getWriter())
                .title(updateRequest.getTitle())
                .content(updateRequest.getContent())
                .build();
    }

    public ArticleCommentRequestDTO toArticleCommentRequestCommand(ArticleCommentRequest commentRequest) {
        return new ArticleCommentRequestDTO(
                commentRequest.getId(),
                commentRequest.getArticleId(),
                commentRequest.getContents(),
                commentRequest.getNickname(),
                commentRequest.getCreatedAt()
        );
    }

    public MemberCurrentDTO toMemberCurrentDTO(Member currentMember) {
        return new MemberCurrentDTO(currentMember);
    }

}
