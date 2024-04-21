package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.FriendStatus;
import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsBySenderAndReceiverOrReceiverAndSender(Member sender, Member receiver,
                                                         Member sender2, Member receiver2);

    Optional<Friend> findBySenderAndReceiver(Member sender, Member receiver);

    List<Friend> findAllByReceiverAndStatus(Member receiver, FriendStatus status);

    @Query("select f from Friend f where f.id = :id AND f.status = :status")
    Optional<Friend> findByIdAndStatus(@Param("id") Long id, @Param("status") FriendStatus status);

    List<Friend> findBySenderAndStatus(Member sender, FriendStatus status);

    List<Friend> findByReceiverAndStatus(Member receiver, FriendStatus status);

    // memberId가 sender 또는 receiver인 친구 관계를 모두 가져오는 쿼리
    @Query("select f from Friend f where f.sender.id = :memberId or f.receiver.id = :memberId")
    List<Friend> findBySenderOrReceiver(@Param("memberId") Long memberId);
}