package dev.linkcentral.service;

import dev.linkcentral.database.entity.friend.Friend;
import dev.linkcentral.database.entity.friend.FriendStatus;
import dev.member.entity.Member;
import dev.linkcentral.database.repository.friend.FriendRepository;
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

    private final FriendMapper friendMapper;
    private final FriendRepository friendRepository;

    /**
     * 친구 요청을 보낸다
     *
     * @param sender   친구 요청을 보내는 사용자
     * @param receiver 친구 요청을 받는 사용자
     * @return 생성된 친구 요청의 ID
     */
    @Transactional
    public Long sendFriendRequest(Member sender, Member receiver) {
        validateFriendRequest(sender, receiver);
        Friend friendEntity = friendMapper.createFriendRequest(sender, receiver);
        friendRepository.save(friendEntity);
        return friendEntity.getId();
    }

    /**
     * 받은 친구 요청을 조회
     *
     * @param receiver 친구 요청을 받은 사용자
     * @return 친구 요청 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<FriendRequestDTO> getReceivedFriendRequests(Member receiver) {
        List<Friend> friends = friendRepository.findAllByReceiverAndStatus(receiver, FriendStatus.REQUESTED);
        return friendMapper.toFriendRequestDTOList(friends);
    }

    /**
     * 친구 요청을 수락
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
     * 친구 요청을 거절
     *
     * @param requestId 친구 요청 ID
     */
    @Transactional
    public void rejectFriendRequest(Long requestId) {
        Friend friendRequest = findFriendRequestById(requestId);
        friendRepository.delete(friendRequest);
    }

    /**
     * 친구 관계 ID를 조회
     *
     * @param sender   친구 요청을 보낸 사용자
     * @param receiver 친구 요청을 받은 사용자
     * @return 친구 관계 ID
     */
    @Transactional
    public Long findFriendshipId(Member sender, Member receiver) {
        return friendRepository.findBySenderAndReceiver(sender, receiver)
                .map(Friend::getId)
                .orElseThrow(() -> new EntityNotFoundException("친구 관계 ID를 찾을 수 없습니다."));
    }

    /**
     * 친구 관계를 삭제
     *
     * @param friendId 친구 관계 ID
     */
    @Transactional
    public void deleteFriendship(Long friendId) {
        Friend friendship = findFriendRequestById(friendId);
        friendRepository.delete(friendship);
    }

    /**
     * 선택된 친구 관계를 삭제
     *
     * @param friendIds 삭제할 친구 관계 ID 목록
     */
    @Transactional
    public void unfriendSelected(List<Long> friendIds) {
        validateFriendIds(friendIds);
        friendIds.forEach(this::deleteFriendById);
    }

    /**
     * 사용자의 친구 관계 목록을 조회
     *
     * @param memberId 사용자 ID
     * @return 친구 관계 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<FriendshipDetailDTO> getFriendships(Long memberId) {
        List<Friend> friendships = friendRepository.findBySenderOrReceiver(memberId);
        return friendMapper.toFriendshipDetailDTOList(friendships);
    }

    /**
     * 친구 요청을 유효성 검사
     *
     * @param sender   친구 요청을 보내는 사용자
     * @param receiver 친구 요청을 받는 사용자
     */
    private void validateFriendRequest(Member sender, Member receiver) {
        boolean friendRequestExists = friendRepository.existsFriendshipBothWays(sender, receiver);
        if (friendRequestExists) {
            throw new IllegalStateException("친구 요청을 이미 보냈거나 받았습니다.");
        }
    }

    /**
     * 주어진 ID로 친구 요청을 찾는다.
     *
     * @param requestId 친구 요청 ID
     * @return 친구 요청 엔티티
     */
    private Friend findFriendRequestById(Long requestId) {
        return friendRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));
    }

    /**
     * 주어진 ID와 상태로 친구 요청을 찾는다.
     *
     * @param requestId 친구 요청 ID
     * @param status    친구 요청 상태
     * @return 친구 요청 엔티티
     */
    private Friend findFriendRequestByIdAndStatus(Long requestId, FriendStatus status) {
        return friendRepository.findByIdAndStatus(requestId, status)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));
    }

    /**
     * 친구 관계 ID 목록을 유효성 검사
     *
     * @param friendIds 친구 관계 ID 목록
     */
    private void validateFriendIds(List<Long> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            throw new IllegalArgumentException("친구 ID는 null이거나 비워 둘 수 없습니다.");
        }
    }

    /**
     * 주어진 ID로 친구 관계를 삭제
     *
     * @param friendId 친구 관계 ID
     */
    private void deleteFriendById(Long friendId) {
        Friend friend = findFriendRequestById(friendId);
        friendRepository.delete(friend);
    }
}
