package dev.linkcentral.service.facade;

import dev.linkcentral.service.FriendService;
import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import dev.linkcentral.service.dto.friend.FriendshipDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendFacade {

    private final FriendService friendService;

    public Long sendFriendRequest(FriendRequestDTO friendRequestDTO) {
        return friendService.sendFriendRequest(friendRequestDTO.getSenderId(), friendRequestDTO.getReceiverId());
    }

    public List<FriendRequestDTO> getReceivedFriendRequests(Long receiverId) {
         return friendService.getReceivedFriendRequests(receiverId);
    }

    public void acceptFriendRequest(Long requestId) {
        friendService.acceptFriendRequest(requestId);
    }

    public Long findFriendshipId(Long senderId, Long receiverId) {
        return friendService.findFriendshipId(senderId, receiverId);
    }

    public void rejectFriendRequest(Long requestId) {
        friendService.rejectFriendRequest(requestId);
    }

    public void unfriend(Long friendId) {
        friendService.deleteFriendship(friendId);
    }

    public void unfriendSelected(List<Long> friendIds) {
        friendService.unfriendSelected(friendIds);
    }

    public List<FriendshipDetailDTO> getFriendships(Long memberId) {
         return friendService.getFriendships(memberId);
    }

}
