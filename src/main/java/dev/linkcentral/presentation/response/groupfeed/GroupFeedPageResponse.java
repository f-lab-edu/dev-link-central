package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedDTO;
import dev.linkcentral.service.dto.groupfeed.GroupFeedPageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedPageResponse {

    private List<GroupFeedDTO> feeds;
    private int offset;
    private int limit;
    private long total;

    public static GroupFeedPageResponse toGroupFeedPageResponse(GroupFeedPageDTO groupFeedPageDTO) {
        return GroupFeedPageResponse.builder()
                .feeds(groupFeedPageDTO.getFeeds())
                .offset(groupFeedPageDTO.getOffset())
                .limit(groupFeedPageDTO.getLimit())
                .total(groupFeedPageDTO.getTotal())
                .build();
    }
}
