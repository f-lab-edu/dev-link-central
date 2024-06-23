package dev.linkcentral.service;

import dev.linkcentral.database.entity.friend.Friend;
import dev.linkcentral.database.entity.friend.FriendStatus;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.friend.FriendRepository;
import dev.linkcentral.database.repository.member.MemberRepository;
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

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final MemberRepository memberRepository;

    /**
     * 친구 요청을 보냅니다.
     *
     * @param senderId 발신자 ID
     * @param receiverId 수신자 ID
     * @return 생성된 친구 요청의 ID
     */
    @Transactional
    public Long sendFriendRequest(Long senderId, Long receiverId) {
        Member sender = findMemberById(senderId);
        Member receiver = findMemberById(receiverId);
        validateFriendRequest(sender, receiver);

        Friend friendEntity = friendMapper.createFriendRequest(sender, receiver);
        friendRepository.save(friendEntity);
        return friendEntity.getId();
    }

    /**
     * 수신된 친구 요청 목록을 반환합니다.
     *
     * @param receiverId 수신자 ID
     * @return 친구 요청 목록
     */
    @Transactional(readOnly = true)
    public List<FriendRequestDTO> getReceivedFriendRequests(Long receiverId) {
        Member receiver = findMemberById(receiverId);
        List<Friend> friends = friendRepository.findAllByReceiverAndStatus(receiver, FriendStatus.REQUESTED);
        return friendMapper.toFriendRequestDTOList(friends);
    }

    /**
     * 친구 요청을 수락합니다.
     *
     * @param requestId 친구 요청 ID
     */
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        Friend friendRequest = findFriendRequestByIdAndStatus(requestId, FriendStatus.REQUESTED);
        friendRequest.updateFriendStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendRequest);
    }

    /**
     * 친구 요청을 거절합니다.
     *
     * @param requestId 친구 요청 ID
     */
    @Transactional
    public void rejectFriendRequest(Long requestId) {
        Friend friendRequest = findFriendRequestById(requestId);
        friendRepository.delete(friendRequest);
    }

    /**
     * 친구 관계 ID를 조회합니다.
     *
     * @param senderId 발신자 ID
     * @param receiverId 수신자 ID
     * @return 친구 관계 ID
     */
    @Transactional
    public Long findFriendshipId(Long senderId, Long receiverId) {
        Member sender = findMemberById(senderId, "요청자 ID를 찾을 수 없습니다.");
        Member receiver = findMemberById(receiverId, "대상의 ID를 찾을 수 없습니다.");
        return friendRepository.findBySenderAndReceiver(sender, receiver)
                .map(Friend::getId)
                .orElseThrow(() -> new EntityNotFoundException("친구 관계 ID를 찾을 수 없습니다."));
    }

    /**
     * 친구 관계를 삭제합니다.
     *
     * @param friendId 친구 관계 ID
     */
    @Transactional
    public void deleteFriendship(Long friendId) {
        Friend friendship = findFriendRequestById(friendId);
        friendRepository.delete(friendship);
    }

    /**
     * 선택된 친구 관계들을 삭제합니다.
     *
     * @param friendIds 친구 관계 ID 목록
     */
    @Transactional
    public void unfriendSelected(List<Long> friendIds) {
        validateFriendIds(friendIds);
        friendIds.forEach(this::deleteFriendById);
    }

    /**
     * 회원의 친구 관계 목록을 반환합니다.
     *
     * @param memberId 회원 ID
     * @return 친구 관계 목록
     */
    @Transactional(readOnly = true)
    public List<FriendshipDetailDTO> getFriendships(Long memberId) {
        List<Friend> friendships = friendRepository.findBySenderOrReceiver(memberId);
        return friendMapper.toFriendshipDetailDTOList(friendships);
    }

    /**
     * 회원 ID로 회원을 찾습니다.
     *
     * @param memberId 회원 ID
     * @return 회원 엔티티
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    /**
     * 회원 ID와 오류 메시지로 회원을 찾습니다.
     *
     * @param memberId 회원 ID
     * @param errorMessage 오류 메시지
     * @return 회원 엔티티
     */
    private Member findMemberById(Long memberId, String errorMessage) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    /**
     * 친구 요청을 유효성 검사합니다.
     *
     * @param sender 발신자 회원 엔티티
     * @param receiver 수신자 회원 엔티티
     */
    private void validateFriendRequest(Member sender, Member receiver) {
        boolean friendRequestExists = friendRepository.existsFriendshipBothWays(sender, receiver);
        if (friendRequestExists) {
            throw new IllegalStateException("친구 요청을 이미 보냈거나 받았습니다.");
        }
    }

    /**
     * 친구 요청 ID로 친구 요청을 찾습니다.
     *
     * @param requestId 친구 요청 ID
     * @return 친구 엔티티
     */
    private Friend findFriendRequestById(Long requestId) {
        return friendRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));
    }

    /**
     * 친구 요청 ID와 상태로 친구 요청을 찾습니다.
     *
     * @param requestId 친구 요청 ID
     * @param status 친구 요청 상태
     * @return 친구 엔티티
     */
    private Friend findFriendRequestByIdAndStatus(Long requestId, FriendStatus status) {
        return friendRepository.findByIdAndStatus(requestId, status)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));
    }

    /**
     * 친구 ID 목록의 유효성을 검사합니다.
     *
     * @param friendIds 친구 ID 목록
     */
    private void validateFriendIds(List<Long> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            throw new IllegalArgumentException("친구 ID는 null이거나 비워 둘 수 없습니다.");
        }
    }

    /**
     * 친구 ID로 친구 관계를 삭제합니다.
     *
     * @param friendId 친구 ID
     */
    private void deleteFriendById(Long friendId) {
        Friend friend = findFriendRequestById(friendId);
        friendRepository.delete(friend);
    }
}
