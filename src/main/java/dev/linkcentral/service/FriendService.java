package dev.linkcentral.service;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.FriendRepository;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.presentation.dto.request.FriendRequest;
import dev.linkcentral.presentation.dto.response.FriendListResponse;
import dev.linkcentral.presentation.dto.response.FriendshipDetailResponse;
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

    /**
     * 친구 요청 보내기
     */
    @Transactional
    public Long sendFriendRequest(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다: " + senderId));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다: " + receiverId));

        if (friendRepository.existsBySenderAndReceiverOrReceiverAndSender(sender, receiver, sender, receiver)) {
            throw new IllegalStateException("친구 요청을 이미 보냈거나 받았습니다.");
        }

        Friend friendRequest = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.REQUESTED)
                .build();

        friendRepository.save(friendRequest);
        return friendRequest.getId();
    }

    /**
     * 받은 친구 요청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FriendRequest> getReceivedFriendRequests(Long receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다: " + receiverId));

        return friendRepository.findAllByReceiverAndStatus(receiver, FriendStatus.REQUESTED).stream()
                .map(friend -> new FriendRequest(
                        friend.getId(), // 친구 요청의 ID 추가
                        friend.getSender().getId(),
                        friend.getReceiver().getId(),
                        friend.getSender().getNickname()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 수락
     */
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        Friend friendRequest = friendRepository.findByIdAndStatus(requestId, FriendStatus.REQUESTED)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));

        friendRequest.updateFriendStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendRequest);
    }

    /**
     * 친구 요청 거절
     */
    @Transactional
    public void rejectFriendRequest(Long requestId) {
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));

        friendRepository.delete(friendRequest);
    }

    /**
     * 친구 요청 상태 조회
     */
    @Transactional
    public Long findFriendshipId(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("요청자 ID를 찾을 수 없습니다."));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("대상의 ID를 찾을 수 없습니다."));

        return friendRepository.findBySenderAndReceiver(sender, receiver)
                .map(Friend::getId)
                .orElse(null); // 이 부분을 변경
    }

    /**
     * 친구 관계를 끊기
     */
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

        // 현재 멤버가 sender인 경우
        List<Friend> sentFriends = friendRepository.findBySenderAndStatus(member, FriendStatus.ACCEPTED);
        List<FriendListResponse> friendsFromSent = sentFriends.stream()
                .map(friend -> new FriendListResponse(friend.getReceiver().getId(), friend.getReceiver().getName()))
                .collect(Collectors.toList());

        // 현재 멤버가 receiver인 경우
        List<Friend> receivedFriends = friendRepository.findByReceiverAndStatus(member, FriendStatus.ACCEPTED);
        List<FriendListResponse> friendsFromReceived = receivedFriends.stream()
                .map(friend -> new FriendListResponse(friend.getSender().getId(), friend.getSender().getName()))
                .collect(Collectors.toList());

        // 두 목록을 합치고 중복을 제거
        return Stream.concat(friendsFromSent.stream(), friendsFromReceived.stream())
                .distinct()
                .collect(Collectors.toList());
    }


    @Transactional
    public void unfriendSelected(List<Long> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            throw new IllegalArgumentException("친구 ID는 null이거나 비워 둘 수 없습니다.");
        }

        // 각 친구 관계 ID에 대해서 삭제를 수행합니다.
        for (Long friendId : friendIds) {
            Friend friend = friendRepository.findById(friendId)
                    .orElseThrow(() -> new EntityNotFoundException("친구를 찾을 수 없습니다."));

            friendRepository.delete(friend);
        }
    }

    @Transactional(readOnly = true)
    public List<FriendshipDetailResponse> getFriendships(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾을 수 없습니다."));

        List<Friend> friendships = friendRepository.findBySenderOrReceiver(memberId);
        return friendships.stream()
                .map(friendship -> FriendshipDetailResponse.builder()
                        .friendshipId(friendship.getId())
                        .senderId(friendship.getSender().getId())
                        .receiverId(friendship.getReceiver().getId())
                        .senderName(friendship.getSender().getName())  // 이름을 설정
                        .receiverName(friendship.getReceiver().getName())  // 이름을 설정
                        .status(friendship.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}