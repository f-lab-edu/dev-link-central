package dev.linkcentral.service.mapper;

import dev.linkcentral.database.entity.GroupFeed;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.dto.groupfeed.GroupFeedSavedDTO;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import org.springframework.stereotype.Component;

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
}
