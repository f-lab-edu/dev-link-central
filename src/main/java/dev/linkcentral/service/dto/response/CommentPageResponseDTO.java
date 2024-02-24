package dev.linkcentral.service.dto.response;

import dev.linkcentral.service.dto.request.ArticleCommentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentPageResponseDTO {

    private List<ArticleCommentRequestDTO> comments;
    private int currentPage;
    private int totalPages;
}