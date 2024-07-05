package dev.linkcentral.database.repository.groupfeed;

import dev.linkcentral.database.entity.groupfeed.GroupFeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupFeedCommentRepository extends JpaRepository<GroupFeedComment, Long> {

    /**
     * 특정 그룹 피드 ID에 속하는 모든 댓글을 찾는다.
     *
     * @param groupFeedId 그룹 피드 ID
     * @return GroupFeedComment 엔티티 목록
     */
    List<GroupFeedComment> findByGroupFeedId(Long groupFeedId);

    /**
     * 특정 ID, 그룹 피드 ID 및 멤버 ID로 댓글을 찾는다.
     *
     * @param id 댓글 ID
     * @param groupFeedId 그룹 피드 ID
     * @param memberId 멤버 ID
     * @return GroupFeedComment 엔티티의 Optional 객체
     */
    Optional<GroupFeedComment> findByIdAndGroupFeedIdAndMemberId(Long id, Long groupFeedId, Long memberId);

    /**
     * 특정 그룹 피드 ID에 속하는 모든 댓글을 삭제
     *
     * @param groupFeedId 그룹 피드 ID
     */
    void deleteByGroupFeedId(Long groupFeedId);
}
