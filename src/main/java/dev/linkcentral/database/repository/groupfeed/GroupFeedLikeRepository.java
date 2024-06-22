package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedLike;
import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedLikeRepository extends JpaRepository<GroupFeedLike, Long> {

    Optional<GroupFeedLike> findByGroupFeedAndMember(GroupFeed groupFeed, Member member);

    List<GroupFeedLike> findByGroupFeedId(Long groupFeedId);
}
