package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import dev.linkcentral.service.dto.friend.FriendshipDetailDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FriendMapper {

    public Friend createFriendRequest(Member sender, Member receiver) {
        return Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.REQUESTED)
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

}
