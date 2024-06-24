package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedWithProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GroupFeedWithProfileResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String profileImageUrl;
    private int likeCount;

    public static GroupFeedWithProfileResponse toGroupFeedWithProfileResponse(GroupFeedWithProfileDTO groupProfileDTO) {
        return GroupFeedWithProfileResponse.builder()
                .id(groupProfileDTO.getId())
                .title(groupProfileDTO.getTitle())
                .content(groupProfileDTO.getContent())
                .writer(groupProfileDTO.getWriter())
                .imageUrl(groupProfileDTO.getImageUrl())
                .createdAt(groupProfileDTO.getCreatedAt())
                .profileImageUrl(groupProfileDTO.getProfileImageUrl())
                .likeCount(groupProfileDTO.getLikeCount())
                .build();
    }
}
