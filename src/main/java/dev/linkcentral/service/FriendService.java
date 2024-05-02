package dev.linkcentral.service;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.FriendRepository;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import dev.linkcentral.service.dto.friend.FriendshipDetailDTO;
import dev.linkcentral.service.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
        boolean friendRequestExists = friendRepository.existsFriendshipBothWays(sender, receiver);
        if (friendRequestExists) {
            throw new IllegalStateException("친구 요청을 이미 보냈거나 받았습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<FriendRequestDTO> getReceivedFriendRequests(Long receiverId) {
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다: " + receiverId));

        List<Friend> friends = friendRepository.findAllByReceiverAndStatus(receiver, FriendStatus.REQUESTED);
        return friendMapper.toFriendRequestDTOList(friends);
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
        Member sender = findMemberById(senderId, "요청자 ID를 찾을 수 없습니다.");
        Member receiver = findMemberById(receiverId, "대상의 ID를 찾을 수 없습니다.");

        return friendRepository.findBySenderAndReceiver(sender, receiver)
                .map(Friend::getId)
                .orElseThrow(() -> new EntityNotFoundException("친구 관계 ID를 찾을 수 없습니다."));
    }

    private Member findMemberById(Long memberId, String errorMessage) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    @Transactional
    public void deleteFriendship(Long friendId) {
        Friend friendship = friendRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("친구 관계를 찾을 수 없습니다."));

        friendRepository.delete(friendship);
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
    public List<FriendshipDetailDTO> getFriendships(Long memberId) {
        List<Friend> friendships = friendRepository.findBySenderOrReceiver(memberId);
        return friendMapper.toFriendshipDetailDTOList(friendships);
    }

}
