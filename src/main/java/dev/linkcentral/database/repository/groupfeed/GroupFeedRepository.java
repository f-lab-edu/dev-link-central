package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedRepository extends JpaRepository<GroupFeed, Long> {

    List<GroupFeed> findByMemberId(Long memberId);

    Optional<GroupFeed> findByIdAndMemberId(Long feedId, Long memberId);
}
