package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.ArticleCreateDTO;
import dev.linkcentral.presentation.dto.ArticleLikeDTO;
import dev.linkcentral.presentation.dto.ArticleUpdateDTO;
import dev.linkcentral.presentation.dto.ArticleUpdatedDTO;
import dev.linkcentral.presentation.dto.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.request.article.ArticleUpdateRequest;
import dev.linkcentral.presentation.dto.response.article.ArticleCreateResponse;
import dev.linkcentral.presentation.dto.response.article.ArticleDeleteResponse;
import dev.linkcentral.presentation.dto.response.article.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleMapper {

    public ArticleCreateDTO toArticleCreateDTO(ArticleCreateRequest request, Member member) {
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

    public ArticleUpdateDTO toArticleUpdateDTO(ArticleUpdateRequest request) {
        return new ArticleUpdateDTO(
                request.getId(),
                request.getWriter(),
                request.getTitle(),
                request.getContent()
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

    public ArticleDeleteResponse toArticleDeleteResponse(boolean isSuccess, String message) {
        return ArticleDeleteResponse.builder()
                .success(isSuccess)
                .message(message)
                .build();
    }

    public ArticleLikeResponse toArticleLikeResponse(ArticleLikeDTO dto) {
        return new ArticleLikeResponse(
                dto.isLiked(),
                dto.getTotalLikes()
        );
    }
}
