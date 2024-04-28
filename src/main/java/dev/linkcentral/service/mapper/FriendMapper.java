package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.FriendRequestDTO;
import dev.linkcentral.service.dto.FriendshipDetailDTO;
import dev.linkcentral.presentation.request.friend.FriendRequest;
import dev.linkcentral.presentation.response.friend.FriendReceivedResponse;
import dev.linkcentral.presentation.response.friend.FriendRequestResponse;
import dev.linkcentral.presentation.response.friend.FriendshipDetailResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<FriendRequestDTO> toFriendRequestDTOList(List<Friend> friends) {
        return friends.stream()
                .map(friend -> new FriendRequestDTO(
                        friend.getId(),
                        friend.getSender().getId(),
                        friend.getReceiver().getId(),
                        friend.getSender().getNickname()
                ))
                .collect(Collectors.toList());
    }

    public FriendReceivedResponse buildFriendReceivedResponse(List<FriendRequestDTO> friendRequests) {
        if (friendRequests == null || friendRequests.isEmpty()) {
            return new FriendReceivedResponse(false, "친구 요청 목록이 비어있습니다.", null);
        } else {
            return new FriendReceivedResponse(true, "친구 요청 목록을 성공적으로 불러왔습니다.", friendRequests);
        }
    }

    public List<FriendshipDetailDTO> toFriendshipDetailDTOList(List<Friend> friendships) {
        return friendships.stream()
                .map(friendship -> FriendshipDetailDTO.builder()
                        .friendshipId(friendship.getId())
                        .senderId(friendship.getSender().getId())
                        .receiverId(friendship.getReceiver().getId())
                        .senderName(friendship.getSender().getName())
                        .receiverName(friendship.getReceiver().getName())
                        .status(friendship.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public List<FriendshipDetailResponse> toFriendshipDetailResponseList(List<FriendshipDetailDTO> friendshipDetails) {
        return friendshipDetails.stream()
                .map(dto -> FriendshipDetailResponse.builder()
                        .friendshipId(dto.getFriendshipId())
                        .senderId(dto.getSenderId())
                        .receiverId(dto.getReceiverId())
                        .senderName(dto.getSenderName())
                        .receiverName(dto.getReceiverName())
                        .status(dto.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

}
