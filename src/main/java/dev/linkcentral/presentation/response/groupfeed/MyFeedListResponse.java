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
public class MyFeedListResponse {

    private List<MyGroupFeedDetailsDTO> feeds;

    public static MyFeedListResponse toMyFeedListResponse(List<MyGroupFeedDetailsDTO> memberFeeds) {
        return MyFeedListResponse.builder()
                .feeds(memberFeeds)
                .build();
    }
}