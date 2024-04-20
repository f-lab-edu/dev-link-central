package dev.linkcentral.presentation.dto.response.article;

import dev.linkcentral.presentation.dto.ArticleCommentViewDTO;
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
