package dev.linkcentral.presentation.dto.response.article;

import dev.linkcentral.presentation.dto.request.article.ArticleCommentRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageResponse {

    private List<ArticleCommentRequest> comments;
    private int currentPage;
    private int totalPages;
}