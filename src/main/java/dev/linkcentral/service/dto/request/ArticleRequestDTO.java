package dev.linkcentral.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequestDTO {

    private String title;
    private String content;
    private String writer;

}