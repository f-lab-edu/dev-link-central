package dev.linkcentral.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateRequestDTO {

    private Long id;
    private String writer;
    private String title;
    private String content;

}