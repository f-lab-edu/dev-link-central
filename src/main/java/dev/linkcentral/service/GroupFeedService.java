package dev.linkcentral.service;

import dev.linkcentral.database.entity.GroupFeed;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.Profile;
import dev.linkcentral.database.repository.GroupFeedRepository;
import dev.linkcentral.database.repository.MemberRepository;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupFeedService {

    private final GroupFeedRepository groupFeedRepository;
    private final MemberRepository memberRepository;
    private final ProfileService profileService;
    private final GroupFeedMapper groupFeedMapper;
    private final FileUploader fileUploader;

    @Transactional
    public GroupFeedSavedDTO createGroupFeed(GroupFeedCreateDTO groupFeedCreateDTO) {
        Member member = memberRepository.findById(groupFeedCreateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        String imageUrl = null;
        MultipartFile imageFile = groupFeedCreateDTO.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileUploader.uploadFile(imageFile, "group-feeds/" + groupFeedCreateDTO.getMemberId());
        }

        GroupFeed groupFeed = GroupFeed.of(member, groupFeedCreateDTO, imageUrl);
        GroupFeed savedGroupFeed = groupFeedRepository.save(groupFeed);
        return groupFeedMapper.toGroupFeedMapper(savedGroupFeed);
    }

    @Transactional(readOnly = true)
    public Page<GroupFeedWithProfileDTO> getGroupFeeds(Pageable pageable) {
        Page<GroupFeed> groupFeeds = groupFeedRepository.findAll(pageable);
        return groupFeeds.map(this::mapToGroupFeedWithProfileDTO);
    }

    private GroupFeedWithProfileDTO mapToGroupFeedWithProfileDTO(GroupFeed groupFeed) {
        Profile profile = profileService.getProfileByMemberId(groupFeed.getMember().getId());
        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile);
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
        return groupFeedMapper.toGroupFeedWithProfileDTO(groupFeed, profile);
    }

    @Transactional
    public void deleteFeed(Long feedId) {
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
}
