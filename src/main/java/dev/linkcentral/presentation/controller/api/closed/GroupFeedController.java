package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.groupfeed.GroupFeedCreateRequest;
import dev.linkcentral.presentation.response.groupfeed.GroupFeedCreateResponse;
import dev.linkcentral.presentation.response.groupfeed.GroupFeedInfoResponse;
import dev.linkcentral.service.dto.groupfeed.GroupFeedCreateDTO;
import dev.linkcentral.service.dto.groupfeed.GroupFeedSavedDTO;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.facade.GroupFeedFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/group-feed")
public class GroupFeedController {

    private final GroupFeedFacade groupFeedFacade;

    @GetMapping("/auth/member-info")
    public ResponseEntity<GroupFeedInfoResponse> getGroupFeedInfo() {
        MemberCurrentDTO memberCurrentDTO = groupFeedFacade.getUserInfo();
        GroupFeedInfoResponse response = GroupFeedInfoResponse.toGroupFeedInfoResponse(memberCurrentDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<GroupFeedCreateResponse> createGroupFeed(@Validated @ModelAttribute GroupFeedCreateRequest groupFeedCreateRequest) {
        GroupFeedCreateDTO groupFeedCreateDTO = GroupFeedCreateRequest.toGroupFeedCreateCommand(groupFeedCreateRequest);
        GroupFeedSavedDTO groupFeedSavedDTO = groupFeedFacade.createGroupFeed(groupFeedCreateDTO);
        GroupFeedCreateResponse response = GroupFeedCreateResponse.toGroupFeedCreateResponse(groupFeedSavedDTO);
        return ResponseEntity.ok(response);
    }
}
