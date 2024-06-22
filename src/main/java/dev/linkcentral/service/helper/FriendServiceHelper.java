package dev.linkcentral.service.helper;

import dev.linkcentral.database.entity.friend.Friend;
import dev.linkcentral.database.entity.friend.FriendStatus;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.friend.FriendRepository;
import dev.linkcentral.database.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendServiceHelper {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    /**
     * 회원 ID로 회원을 찾습니다.
     *
     * @param memberId 회원 ID
     * @return 회원 엔티티
     */
    public Member findMemberById(Long memberId) {
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
    public Member findMemberById(Long memberId, String errorMessage) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    /**
     * 친구 요청을 유효성 검사합니다.
     *
     * @param sender 발신자 회원 엔티티
     * @param receiver 수신자 회원 엔티티
     */
    public void validateFriendRequest(Member sender, Member receiver) {
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
    public Friend findFriendRequestById(Long requestId) {
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
    public Friend findFriendRequestByIdAndStatus(Long requestId, FriendStatus status) {
        return friendRepository.findByIdAndStatus(requestId, status)
                .orElseThrow(() -> new EntityNotFoundException("ID로 친구 요청을 찾을 수 없습니다: " + requestId));
    }

    /**
     * 친구 ID 목록의 유효성을 검사합니다.
     *
     * @param friendIds 친구 ID 목록
     */
    public void validateFriendIds(List<Long> friendIds) {
        if (friendIds == null || friendIds.isEmpty()) {
            throw new IllegalArgumentException("친구 ID는 null이거나 비워 둘 수 없습니다.");
        }
    }

    /**
     * 친구 ID로 친구 관계를 삭제합니다.
     *
     * @param friendId 친구 ID
     */
    public void deleteFriendById(Long friendId) {
        Friend friend = findFriendRequestById(friendId);
        friendRepository.delete(friend);
    }
}
