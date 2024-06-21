package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.GroupFeed;
import dev.linkcentral.database.entity.GroupFeedLike;
import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedLikeRepository extends JpaRepository<GroupFeedLike, Long> {

    Optional<GroupFeedLike> findByGroupFeedAndMember(GroupFeed groupFeed, Member member);

    List<GroupFeedLike> findByGroupFeedId(Long groupFeedId);
}
