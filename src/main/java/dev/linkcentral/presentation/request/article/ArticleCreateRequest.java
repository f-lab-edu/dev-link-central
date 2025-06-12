package dev.linkcentral.presentation.request.article;

import dev.linkcentral.service.dto.article.ArticleCreateRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCreateRequest {

    private Long id;

    @NotBlank(message = "게시글 제목은 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    @NotBlank(message = "작성자 이름은 필수 입력 항목입니다.")
    private String writer;

    private Long writerId;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private int views;

    public ArticleCreateRequest(Long id, String title, String writer, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public static ArticleCreateRequestDTO toArticleCreateCommand(ArticleCreateRequest articleCreateRequest) {
        return ArticleCreateRequestDTO.builder()
                .id(articleCreateRequest.getId())
                .title(articleCreateRequest.getTitle())
                .content(articleCreateRequest.getContent())
                .writer(articleCreateRequest.getWriter())
                .writerId(articleCreateRequest.getWriterId())
                .createdAt(articleCreateRequest.getCreatedAt())
                .modifiedAt(articleCreateRequest.getModifiedAt())
                .views(articleCreateRequest.getViews())
                .build();
    }
}