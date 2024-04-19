package dev.linkcentral.presentation.dto.response.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponse {

    private Long id;
    private String name;
}
