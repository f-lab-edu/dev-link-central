package dev.linkcentral.presentation.response.friend;

import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendReceivedResponse {

    private boolean success;
    private String message;
    private List<FriendRequestDTO> friendRequests;

    public static FriendReceivedResponse toFriendReceivedResponse(List<FriendRequestDTO> friendRequests) {
        if (friendRequests == null || friendRequests.isEmpty()) {
            return new FriendReceivedResponse(false, "친구 요청 목록이 비어있습니다.", null);
        } else {
            return new FriendReceivedResponse(true, "친구 요청 목록을 성공적으로 불러왔습니다.", friendRequests);
        }
    }
}
