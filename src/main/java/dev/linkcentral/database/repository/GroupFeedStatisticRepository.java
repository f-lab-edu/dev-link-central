package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.GroupFeed;
import dev.linkcentral.database.entity.GroupFeedStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupFeedStatisticRepository extends JpaRepository<GroupFeedStatistic, Long> {

    Optional<GroupFeedStatistic> findByGroupFeed(GroupFeed groupFeed);

    void deleteByGroupFeedId(Long groupFeedId);
}
