package dev.linkcentral.service.facade;

import dev.member.entity.Member;
import dev.linkcentral.service.FriendService;
import dev.member.service.MemberService;
import dev.linkcentral.service.dto.friend.FriendMemberInfoDTO;
import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import dev.linkcentral.service.dto.friend.FriendshipDetailDTO;
import dev.member.service.dto.MemberInfoResponse;
import dev.linkcentral.service.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendFacade {

    private final FriendMapper friendMapper;
    private final FriendService friendService;
    private final MemberService memberService;

    public Long sendFriendRequest(FriendRequestDTO friendRequestDTO) {
        Member sender = memberService.findById(friendRequestDTO.getSenderId());
        Member receiver = memberService.findById(friendRequestDTO.getReceiverId());
        return friendService.sendFriendRequest(sender, receiver);
    }

    public List<FriendRequestDTO> getReceivedFriendRequests(Long receiverId) {
        Member receiver = memberService.findById(receiverId);
        return friendService.getReceivedFriendRequests(receiver);
    }

    public void acceptFriendRequest(Long requestId) {
        friendService.acceptFriendRequest(requestId);
    }

    public Long findFriendshipId(Long senderId, Long receiverId) {
        Member sender = memberService.findById(senderId);
        Member receiver = memberService.findById(receiverId);
        return friendService.findFriendshipId(sender, receiver);
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

    public FriendMemberInfoDTO getMemberInfo() {
        MemberInfoResponse currentMember = memberService.getCurrentUserInfo();
        return friendMapper.toFriendMemberInfoDTO(currentMember);
    }
}
