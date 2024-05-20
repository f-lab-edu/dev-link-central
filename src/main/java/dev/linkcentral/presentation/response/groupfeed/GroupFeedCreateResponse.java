package dev.linkcentral.presentation.response.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedSavedDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCreateResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private String imageUrl;

    public static GroupFeedCreateResponse toGroupFeedCreateResponse(GroupFeedSavedDTO groupFeedSavedDTO) {
        return GroupFeedCreateResponse.builder()
                .id(groupFeedSavedDTO.getId())
                .title(groupFeedSavedDTO.getTitle())
                .content(groupFeedSavedDTO.getContent())
                .writer(groupFeedSavedDTO.getWriter())
                .imageUrl(groupFeedSavedDTO.getImageUrl())
                .build();
    }
}
