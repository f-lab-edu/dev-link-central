package dev.linkcentral.service;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedComment;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.database.repository.groupfeed.GroupFeedCommentRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedStatisticRepository;
import dev.linkcentral.service.dto.groupfeed.*;
import dev.linkcentral.service.helper.GroupFeedServiceHelper;
import dev.linkcentral.service.mapper.GroupFeedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupFeedService {

    private final GroupFeedRepository groupFeedRepository;
    private final GroupFeedStatisticRepository groupFeedStatisticRepository;
    private final GroupFeedCommentRepository groupFeedCommentRepository;
    private final ProfileService profileService;
    private final GroupFeedMapper groupFeedMapper;
    private final GroupFeedServiceHelper groupFeedServiceHelper;

    /**
     * 그룹 피드를 생성합니다.
     *
     * @param groupFeedCreateDTO 그룹 피드 생성 DTO
     * @return 생성된 그룹 피드 저장 DTO
     */
    @Transactional
    public GroupFeedSavedDTO createGroupFeed(GroupFeedCreateDTO groupFeedCreateDTO) {
        Member member = groupFeedServiceHelper.findMemberById(groupFeedCreateDTO.getMemberId());
        Profile profile = profileService.findOrCreateProfile(member.getId());
        String imageUrl = groupFeedServiceHelper.uploadImageFile(groupFeedCreateDTO.getImageFile(), member.getId());

        GroupFeed groupFeed = GroupFeed.of(member, groupFeedCreateDTO, imageUrl);
        GroupFeed savedGroupFeed = groupFeedRepository.save(groupFeed);

        groupFeedServiceHelper.createGroupFeedStatistic(savedGroupFeed);

        return groupFeedMapper.toGroupFeedMapper(savedGroupFeed);
    }

    /**
     * 그룹 피드 목록을 페이징 처리하여 반환합니다.
     *
     * @param offset 페이지 시작점
     * @param limit 페이지 크기
     * @return 그룹 피드 목록 DTO
     */
    @Transactional(readOnly = true)
    public Page<GroupFeedWithProfileDTO> getGroupFeeds(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("id").descending());
        Page<GroupFeed> groupFeeds = groupFeedRepository.findAll(pageable);
        return groupFeeds.map(this::mapToGroupFeedWithProfileDTO);
    }

    private GroupFeedWithProfileDTO mapToGroupFeedWithProfileDTO(GroupFeed groupFeed) {
        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        int likeCount = groupFeedServiceHelper.getLikeCount(groupFeed);
        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile, likeCount);
    }

    /**
     * 특정 멤버의 모든 피드를 반환합니다.
     *
     * @param memberId 멤버 ID
     * @return 그룹 피드 세부 정보 목록
     */
    @Transactional(readOnly = true)
    public List<MyGroupFeedDetailsDTO> getAllFeedsByMemberId(Long memberId) {
        List<GroupFeed> groupFeeds = groupFeedRepository.findByMemberId(memberId);
        return groupFeeds.stream()
                .map(this::mapToMyFeedDTO)
                .collect(Collectors.toList());
    }

    private MyGroupFeedDetailsDTO mapToMyFeedDTO(GroupFeed groupFeed) {
        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        return groupFeedMapper.toMyFeedDTO(groupFeed, profile);
    }

    /**
     * 특정 피드의 세부 정보를 반환합니다.
     *
     * @param feedId 피드 ID
     * @return 그룹 피드와 프로필 DTO
     */
    @Transactional(readOnly = true)
    public GroupFeedWithProfileDTO getFeedById(Long feedId) {
        GroupFeed groupFeed = groupFeedServiceHelper.findGroupFeedById(feedId);

        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        int likeCount = groupFeedServiceHelper.getLikeCount(groupFeed);

        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile, likeCount);
    }

    /**
     * 특정 피드를 삭제합니다.
     *
     * @param feedId 피드 ID
     */
    @Transactional
    public void deleteFeed(Long feedId) {
        groupFeedServiceHelper.deleteAllAssociatedData(feedId);
        groupFeedRepository.deleteById(feedId);
    }

    /**
     * 특정 피드를 업데이트합니다.
     *
     * @param memberId 멤버 ID
     * @param groupFeedUpdateDTO 그룹 피드 업데이트 DTO
     * @return 업데이트된 그룹 피드 저장 DTO
     */
    @Transactional
    public GroupFeedSavedDTO updateGroupFeed(Long memberId, GroupFeedUpdateDTO groupFeedUpdateDTO) {
        GroupFeed groupFeed = groupFeedServiceHelper.findGroupFeedByIdAndMemberId(groupFeedUpdateDTO.getFeedId(), memberId);
        groupFeedServiceHelper.updateGroupFeedDetails(groupFeed, groupFeedUpdateDTO, memberId);
        GroupFeed updatedGroupFeed = groupFeedRepository.save(groupFeed);
        return groupFeedMapper.toGroupFeedMapper(updatedGroupFeed);
    }

    /**
     * 특정 피드에 댓글을 추가합니다.
     *
     * @param feedId 피드 ID
     * @param commentRequestDTO 댓글 요청 DTO
     * @param member 멤버 객체
     */
    @Transactional
    public void addComment(Long feedId, GroupFeedCommentDTO commentRequestDTO, Member member) {
        GroupFeed groupFeed = groupFeedServiceHelper.findGroupFeedById(feedId);
        GroupFeedComment newComment = GroupFeedComment.createComment(groupFeed, member, commentRequestDTO);
        groupFeedCommentRepository.save(newComment);
    }

    /**
     * 특정 피드의 모든 댓글을 반환합니다.
     *
     * @param feedId 피드 ID
     * @return 그룹 피드 댓글 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<GroupFeedCommentDTO> getComments(Long feedId) {
        List<GroupFeedComment> comments = groupFeedCommentRepository.findByGroupFeedId(feedId);
        return comments.stream()
                .map(groupFeedMapper::toGroupFeedCommentDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 피드의 특정 댓글을 업데이트합니다.
     *
     * @param member 멤버 객체
     * @param commentUpdateDTO 댓글 업데이트 DTO
     */
    @Transactional
    public void updateComment(Member member, GroupFeedCommentUpdateDTO commentUpdateDTO) {
        GroupFeedComment comment = groupFeedServiceHelper.findCommentByIdAndGroupFeedIdAndMemberId(
                commentUpdateDTO.getCommentId(), commentUpdateDTO.getFeedId(), member.getId());
        comment.updateContent(commentUpdateDTO.getContent());
        groupFeedCommentRepository.save(comment);
    }

    /**
     * 특정 피드의 특정 댓글을 삭제합니다.
     *
     * @param feedId 피드 ID
     * @param commentId 댓글 ID
     * @param member 멤버 객체
     */
    @Transactional
    public void deleteComment(Long feedId, Long commentId, Member member) {
        GroupFeedComment comment = groupFeedServiceHelper.findCommentByIdAndGroupFeedIdAndMemberId(commentId, feedId, member.getId());
        groupFeedCommentRepository.delete(comment);
    }

    /**
     * 특정 피드에 좋아요를 토글합니다.
     *
     * @param feedId 피드 ID
     * @param memberId 멤버 ID
     * @return 그룹 피드 좋아요 DTO
     */
    @Transactional
    public GroupFeedLikeDTO toggleLike(Long feedId, Long memberId) {
        GroupFeed groupFeed = groupFeedServiceHelper.findGroupFeedById(feedId);
        Member member = groupFeedServiceHelper.findMemberById(memberId);

        GroupFeedLikeDTO groupFeedLikeDTO = groupFeedServiceHelper.processLikeToggle(groupFeed, member);
        groupFeedStatisticRepository.save(groupFeedServiceHelper.getFeedStatistic(groupFeed));
        return groupFeedLikeDTO;
    }
}
