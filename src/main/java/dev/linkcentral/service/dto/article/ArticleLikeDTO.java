package dev.linkcentral.service.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLikeDTO {

    private boolean isLiked;
    private int totalLikes;
}
