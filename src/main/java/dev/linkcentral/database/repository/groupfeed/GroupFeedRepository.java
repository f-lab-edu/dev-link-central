package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedRepository extends JpaRepository<GroupFeed, Long> {

    /**
     * 특정 멤버 ID로 모든 GroupFeed를 찾는다.
     *
     * @param memberId 멤버 ID
     * @return GroupFeed 엔티티 목록
     */
    List<GroupFeed> findByMemberId(Long memberId);

    /**
     * 특정 피드 ID와 멤버 ID로 GroupFeed를 찾는다.
     *
     * @param feedId 피드 ID
     * @param memberId 멤버 ID
     * @return GroupFeed 엔티티의 Optional 객체
     */
    Optional<GroupFeed> findByIdAndMemberId(Long feedId, Long memberId);

    /**
     * 멤버 목록과 Pageable 객체로 페이징된 GroupFeed를 찾는다.
     *
     * @param members 멤버 엔티티 목록
     * @param pageable 페이징 정보
     * @return GroupFeed 엔티티의 페이지 객체
     */
    Page<GroupFeed> findAllByMemberIn(List<Member> members, Pageable pageable);
}
