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
public class ArticleCommentPagedResponse {

    private List<ArticleCommentViewDTO> comments;
    private int currentPage;
    private int totalPages;
    private boolean hasMoreComments;

    public static ArticleCommentPagedResponse toArticleCommentPageResponse(Page<ArticleCommentViewDTO> commentsPage, boolean hasMoreComments) {
        return new ArticleCommentPagedResponse(
                commentsPage.getContent(),
                commentsPage.getNumber(),
                commentsPage.getTotalPages(),
                hasMoreComments
        );
    }
}
