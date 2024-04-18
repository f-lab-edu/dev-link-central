package dev.linkcentral.presentation.dto.response;

import dev.linkcentral.presentation.dto.ArticleUpdatedDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateResponse {

    private int status;
    private ArticleUpdatedDTO articleUpdatedDTO;
}
