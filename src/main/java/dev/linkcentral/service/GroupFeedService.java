package dev.linkcentral.service;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedComment;
import dev.linkcentral.database.entity.groupfeed.GroupFeedLike;
import dev.linkcentral.database.entity.groupfeed.GroupFeedStatistic;
import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.database.repository.groupfeed.GroupFeedCommentRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedLikeRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedRepository;
import dev.linkcentral.database.repository.groupfeed.GroupFeedStatisticRepository;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.infrastructure.s3.FileUploader;
import dev.linkcentral.service.dto.groupfeed.*;
import dev.linkcentral.service.mapper.GroupFeedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupFeedService {

    private final GroupFeedRepository groupFeedRepository;
    private final GroupFeedStatisticRepository groupFeedStatisticRepository;
    private final GroupFeedCommentRepository groupFeedCommentRepository;
    private final ProfileService profileService;
    private final StudyGroupService studyGroupService;
    private final GroupFeedMapper groupFeedMapper;
    private final FileUploader fileUploader;
    private final MemberRepository memberRepository;
    private final GroupFeedLikeRepository groupFeedLikeRepository;

    /**
     * 그룹 피드를 생성합니다.
     *
     * @param groupFeedCreateDTO 그룹 피드 생성 DTO
     * @return 생성된 그룹 피드 저장 DTO
     */
    @Transactional
    public GroupFeedSavedDTO createGroupFeed(GroupFeedCreateDTO groupFeedCreateDTO) {
        Member member = findMemberById(groupFeedCreateDTO.getMemberId());
        Profile profile = profileService.findOrCreateProfile(member.getId());
        String imageUrl = uploadImageFile(groupFeedCreateDTO.getImageFile(), member.getId());

        GroupFeed groupFeed = GroupFeed.of(member, groupFeedCreateDTO, imageUrl);
        GroupFeed savedGroupFeed = groupFeedRepository.save(groupFeed);

        createGroupFeedStatistic(savedGroupFeed);

        return groupFeedMapper.toGroupFeedMapper(savedGroupFeed);
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
        GroupFeed groupFeed = findGroupFeedById(feedId);

        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        int likeCount = getLikeCount(groupFeed);

        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile, likeCount);
    }

    /**
     * 특정 피드를 삭제합니다.
     *
     * @param feedId 피드 ID
     */
    @Transactional
    public void deleteFeed(Long feedId) {
        deleteAllAssociatedData(feedId);
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
        GroupFeed groupFeed = findGroupFeedByIdAndMemberId(groupFeedUpdateDTO.getFeedId(), memberId);
        updateGroupFeedDetails(groupFeed, groupFeedUpdateDTO, memberId);
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
        GroupFeed groupFeed = findGroupFeedById(feedId);
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
        GroupFeedComment comment = findCommentByIdAndGroupFeedIdAndMemberId(
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
        GroupFeedComment comment = findCommentByIdAndGroupFeedIdAndMemberId(commentId, feedId, member.getId());
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
        GroupFeed groupFeed = findGroupFeedById(feedId);
        Member member = findMemberById(memberId);

        GroupFeedLikeDTO groupFeedLikeDTO = processLikeToggle(groupFeed, member);
        groupFeedStatisticRepository.save(getFeedStatistic(groupFeed));
        return groupFeedLikeDTO;
    }

    @Transactional(readOnly = true)
    public GroupFeedPageDTO getGroupFeedsForUser(Long userId, int offset, int limit) {
        List<Member> userMembers = studyGroupService.findMembersByUserId(userId);
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("id").descending());
        Page<GroupFeed> groupFeeds = groupFeedRepository.findAllByMemberInOrMemberId(userMembers, userId, pageable);
        List<GroupFeedDTO> feedDTOs = groupFeeds.stream()
                .map(groupFeedMapper::toGroupFeedDTO)
                .collect(Collectors.toList());
        return groupFeedMapper.toGroupFeedPageDTO(feedDTOs, offset, limit, groupFeeds.getTotalElements());
    }

    /**
     * ID로 멤버를 찾습니다.
     *
     * @param memberId 멤버 ID
     * @return 멤버 객체
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    /**
     * ID로 그룹 피드를 찾습니다.
     *
     * @param feedId 피드 ID
     * @return 그룹 피드 객체
     */
    private GroupFeed findGroupFeedById(Long feedId) {
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
    private GroupFeed findGroupFeedByIdAndMemberId(Long feedId, Long memberId) {
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
    private GroupFeedComment findCommentByIdAndGroupFeedIdAndMemberId(Long commentId, Long feedId, Long memberId) {
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
    private String uploadImageFile(MultipartFile imageFile, Long memberId) {
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
    private int getLikeCount(GroupFeed groupFeed) {
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
    private void updateGroupFeedDetails(GroupFeed groupFeed, GroupFeedUpdateDTO updateDTO, Long memberId) {
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
    private void deleteAllAssociatedData(Long feedId) {
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
    private GroupFeedLikeDTO processLikeToggle(GroupFeed groupFeed, Member member) {
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
    private GroupFeedStatistic getFeedStatistic(GroupFeed groupFeed) {
        return groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .orElseGet(() -> GroupFeedStatistic.createStatistic(groupFeed));
    }

    /**
     * 그룹 피드 통계 정보를 생성합니다.
     *
     * @param savedGroupFeed 저장된 그룹 피드 객체
     */
    private void createGroupFeedStatistic(GroupFeed savedGroupFeed) {
        GroupFeedStatistic groupFeedStatistic = GroupFeedStatistic.createStatistic(savedGroupFeed);
        groupFeedStatisticRepository.save(groupFeedStatistic);
    }
}
