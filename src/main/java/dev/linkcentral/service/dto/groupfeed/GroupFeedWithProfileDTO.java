package dev.linkcentral.service.dto.groupfeed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GroupFeedWithProfileDTO {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String profileImageUrl;
    private int likeCount;
}
