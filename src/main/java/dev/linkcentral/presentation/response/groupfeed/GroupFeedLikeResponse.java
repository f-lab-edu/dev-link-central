package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedLikeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedLikeResponse {

    private int likeCount;
    private boolean liked;

    public static GroupFeedLikeResponse toGroupFeedLikeResponse(GroupFeedLikeDTO groupFeedLikeDTO) {
        return GroupFeedLikeResponse.builder()
                .likeCount(groupFeedLikeDTO.getLikeCount())
                .liked(groupFeedLikeDTO.isLiked())
                .build();
    }
}
