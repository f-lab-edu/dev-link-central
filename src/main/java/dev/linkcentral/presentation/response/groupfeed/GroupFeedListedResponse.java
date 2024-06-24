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
public class GroupFeedListedResponse {

    private Page<GroupFeedWithProfileDTO> feeds;

    public static GroupFeedListedResponse toGroupFeedListResponse(Page<GroupFeedWithProfileDTO> groupFeeds) {
        return GroupFeedListedResponse.builder()
                .feeds(groupFeeds)
                .build();
    }
}
