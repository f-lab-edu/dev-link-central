package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupFeedStatisticRepository extends JpaRepository<GroupFeedStatistic, Long> {

    Optional<GroupFeedStatistic> findByGroupFeed(GroupFeed groupFeed);

    void deleteByGroupFeedId(Long groupFeedId);
}
