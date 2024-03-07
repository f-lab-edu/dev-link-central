package dev.linkcentral.presentation.dto.request;

import dev.linkcentral.database.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int views;

    // Entity -> DTO 변환
    public static ArticleRequest toArticleDTOWithViews(Article article, int views) {
        return ArticleRequest.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .views(views)
                .build();
    }

    // Entity -> DTO 변환
    public static ArticleRequest toArticleDTO(Article article) {
        ArticleRequest dto = ArticleRequest.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .writer(article.getWriter())
                .createdAt(article.getCreatedAt())
                .modifiedAt(article.getModifiedAt())
                .build();

        // Member 객체가 존재하는 경우에만 writerId 설정
        if (article.getMember() != null) {
            dto.setWriterId(article.getMember().getId());
        }
        return dto;
    }

    public ArticleRequest(Long id, String title, String writer, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.createdAt = createdAt;
    }
}