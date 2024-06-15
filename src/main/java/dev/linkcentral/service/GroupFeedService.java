package dev.linkcentral.service;

import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.*;
import dev.linkcentral.infrastructure.s3.FileUploader;
import dev.linkcentral.service.dto.groupfeed.*;
import dev.linkcentral.service.mapper.GroupFeedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final GroupFeedRepository groupFeedRepository;
    private final GroupFeedLikeRepository groupFeedLikeRepository;
    private final GroupFeedStatisticRepository groupFeedStatisticRepository;
    private final GroupFeedCommentRepository groupFeedCommentRepository;
    private final MemberRepository memberRepository;
    private final ProfileService profileService;
    private final GroupFeedMapper groupFeedMapper;
    private final FileUploader fileUploader;

    @Transactional
    public GroupFeedSavedDTO createGroupFeed(GroupFeedCreateDTO groupFeedCreateDTO) {
        Member member = memberRepository.findById(groupFeedCreateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        Profile profile = profileService.findOrCreateProfile(member.getId());

        String imageUrl = null;
        MultipartFile imageFile = groupFeedCreateDTO.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileUploader.uploadFile(imageFile, "group-feeds/" + groupFeedCreateDTO.getMemberId());
        }

        GroupFeed groupFeed = GroupFeed.of(member, groupFeedCreateDTO, imageUrl);
        GroupFeed savedGroupFeed = groupFeedRepository.save(groupFeed);

        // GroupFeedStatistic 초기화
        GroupFeedStatistic groupFeedStatistic = GroupFeedStatistic.createStatistic(savedGroupFeed);
        groupFeedStatisticRepository.save(groupFeedStatistic);

        return groupFeedMapper.toGroupFeedMapper(savedGroupFeed);
    }

    @Transactional(readOnly = true)
    public Page<GroupFeedWithProfileDTO> getGroupFeeds(Pageable pageable) {
        Page<GroupFeed> groupFeeds = groupFeedRepository.findAll(pageable);
        return groupFeeds.map(this::mapToGroupFeedWithProfileDTO);
    }

    private GroupFeedWithProfileDTO mapToGroupFeedWithProfileDTO(GroupFeed groupFeed) {
        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        int likeCount = groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .map(GroupFeedStatistic::getLikes)
                .orElse(0); // 좋아요 수 가져오기
        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile, likeCount);
    }

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

    @Transactional(readOnly = true)
    public GroupFeedWithProfileDTO getFeedById(Long feedId) {
        GroupFeed groupFeed = groupFeedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다."));

        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        int likeCount = groupFeedStatisticRepository.findByGroupFeed(groupFeed)
                .map(GroupFeedStatistic::getLikes)
                .orElse(0); // 좋아요 수 가져오기

        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile, likeCount);
    }

    @Transactional
    public void deleteFeed(Long feedId) {
        // 피드와 연관된 좋아요 삭제
        List<GroupFeedLike> likes = groupFeedLikeRepository.findByGroupFeedId(feedId);
        groupFeedLikeRepository.deleteAll(likes);

        // 피드와 연관된 통계 삭제
        groupFeedStatisticRepository.deleteByGroupFeedId(feedId);

        // 피드와 연관된 댓글 삭제
        groupFeedCommentRepository.deleteByGroupFeedId(feedId);
        groupFeedRepository.deleteById(feedId);
    }

    @Transactional
    public GroupFeedSavedDTO updateGroupFeed(Long memberId, GroupFeedUpdateDTO groupFeedUpdateDTO) {
        GroupFeed groupFeed = groupFeedRepository.findByIdAndMemberId(groupFeedUpdateDTO.getFeedId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 피드를 찾을 수 없거나 수정할 권한이 없습니다."));

        groupFeed.updateTitle(groupFeedUpdateDTO.getTitle());
        groupFeed.updateContent(groupFeedUpdateDTO.getContent());

        MultipartFile imageFile = groupFeedUpdateDTO.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileUploader.uploadFile(imageFile, "group-feeds/" + memberId);
            groupFeed.updateImageUrl(imageUrl);
        }

        GroupFeed updatedGroupFeed = groupFeedRepository.save(groupFeed);
        return groupFeedMapper.toGroupFeedMapper(updatedGroupFeed);
    }

    @Transactional
    public void addComment(Long feedId, GroupFeedCommentDTO commentRequestDTO, Member member) {
        GroupFeed groupFeed = groupFeedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다."));

        GroupFeedComment newComment = GroupFeedComment.createComment(groupFeed, member, commentRequestDTO);
        groupFeedCommentRepository.save(newComment);
    }

    @Transactional(readOnly = true)
    public List<GroupFeedCommentDTO> getComments(Long feedId) {
        List<GroupFeedComment> comments = groupFeedCommentRepository.findByGroupFeedId(feedId);
        return comments.stream()
                .map(groupFeedMapper::toGroupFeedCommentDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Member member, GroupFeedCommentUpdateDTO commentUpdateDTO) {
        GroupFeedComment comment = groupFeedCommentRepository.findByIdAndGroupFeedIdAndMemberId(
                        commentUpdateDTO.getCommentId(), commentUpdateDTO.getFeedId(), member.getId())
                        .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 수정할 권한이 없습니다."));
        comment.updateContent(commentUpdateDTO.getContent());
        groupFeedCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long feedId, Long commentId, Member member) {
        GroupFeedComment comment = groupFeedCommentRepository
                .findByIdAndGroupFeedIdAndMemberId(commentId, feedId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 삭제할 권한이 없습니다."));
        groupFeedCommentRepository.delete(comment);
    }

    @Transactional
    public GroupFeedLikeDTO toggleLike(Long feedId, Long memberId) {
        GroupFeed groupFeed = groupFeedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("피드를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

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
        return groupFeedMapper.toGroupFeedLikeDTO(feedStatistic.getLikes(), liked);
    }
}
