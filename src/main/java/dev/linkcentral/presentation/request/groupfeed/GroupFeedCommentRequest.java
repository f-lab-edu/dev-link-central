package dev.linkcentral.presentation.request.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedCommentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCommentRequest {

    private String content;

    public static GroupFeedCommentDTO toGroupFeedCommentCommand(GroupFeedCommentRequest commentRequest) {
        return GroupFeedCommentDTO.builder()
                .content(commentRequest.getContent())
                .build();
    }
}
