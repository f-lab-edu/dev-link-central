package dev.linkcentral.service.dto.groupfeed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupFeedDetailsDTO {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String profileImageUrl;
}