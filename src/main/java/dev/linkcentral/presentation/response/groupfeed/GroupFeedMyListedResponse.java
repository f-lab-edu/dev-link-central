package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.MyGroupFeedDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedMyListedResponse {

    private List<MyGroupFeedDetailsDTO> feeds;

    public static GroupFeedMyListedResponse toMyFeedListResponse(List<MyGroupFeedDetailsDTO> memberFeeds) {
        return GroupFeedMyListedResponse.builder()
                .feeds(memberFeeds)
                .build();
    }
}