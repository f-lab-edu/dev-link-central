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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupFeedService {

    private final FileUploader fileUploader;
    private final ProfileService profileService;
    private final GroupFeedMapper groupFeedMapper;
    private final MemberRepository memberRepository;
    private final StudyGroupService studyGroupService;
    private final GroupFeedRepository groupFeedRepository;
    private final GroupFeedLikeRepository groupFeedLikeRepository;
    private final GroupFeedCommentRepository groupFeedCommentRepository;
    private final GroupFeedStatisticRepository groupFeedStatisticRepository;

    /**
     * 그룹 피드를 생성
     *
     * @param groupFeedCreateDTO 그룹 피드 생성 DTO
     * @return 저장된 그룹 피드 DTO
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
     * 회원의 모든 그룹 피드를 조회
     *
     * @param memberId 회원 ID
     * @return 그룹 피드 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<MyGroupFeedDetailsDTO> getAllFeedsByMemberId(Long memberId) {
        List<GroupFeed> groupFeeds = groupFeedRepository.findByMemberId(memberId);
        return groupFeeds.stream()
                .map(this::mapToMyFeedDTO)
                .collect(Collectors.toList());
    }

    /**
     * 그룹 피드를 DTO로 매핑
     *
     * @param groupFeed 그룹 피드 엔티티
     * @return 그룹 피드 상세 정보 DTO
     */
    private MyGroupFeedDetailsDTO mapToMyFeedDTO(GroupFeed groupFeed) {
        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        return groupFeedMapper.toMyFeedDTO(groupFeed, profile);
    }

    /**
     * 피드 ID로 그룹 피드 상세 정보를 조회
     *
     * @param feedId 피드 ID
     * @return 그룹 피드 상세 정보 DTO
     */
    @Transactional(readOnly = true)
    public GroupFeedWithProfileDTO getFeedById(Long feedId) {
        GroupFeed groupFeed = findGroupFeedById(feedId);
        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        int likeCount = getLikeCount(groupFeed);

        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile, likeCount);
    }

    /**
     * 그룹 피드를 삭제
     *
     * @param feedId 피드 ID
     */
    @Transactional
    public void deleteFeed(Long feedId) {
        deleteAllAssociatedData(feedId);
        groupFeedRepository.deleteById(feedId);
    }

    /**
     * 그룹 피드를 업데이트
     *
     * @param memberId           회원 ID
     * @param groupFeedUpdateDTO 그룹 피드 업데이트 DTO
     * @return 업데이트된 그룹 피드 DTO
     */
    @Transactional
    public GroupFeedSavedDTO updateGroupFeed(Long memberId, GroupFeedUpdateDTO groupFeedUpdateDTO) {
        GroupFeed groupFeed = findGroupFeedByIdAndMemberId(groupFeedUpdateDTO.getFeedId(), memberId);
        updateGroupFeedDetails(groupFeed, groupFeedUpdateDTO, memberId);
        GroupFeed updatedGroupFeed = groupFeedRepository.save(groupFeed);
        return groupFeedMapper.toGroupFeedMapper(updatedGroupFeed);
    }

    /**
     * 그룹 피드에 댓글을 추가
     *
     * @param feedId            피드 ID
     * @param commentRequestDTO 댓글 요청 DTO
     * @param member            회원 엔티티
     */
    @Transactional
    public void addComment(Long feedId, GroupFeedCommentDTO commentRequestDTO, Member member) {
        GroupFeed groupFeed = findGroupFeedById(feedId);
        GroupFeedComment newComment = GroupFeedComment.createComment(groupFeed, member, commentRequestDTO);
        groupFeedCommentRepository.save(newComment);
    }

    /**
     * 그룹 피드의 댓글 목록을 조회
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
     * 그룹 피드의 댓글을 업데이트
     *
     * @param member             회원 엔티티
     * @param commentUpdateDTO   댓글 업데이트 DTO
     */
    @Transactional
    public void updateComment(Member member, GroupFeedCommentUpdateDTO commentUpdateDTO) {
        GroupFeedComment comment = findCommentByIdAndGroupFeedIdAndMemberId(
                commentUpdateDTO.getCommentId(), commentUpdateDTO.getFeedId(), member.getId());
        comment.updateContent(commentUpdateDTO.getContent());
        groupFeedCommentRepository.save(comment);
    }

    /**
     * 그룹 피드의 댓글을 삭제
     *
     * @param feedId    피드 ID
     * @param commentId 댓글 ID
     * @param member    회원 엔티티
     */
    @Transactional
    public void deleteComment(Long feedId, Long commentId, Member member) {
        GroupFeedComment comment = findCommentByIdAndGroupFeedIdAndMemberId(commentId, feedId, member.getId());
        groupFeedCommentRepository.delete(comment);
    }

    /**
     * 그룹 피드 좋아요를 토글
     *
     * @param feedId  피드 ID
     * @param memberId 회원 ID
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

    /**
     * 사용자의 그룹 피드 목록을 조회
     *
     * @param userId 사용자 ID
     * @param offset 페이지 오프셋
     * @param limit  페이지 제한
     * @return 그룹 피드 페이지 DTO
     */
    @Transactional(readOnly = true)
    public GroupFeedPageDTO getGroupFeedsForUser(Long userId, int offset, int limit) {
        Member member = findMemberById(userId);
        List<Member> groupMembers = studyGroupService.findMembersByUserId(userId);
        groupMembers.add(member);

        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by("id").descending());
        Page<GroupFeed> groupFeeds = groupFeedRepository.findAllByMemberIn(groupMembers, pageable);

        List<GroupFeedDTO> feedDTOs = groupFeeds.stream()
                .map(feed -> {
                    GroupFeedDTO groupFeedDTO = groupFeedMapper.toGroupFeedDTO(feed);
                    String profileImageUrl = profileService.getProfile(feed.getMember().getId()).getImageUrl();
                    groupFeedDTO.setProfileImageUrl(profileImageUrl);
                    return groupFeedDTO;
                })
                .collect(Collectors.toList());

        return groupFeedMapper.toGroupFeedPageDTO(feedDTOs, offset, limit, groupFeeds.getTotalElements());
    }

    /**
     * 주어진 ID로 회원을 조회
     *
     * @param memberId 회원 ID
     * @return 조회된 회원 엔티티
     * @throws IllegalArgumentException 주어진 ID로 회원을 찾을 수 없는 경우
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("ID로 멤버를 찾을 수 없습니다: " + memberId));
    }

    /**
     * 주어진 ID로 그룹 피드를 조회
     *
     * @param feedId 피드 ID
     * @return 조회된 그룹 피드 엔티티
     * @throws IllegalArgumentException 주어진 ID로 그룹 피드를 찾을 수 없는 경우
     */
    private GroupFeed findGroupFeedById(Long feedId) {
        return groupFeedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다."));
    }

    /**
     * 주어진 피드 ID와 회원 ID로 그룹 피드를 조회
     *
     * @param feedId  피드 ID
     * @param memberId 회원 ID
     * @return 조회된 그룹 피드 엔티티
     * @throws IllegalArgumentException 주어진 피드 ID와 회원 ID로 그룹 피드를 찾을 수 없거나 권한이 없는 경우
     */
    private GroupFeed findGroupFeedByIdAndMemberId(Long feedId, Long memberId) {
        return groupFeedRepository.findByIdAndMemberId(feedId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 피드를 찾을 수 없거나 수정할 권한이 없습니다."));
    }

    /**
     * 주어진 댓글 ID, 피드 ID, 회원 ID로 그룹 피드 댓글을 조회
     *
     * @param commentId 댓글 ID
     * @param feedId    피드 ID
     * @param memberId  회원 ID
     * @return 조회된 그룹 피드 댓글 엔티티
     * @throws IllegalArgumentException 주어진 조건으로 그룹 피드 댓글을 찾을 수 없는 경우
     */
    private GroupFeedComment findCommentByIdAndGroupFeedIdAndMemberId(Long commentId, Long feedId, Long memberId) {
        return groupFeedCommentRepository.findByIdAndGroupFeedIdAndMemberId(commentId, feedId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 수정할 권한이 없습니다."));
    }

    /**
     * 이미지 파일을 업로드하고 URL을 반환
     *
     * @param imageFile 이미지 파일
     * @param memberId  회원 ID
     * @return 업로드된 이미지의 URL
     */
    private String uploadImageFile(MultipartFile imageFile, Long memberId) {
        if (imageFile != null && !imageFile.isEmpty()) {
            return fileUploader.uploadFile(imageFile, "group-feeds/" + memberId);
        }
        return null;
    }

    /**
     * 그룹 피드의 좋아요 수를 조회
     *
     * @param groupFeed 그룹 피드 엔티티
     * @return 그룹 피드의 좋아요 수
     */
    private int getLikeCount(GroupFeed groupFeed) {
        return groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .map(GroupFeedStatistic::getLikes)
                .orElse(0);
    }

    /**
     * 그룹 피드를 업데이트
     *
     * @param groupFeed 그룹 피드 엔티티
     * @param updateDTO 그룹 피드 업데이트 DTO
     * @param memberId  회원 ID
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
     * 피드에 연관된 모든 데이터를 삭제
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
     * 그룹 피드 좋아요를 토글하고, 그룹 피드 통계를 업데이트
     *
     * @param groupFeed 그룹 피드 엔티티
     * @param member    회원 엔티티
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
     * 그룹 피드 통계를 조회
     *
     * @param groupFeed 그룹 피드 엔티티
     * @return 그룹 피드 통계 엔티티
     */
    private GroupFeedStatistic getFeedStatistic(GroupFeed groupFeed) {
        return groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .orElseGet(() -> GroupFeedStatistic.createStatistic(groupFeed));
    }

    /**
     * 그룹 피드 통계를 생성
     *
     * @param savedGroupFeed 저장된 그룹 피드 엔티티
     */
    private void createGroupFeedStatistic(GroupFeed savedGroupFeed) {
        GroupFeedStatistic groupFeedStatistic = GroupFeedStatistic.createStatistic(savedGroupFeed);
        groupFeedStatisticRepository.save(groupFeedStatistic);
    }
}
