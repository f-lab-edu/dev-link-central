package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupFeedStatisticRepository extends JpaRepository<GroupFeedStatistic, Long> {

    /**
     * 특정 그룹 피드로 GroupFeedStatistic을 찾는다.
     *
     * @param groupFeed 그룹 피드 엔티티
     * @return GroupFeedStatistic 엔티티의 Optional 객체
     */
    Optional<GroupFeedStatistic> findByGroupFeed(GroupFeed groupFeed);

    /**
     * 특정 그룹 피드 ID로 GroupFeedStatistic을 삭제
     *
     * @param groupFeedId 그룹 피드 ID
     */
    void deleteByGroupFeedId(Long groupFeedId);
}
