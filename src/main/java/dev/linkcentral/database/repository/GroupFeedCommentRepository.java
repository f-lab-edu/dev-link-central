package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.GroupFeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupFeedCommentRepository extends JpaRepository<GroupFeedComment, Long> {

    List<GroupFeedComment> findByGroupFeedId(Long groupFeedId);
}