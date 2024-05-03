package dev.linkcentral.presentation.response.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipResponse {

    private Long friendshipId;

    public static FriendshipResponse toFriendshipId(Long friendshipId) {
        return new FriendshipResponse(friendshipId);
    }
}
