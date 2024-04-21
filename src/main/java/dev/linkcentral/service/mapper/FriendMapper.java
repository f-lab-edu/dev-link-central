package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.FriendRequestDTO;
import dev.linkcentral.presentation.dto.request.friend.FriendRequest;
import dev.linkcentral.presentation.dto.response.friend.FriendRequestResponse;
import org.springframework.stereotype.Component;

@Component
public class FriendMapper {

    public FriendRequestDTO toFriendRequestDTO(FriendRequest friendRequest) {
        return FriendRequestDTO.builder()
                .senderId(friendRequest.getSenderId())
                .receiverId(friendRequest.getReceiverId())
                .build();
    }

    public Friend createFriendRequest(Member sender, Member receiver) {
        return Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.REQUESTED)
                .build();
    }

    public FriendRequestResponse createFriendRequestResponse(Long friendRequestId) {
        return FriendRequestResponse.builder()
                .friendRequestId(friendRequestId)
                .message("친구 요청이 성공적으로 보내졌습니다.")
                .build();
    }

}
