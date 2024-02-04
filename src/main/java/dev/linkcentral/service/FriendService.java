package dev.linkcentral.service;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.FriendRepository;
import dev.linkcentral.database.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

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
    public void createFriendRequest(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("보낸 사람을 찾을 수 없음"));

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("수신자를 찾을 수 없음"));

        Friend friend = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendStatus.REQUESTED)
                .build();

        friendRepository.save(friend);
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

    @Transactional
    public void updateFriendRequestStatus(Long requestId) {
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("친구 요청을 찾을 수 없습니다."));

        friendRequest.updateFriendStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendRequest);
    }

    @Transactional
    public void deleteFriendship(Long friendId) {
        Friend friendship = friendRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("친구 관계를 찾을 수 없습니다."));

        friendRepository.delete(friendship);
    }

}
