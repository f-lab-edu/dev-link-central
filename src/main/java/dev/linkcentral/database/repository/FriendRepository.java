package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Friend;
import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    /**
     * 친구 관계는 대칭적
     * MemberA -> MemberB
     * MemberB -> MemberA
     */
    boolean existsBySenderAndReceiverOrReceiverAndSender(Member sender1, Member receiver1, Member sender2, Member receiver2);

    Optional<Friend> findBySenderAndReceiver(Member sender, Member receiver);

}

