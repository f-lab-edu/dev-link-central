package dev.linkcentral.presentation.request.article;

import dev.linkcentral.service.dto.article.ArticleUpdateRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long id;

    @NotNull(message = "작성자 ID는 필수입니다.")
    private Long writerId;

    @NotBlank(message = "게시글 작성자는 필수입니다.")
    private String writer;

    @NotBlank(message = "게시글 제목은 필수입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    public static ArticleUpdateRequestDTO toArticleUpdateRequestCommand(ArticleUpdateRequest updateRequest) {
        return ArticleUpdateRequestDTO.builder()
                .id(updateRequest.getId())
                .writerId(updateRequest.getWriterId())
                .writer(updateRequest.getWriter())
                .title(updateRequest.getTitle())
                .content(updateRequest.getContent())
                .build();
    }
}
