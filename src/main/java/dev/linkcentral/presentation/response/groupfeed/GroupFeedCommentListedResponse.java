package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedCommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCommentListedResponse {

    private List<GroupFeedCommentDTO> comments;

    public static GroupFeedCommentListedResponse toGroupFeedCommentResponse(List<GroupFeedCommentDTO> comments) {
        return GroupFeedCommentListedResponse.builder()
                .comments(comments)
                .build();
    }
}
