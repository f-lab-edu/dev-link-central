package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.ArticleCreateDTO;
import dev.linkcentral.presentation.dto.request.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.response.ArticleCreateResponse;
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

}
