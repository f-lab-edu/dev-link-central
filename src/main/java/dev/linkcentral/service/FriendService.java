package dev.linkcentral.service;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.FriendRepository;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.presentation.dto.request.friend.FriendRequest;
import dev.linkcentral.presentation.dto.response.friend.FriendListResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendshipDetailResponse;
import dev.linkcentral.service.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;

    @Transactional
    public Long sendFriendRequest(Long senderId, Long receiverId) {
        Member sender = findMemberById(senderId);
        Member receiver = findMemberById(receiverId);
        validateFriendRequest(sender, receiver);

        Friend friendEntity = friendMapper.createFriendRequest(sender, receiver);
        friendRepository.save(friendEntity);
        return friendEntity.getId();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    private void validateFriendRequest(Member sender, Member receiver) {
        boolean friendRequestExists = friendRepository.existsBySenderAndReceiverOrReceiverAndSender(sender, receiver, sender, receiver);
        if (friendRequestExists) {
            throw new IllegalStateException("친구 요청을 이미 보냈거나 받았습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<FriendRequest> getReceivedFriendRequests(Long receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다: " + receiverId));

        return friendRepository.findAllByReceiverAndStatus(receiver, FriendStatus.REQUESTED).stream()
                .map(friend -> new FriendRequest(
                        friend.getId(),
                        friend.getSender().getId(),
                        friend.getReceiver().getId(),
                        friend.getSender().getNickname()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(Long requestId) {
        Friend friendRequest = friendRepository.findByIdAndStatus(requestId, FriendStatus.REQUESTED)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));

        friendRequest.updateFriendStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendRequest);
    }

    @Transactional
    public void rejectFriendRequest(Long requestId) {
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));

        friendRepository.delete(friendRequest);
    }

    @Transactional
    public Long findFriendshipId(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("요청자 ID를 찾을 수 없습니다."));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("대상의 ID를 찾을 수 없습니다."));

        return friendRepository.findBySenderAndReceiver(sender, receiver)
                .map(Friend::getId)
                .orElse(null);
    }

    @Transactional
    public void deleteFriendship(Long friendId) {
        Friend friendship = friendRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("친구 관계를 찾을 수 없습니다."));

        friendRepository.delete(friendship);
    }

    @Transactional(readOnly = true)
    public List<FriendListResponse> getFriends(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾을 수 없습니다."));

        List<Friend> sentFriends = friendRepository.findBySenderAndStatus(member, FriendStatus.ACCEPTED);
        List<FriendListResponse> friendsFromSent = sentFriends.stream()
                .map(friend -> new FriendListResponse(friend.getReceiver().getId(), friend.getReceiver().getName()))
                .collect(Collectors.toList());

        List<Friend> receivedFriends = friendRepository.findByReceiverAndStatus(member, FriendStatus.ACCEPTED);
        List<FriendListResponse> friendsFromReceived = receivedFriends.stream()
                .map(friend -> new FriendListResponse(friend.getSender().getId(), friend.getSender().getName()))
                .collect(Collectors.toList());

        return Stream.concat(friendsFromSent.stream(), friendsFromReceived.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public void unfriendSelected(List<Long> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            throw new IllegalArgumentException("친구 ID는 null이거나 비워 둘 수 없습니다.");
        }

        for (Long friendId : friendIds) {
            Friend friend = friendRepository.findById(friendId)
                    .orElseThrow(() -> new EntityNotFoundException("친구를 찾을 수 없습니다."));

            friendRepository.delete(friend);
        }
    }

    @Transactional(readOnly = true)
    public List<FriendshipDetailResponse> getFriendships(Long memberId) {
        List<Friend> friendships = friendRepository.findBySenderOrReceiver(memberId);
        return friendships.stream()
                .map(friendship -> FriendshipDetailResponse.builder()
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
