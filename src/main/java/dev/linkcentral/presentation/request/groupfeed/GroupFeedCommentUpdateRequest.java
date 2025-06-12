package dev.linkcentral.presentation.request.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedCommentUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCommentUpdateRequest {

    private Long feedId;
    private Long commentId;

    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    public static GroupFeedCommentUpdateDTO toGroupFeedCommentUpdateCommand(
            Long feedId, Long commentId, GroupFeedCommentUpdateRequest commentUpdateRequest) {

        GroupFeedCommentUpdateDTO updateRequest = new GroupFeedCommentUpdateDTO();
        updateRequest.setFeedId(feedId);
        updateRequest.setCommentId(commentId);
        updateRequest.setContent(commentUpdateRequest.getContent());
        return updateRequest;
    }
}
