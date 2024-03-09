package dev.linkcentral.service;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.FriendRepository;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.presentation.dto.request.FriendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
                .map(friend -> new FriendRequest(friend.getSender().getId(), friend.getReceiver().getId()))
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 수락
     */
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        Friend friendRequest = friendRepository.findById(requestId)
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

    @Transactional
    public boolean checkFriendship(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId).orElse(null);
        Member receiver = memberRepository.findById(receiverId).orElse(null);
        if (sender == null || receiver == null) {
            return false;
        }
        return friendRepository.existsBySenderAndReceiverOrReceiverAndSender(sender, receiver, sender, receiver);
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

}