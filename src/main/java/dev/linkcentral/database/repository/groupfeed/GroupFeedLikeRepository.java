package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedLike;
import dev.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedLikeRepository extends JpaRepository<GroupFeedLike, Long> {

    /**
     * 특정 그룹 피드와 멤버로 GroupFeedLike를 찾는다.
     *
     * @param groupFeed 그룹 피드 엔티티
     * @param member 멤버 엔티티
     * @return GroupFeedLike 엔티티의 Optional 객체
     */
    Optional<GroupFeedLike> findByGroupFeedAndMember(GroupFeed groupFeed, Member member);

    /**
     * 특정 그룹 피드 ID로 모든 GroupFeedLike를 찾는다.
     *
     * @param groupFeedId 그룹 피드 ID
     * @return GroupFeedLike 엔티티 목록
     */
    List<GroupFeedLike> findByGroupFeedId(Long groupFeedId);
}
