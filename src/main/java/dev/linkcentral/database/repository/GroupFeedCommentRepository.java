package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.GroupFeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedCommentRepository extends JpaRepository<GroupFeedComment, Long> {

    List<GroupFeedComment> findByGroupFeedId(Long groupFeedId);

    Optional<GroupFeedComment> findByIdAndGroupFeedIdAndMemberId(Long id, Long groupFeedId, Long memberId);
}