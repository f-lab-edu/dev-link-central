package dev.linkcentral.service.dto.groupfeed;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupFeedDTO {

    private Long id;
    private Long memberId;
    private String writer;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
}
