package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.article.Article;
import dev.linkcentral.database.entity.article.ArticleComment;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public ArticleViewDTO toArticleDTO(Article article) {
        String localDateTime = DateUtils.formatLocalDateTime(article.getCreatedAt());
        Long writerId = Optional.ofNullable(article.getMember())
                .map(Member::getId)
                .orElse(null);

        return ArticleViewDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .writerId(writerId)
                .createdAt(localDateTime)
                .modifiedAt(article.getModifiedAt())
                .build();
    }

    public ArticleViewDTO toDetailedArticleDTO(Article article, int views) {
        String localDateTime = DateUtils.formatLocalDateTime(article.getCreatedAt());
        Long writerId = Optional.ofNullable(article.getMember())
                .map(Member::getId)
                .orElse(null);

        return ArticleViewDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .writerId(writerId)
                .createdAt(localDateTime)
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
        String localDateTime = DateUtils.formatLocalDateTime(comment.getCreatedAt());
        commentDTO.setCreatedAt(localDateTime);
        return commentDTO;
    }

    public MemberCurrentDTO toMemberCurrentDTO(Long memberId) {
        return MemberCurrentDTO.builder()
                .memberId(memberId)
                .build();
    }

    public MemberCurrentDTO toMemberCurrentDTO(Long memberId, String nickname) {
        return MemberCurrentDTO.builder()
                .memberId(memberId)
                .nickname(nickname)
                .build();
    }

    public static ArticleCreatedAtDTO toArticleCreatedAtDTO(ArticleViewDTO article) {
        return ArticleCreatedAtDTO.builder()
                .id(article.getId())
                .formattedCreatedAt(article.getCreatedAt())
                .build();
    }

    public ArticleCurrentMemberDTO toCurrentMemberIdDTO(Long id) {
        return new ArticleCurrentMemberDTO(id);
    }
}
