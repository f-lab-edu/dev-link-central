package dev.linkcentral.service.dto.groupfeed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCommentUpdateDTO {

    private Long feedId;
    private Long commentId;
    private String content;
}
