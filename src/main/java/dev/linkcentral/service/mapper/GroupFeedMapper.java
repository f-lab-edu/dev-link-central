package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.groupfeed.GroupFeed;
import dev.linkcentral.database.entity.groupfeed.GroupFeedComment;
import dev.member.entity.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.service.dto.groupfeed.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupFeedMapper {

    public GroupFeedSavedDTO toGroupFeedMapper(GroupFeed savedGroupFeed) {
        return GroupFeedSavedDTO.builder()
                .id(savedGroupFeed.getId())
                .title(savedGroupFeed.getTitle())
                .content(savedGroupFeed.getContent())
                .writer(savedGroupFeed.getWriter())
                .imageUrl(savedGroupFeed.getImageUrl())
                .build();
    }

    public MemberCurrentDTO toMemberCurrentDTO(Member currentMember) {
        return MemberCurrentDTO.builder()
                .memberId(currentMember.getId())
                .name(currentMember.getName())
                .nickname(currentMember.getNickname())
                .build();
    }

    public GroupFeedWithProfileDTO toGroupFeedWithProfileDTO(GroupFeed groupFeed, Profile profile, int likeCount) {
        return GroupFeedWithProfileDTO.builder()
                .id(groupFeed.getId())
                .title(groupFeed.getTitle())
                .content(groupFeed.getContent())
                .writer(groupFeed.getWriter())
                .imageUrl(groupFeed.getImageUrl())
                .createdAt(groupFeed.getCreatedAt())
                .profileImageUrl(profile != null ? profile.getImageUrl() : null)
                .likeCount(likeCount)
                .build();
    }

    public MyGroupFeedDetailsDTO toMyFeedDTO(GroupFeed groupFeed, Profile profile) {
        return MyGroupFeedDetailsDTO.builder()
                .id(groupFeed.getId())
                .title(groupFeed.getTitle())
                .content(groupFeed.getContent())
                .writer(groupFeed.getWriter())
                .imageUrl(groupFeed.getImageUrl())
                .createdAt(groupFeed.getCreatedAt())
                .profileImageUrl(profile != null ? profile.getImageUrl() : null)
                .build();
    }

    public GroupFeedCommentDTO toGroupFeedCommentDTO(GroupFeedComment comment) {
        return new GroupFeedCommentDTO(
                comment.getId(),
                comment.getWriterNickname(),
                comment.getContent(),
                comment.getCreatedAt().toString()
        );
    }

    public GroupFeedDTO toGroupFeedDTO(GroupFeed groupFeed) {
        return GroupFeedDTO.builder()
                .id(groupFeed.getId())
                .memberId(groupFeed.getMember().getId())
                .writer(groupFeed.getWriter())
                .title(groupFeed.getTitle())
                .content(groupFeed.getContent())
                .imageUrl(groupFeed.getImageUrl())
                .createdAt(groupFeed.getCreatedAt())
                .build();
    }

    public GroupFeedPageDTO toGroupFeedPageDTO(List<GroupFeedDTO> feedDTOs, int offset, int limit, long total) {
        return GroupFeedPageDTO.builder()
                .feeds(feedDTOs)
                .offset(offset)
                .limit(limit)
                .total(total)
                .build();
    }
}
