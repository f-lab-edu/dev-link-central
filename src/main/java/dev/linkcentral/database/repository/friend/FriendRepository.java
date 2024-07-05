package dev.linkcentral.database.repository.friend;

import dev.linkcentral.database.entity.friend.Friend;
import dev.linkcentral.database.entity.friend.FriendStatus;
import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    /**
     * 두 회원 간에 상호 친구 관계가 존재하는지 확인
     *
     * @param sender 확인할 첫 번째 회원
     * @param receiver 확인할 두 번째 회원
     * @return 친구 관계 존재 여부 (true: 존재함, false: 존재하지 않음)
     */
    @Query("SELECT count(*) > 0 FROM Friend f " +
            "WHERE (f.sender = :sender AND f.receiver = :receiver) " +
            "OR (f.receiver = :sender AND f.sender = :receiver)")
    boolean existsFriendshipBothWays(@Param("sender") Member sender, @Param("receiver") Member receiver);

    /**
     * 송신자와 수신자에 대한 친구 관계를 찾는다
     *
     * @param sender 친구 관계의 송신자
     * @param receiver 친구 관계의 수신자
     * @return Friend 엔티티의 Optional 객체
     */
    Optional<Friend> findBySenderAndReceiver(Member sender, Member receiver);

    /**
     * 특정 수신자와 상태를 기준으로 모든 친구 관계를 찾는다.
     *
     * @param receiver 친구 관계의 수신자
     * @param status 친구 관계의 상태
     * @return Friend 엔티티 목록
     */
    List<Friend> findAllByReceiverAndStatus(Member receiver, FriendStatus status);

    /**
     * 특정 ID와 상태를 기준으로 친구 관계를 찾는다.
     *
     * @param id 친구 관계 ID
     * @param status 친구 관계 상태
     * @return Friend 엔티티의 Optional 객체
     */
    @Query("select f from Friend f where f.id = :id AND f.status = :status")
    Optional<Friend> findByIdAndStatus(@Param("id") Long id, @Param("status") FriendStatus status);

    /**
     * 특정 회원이 송신자 또는 수신자인 모든 친구 관계를 찾는다.
     *
     * @param memberId 회원 ID
     * @return Friend 엔티티 목록
     */
    @Query("select f from Friend f where f.sender.id = :memberId or f.receiver.id = :memberId")
    List<Friend> findBySenderOrReceiver(@Param("memberId") Long memberId);
}
