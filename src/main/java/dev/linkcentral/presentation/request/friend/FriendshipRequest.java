package dev.linkcentral.presentation.request.friend;

import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipRequest {

    private Long id;

    @NotNull(message = "보내는 사람의 ID는 필수입니다.")
    private Long senderId;

    @NotNull(message = "받는 사람의 ID는 필수입니다.")
    private Long receiverId;

    public static FriendRequestDTO toFriendRequestCommand(FriendshipRequest friendshipRequest) {
        return FriendRequestDTO.builder()
                .senderId(friendshipRequest.getSenderId())
                .receiverId(friendshipRequest.getReceiverId())
                .build();
    }

}
