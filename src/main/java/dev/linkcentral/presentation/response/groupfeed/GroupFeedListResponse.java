package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedWithProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedListResponse {

    private Page<GroupFeedWithProfileDTO> feeds;

    public static GroupFeedListResponse toGroupFeedListResponse(Page<GroupFeedWithProfileDTO> groupFeeds) {
        return GroupFeedListResponse.builder()
                .feeds(groupFeeds)
                .build();
    }
}
