package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedRepository extends JpaRepository<GroupFeed, Long> {

    List<GroupFeed> findByMemberId(Long memberId);

    Optional<GroupFeed> findByIdAndMemberId(Long feedId, Long memberId);

    Page<GroupFeed> findAllByMemberIn(List<Member> members, Pageable pageable);
}
