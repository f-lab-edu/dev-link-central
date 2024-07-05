package dev.linkcentral.presentation.response.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipIdResponse {

    private Long friendshipId;

    public static FriendshipIdResponse toFriendshipId(Long friendshipId) {
        return new FriendshipIdResponse(friendshipId);
    }
}
