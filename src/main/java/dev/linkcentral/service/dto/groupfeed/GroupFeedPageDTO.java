package dev.linkcentral.service.dto.groupfeed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedPageDTO {

    private List<GroupFeedDTO> feeds;
    private int offset;
    private int limit;
    private long total;
}
