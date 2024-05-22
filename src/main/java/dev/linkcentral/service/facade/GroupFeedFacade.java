package dev.linkcentral.service.facade;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.GroupFeedService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.groupfeed.GroupFeedCreateDTO;
import dev.linkcentral.service.dto.groupfeed.GroupFeedSavedDTO;
import dev.linkcentral.service.dto.groupfeed.GroupFeedWithProfileDTO;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.mapper.GroupFeedMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupFeedFacade {

    private final MemberService memberService;
    private final GroupFeedService groupFeedService;
    private final GroupFeedMapper groupFeedMapper;

    public MemberCurrentDTO getUserInfo() {
        Member currentMember = memberService.getCurrentMember();
        return groupFeedMapper.toMemberCurrentDTO(currentMember);
    }

    public GroupFeedSavedDTO createGroupFeed(GroupFeedCreateDTO groupFeedCreateDTO) {
        Member currentMember = memberService.getCurrentMember();
        return groupFeedService.createGroupFeed(groupFeedCreateDTO);
    }

    public Page<GroupFeedWithProfileDTO> getGroupFeeds(Pageable pageable) {
        return groupFeedService.getGroupFeeds(pageable);
    }
}
