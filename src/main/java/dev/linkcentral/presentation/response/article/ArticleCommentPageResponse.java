package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCommentViewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentPageResponse {

    private List<ArticleCommentViewDTO> comments;
    private int currentPage;
    private int totalPages;

    public static ArticleCommentPageResponse toArticleCommentPageResponse(Page<ArticleCommentViewDTO> commentsPage) {
        return new ArticleCommentPageResponse(
                commentsPage.getContent(),
                commentsPage.getNumber(),
                commentsPage.getTotalPages()
        );
    }
}
