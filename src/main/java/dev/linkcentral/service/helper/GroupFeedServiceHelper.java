package dev.linkcentral.service.helper;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedComment;
import dev.linkcentral.database.entity.groupfeed.GroupFeedLike;
import dev.linkcentral.database.entity.groupfeed.GroupFeedStatistic;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.repository.groupfeed.GroupFeedCommentRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedLikeRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedStatisticRepository;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.infrastructure.s3.FileUploader;
import dev.linkcentral.service.dto.groupfeed.GroupFeedLikeDTO;
import dev.linkcentral.service.dto.groupfeed.GroupFeedUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GroupFeedServiceHelper {

    private final MemberRepository memberRepository;
    private final GroupFeedRepository groupFeedRepository;
    private final GroupFeedLikeRepository groupFeedLikeRepository;
    private final GroupFeedStatisticRepository groupFeedStatisticRepository;
    private final GroupFeedCommentRepository groupFeedCommentRepository;
    private final FileUploader fileUploader;

    /**
     * ID로 멤버를 찾습니다.
     *
     * @param memberId 멤버 ID
     * @return 멤버 객체
     */
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    /**
     * ID로 그룹 피드를 찾습니다.
     *
     * @param feedId 피드 ID
     * @return 그룹 피드 객체
     */
    public GroupFeed findGroupFeedById(Long feedId) {
        return groupFeedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다."));
    }

    /**
     * 멤버 ID와 피드 ID로 그룹 피드를 찾습니다.
     *
     * @param feedId 피드 ID
     * @param memberId 멤버 ID
     * @return 그룹 피드 객체
     */
    public GroupFeed findGroupFeedByIdAndMemberId(Long feedId, Long memberId) {
        return groupFeedRepository.findByIdAndMemberId(feedId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 피드를 찾을 수 없거나 수정할 권한이 없습니다."));
    }

    /**
     * 댓글 ID, 피드 ID, 멤버 ID로 댓글을 찾습니다.
     *
     * @param commentId 댓글 ID
     * @param feedId 피드 ID
     * @param memberId 멤버 ID
     * @return 그룹 피드 댓글 객체
     */
    public GroupFeedComment findCommentByIdAndGroupFeedIdAndMemberId(Long commentId, Long feedId, Long memberId) {
        return groupFeedCommentRepository.findByIdAndGroupFeedIdAndMemberId(commentId, feedId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 수정할 권한이 없습니다."));
    }

    /**
     * 이미지 파일을 업로드합니다.
     *
     * @param imageFile 이미지 파일
     * @param memberId 멤버 ID
     * @return 업로드된 파일의 URL
     */
    public String uploadImageFile(MultipartFile imageFile, Long memberId) {
        if (imageFile != null && !imageFile.isEmpty()) {
            return fileUploader.uploadFile(imageFile, "group-feeds/" + memberId);
        }
        return null;
    }

    /**
     * 특정 피드의 좋아요 수를 반환합니다.
     *
     * @param groupFeed 그룹 피드 객체
     * @return 좋아요 수
     */
    public int getLikeCount(GroupFeed groupFeed) {
        return groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .map(GroupFeedStatistic::getLikes)
                .orElse(0);
    }

    /**
     * 그룹 피드를 업데이트합니다.
     *
     * @param groupFeed 그룹 피드 객체
     * @param updateDTO 그룹 피드 업데이트 DTO
     * @param memberId 멤버 ID
     */
    public void updateGroupFeedDetails(GroupFeed groupFeed, GroupFeedUpdateDTO updateDTO, Long memberId) {
        groupFeed.updateTitle(updateDTO.getTitle());
        groupFeed.updateContent(updateDTO.getContent());

        MultipartFile imageFile = updateDTO.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadImageFile(imageFile, memberId);
            groupFeed.updateImageUrl(imageUrl);
        }
    }

    /**
     * 특정 피드와 관련된 모든 데이터를 삭제합니다.
     *
     * @param feedId 피드 ID
     */
    public void deleteAllAssociatedData(Long feedId) {
        List<GroupFeedLike> likes = groupFeedLikeRepository.findByGroupFeedId(feedId);
        groupFeedLikeRepository.deleteAll(likes);
        groupFeedStatisticRepository.deleteByGroupFeedId(feedId);
        groupFeedCommentRepository.deleteByGroupFeedId(feedId);
    }

    /**
     * 특정 피드에 대한 좋아요를 토글합니다.
     *
     * @param groupFeed 그룹 피드 객체
     * @param member 멤버 객체
     * @return 그룹 피드 좋아요 DTO
     */
    public GroupFeedLikeDTO processLikeToggle(GroupFeed groupFeed, Member member) {
        Optional<GroupFeedLike> existingLike = groupFeedLikeRepository.findByGroupFeedAndMember(groupFeed, member);
        GroupFeedStatistic feedStatistic = groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .orElseGet(() -> GroupFeedStatistic.createStatistic(groupFeed));

        boolean liked;
        if (existingLike.isPresent()) {
            groupFeedLikeRepository.delete(existingLike.get());
            feedStatistic.updateLikes(feedStatistic.getLikes() - 1);
            liked = false;
        } else {
            GroupFeedLike newLike = GroupFeedLike.createGroupFeedLike(groupFeed, member);
            groupFeedLikeRepository.save(newLike);
            feedStatistic.updateLikes(feedStatistic.getLikes() + 1);
            liked = true;
        }

        groupFeedStatisticRepository.save(feedStatistic);
        return new GroupFeedLikeDTO(feedStatistic.getLikes(), liked);
    }

    /**
     * 특정 피드의 통계 정보를 반환합니다.
     *
     * @param groupFeed 그룹 피드 객체
     * @return 그룹 피드 통계 객체
     */
    public GroupFeedStatistic getFeedStatistic(GroupFeed groupFeed) {
        return groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .orElseGet(() -> GroupFeedStatistic.createStatistic(groupFeed));
    }

    /**
     * 그룹 피드 통계 정보를 생성합니다.
     *
     * @param savedGroupFeed 저장된 그룹 피드 객체
     */
    public void createGroupFeedStatistic(GroupFeed savedGroupFeed) {
        GroupFeedStatistic groupFeedStatistic = GroupFeedStatistic.createStatistic(savedGroupFeed);
        groupFeedStatisticRepository.save(groupFeedStatistic);
    }
}
