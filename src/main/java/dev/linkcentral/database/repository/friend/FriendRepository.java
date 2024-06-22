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

    @Query("SELECT count(*) > 0 FROM Friend f " +
            "WHERE (f.sender = :sender AND f.receiver = :receiver) " +
            "OR (f.receiver = :sender AND f.sender = :receiver)")
    boolean existsFriendshipBothWays(@Param("sender") Member sender, @Param("receiver") Member receiver);

    Optional<Friend> findBySenderAndReceiver(Member sender, Member receiver);

    List<Friend> findAllByReceiverAndStatus(Member receiver, FriendStatus status);

    @Query("select f from Friend f where f.id = :id AND f.status = :status")
    Optional<Friend> findByIdAndStatus(@Param("id") Long id, @Param("status") FriendStatus status);

    @Query("select f from Friend f where f.sender.id = :memberId or f.receiver.id = :memberId")
    List<Friend> findBySenderOrReceiver(@Param("memberId") Long memberId);

}
