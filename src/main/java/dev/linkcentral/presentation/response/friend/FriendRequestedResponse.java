package dev.linkcentral.presentation.response.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestedResponse {

    private Long friendRequestId;
    private String message;

    public static FriendRequestedResponse toFriendRequestResponse(Long friendRequestId) {
        return FriendRequestedResponse.builder()
                .friendRequestId(friendRequestId)
                .message("친구 요청이 성공적으로 보내졌습니다.")
                .build();
    }
}
