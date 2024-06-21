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
public class GroupFeedCommentResponse {

    private List<GroupFeedCommentDTO> comments;

    public static GroupFeedCommentResponse toGroupFeedCommentResponse(List<GroupFeedCommentDTO> comments) {
        return GroupFeedCommentResponse.builder()
                .comments(comments)
                .build();
    }
}
