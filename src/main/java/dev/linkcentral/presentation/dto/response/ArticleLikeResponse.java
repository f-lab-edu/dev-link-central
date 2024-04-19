package dev.linkcentral.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikeResponse {

    private boolean isLiked;
    private int totalLikes;
}
