package dev.linkcentral.presentation.response.article;

import dev.linkcentral.service.dto.article.ArticleCommentViewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCommentPageResponse {

    private List<ArticleCommentViewDTO> comments;
    private int currentPage;
    private int totalPages;

}
