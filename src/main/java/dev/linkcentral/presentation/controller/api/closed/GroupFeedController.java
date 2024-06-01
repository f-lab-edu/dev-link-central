package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.groupfeed.GroupFeedCommentRequest;
import dev.linkcentral.presentation.request.groupfeed.GroupFeedCreateRequest;
import dev.linkcentral.presentation.request.groupfeed.GroupFeedUpdateRequest;
import dev.linkcentral.presentation.response.groupfeed.*;
import dev.linkcentral.service.dto.groupfeed.*;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.facade.GroupFeedFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<GroupFeedCreateResponse> createGroupFeed(
            @Validated @ModelAttribute GroupFeedCreateRequest groupFeedCreateRequest) {

        GroupFeedCreateDTO groupFeedCreateDTO = GroupFeedCreateRequest.toGroupFeedCreateCommand(groupFeedCreateRequest);
        GroupFeedSavedDTO groupFeedSavedDTO = groupFeedFacade.createGroupFeed(groupFeedCreateDTO);
        GroupFeedCreateResponse response = GroupFeedCreateResponse.toGroupFeedCreateResponse(groupFeedSavedDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<GroupFeedListResponse> getGroupFeeds(@PageableDefault(size = 3) Pageable pageable) {
        Page<GroupFeedWithProfileDTO> groupFeeds = groupFeedFacade.getGroupFeeds(pageable);
        GroupFeedListResponse response = GroupFeedListResponse.toGroupFeedListResponse(groupFeeds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-feeds")
    public ResponseEntity<MyFeedListResponse> getMyFeeds() {
        List<MyGroupFeedDetailsDTO> myFeeds = groupFeedFacade.getAllFeedsByMemberId();
        MyFeedListResponse response = MyFeedListResponse.toMyFeedListResponse(myFeeds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<GroupFeedWithProfileDTO> getFeedDetail(@PathVariable Long feedId) {
        GroupFeedWithProfileDTO feed = groupFeedFacade.getFeedById(feedId);
        return ResponseEntity.ok(feed);
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Long feedId) {
        groupFeedFacade.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{feedId}")
    public ResponseEntity<Void> updateGroupFeed(@PathVariable Long feedId,
                     @Validated @RequestPart("groupFeedUpdateRequest") GroupFeedUpdateRequest groupFeedUpdateRequest,
                     @RequestPart(value = "image", required = false) MultipartFile image) {

        groupFeedUpdateRequest.setImage(image);
        GroupFeedUpdateDTO feedUpdateDTO = GroupFeedUpdateRequest.toGroupFeedUpdateCommand(feedId, groupFeedUpdateRequest);
        groupFeedFacade.updateGroupFeed(feedUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{feedId}/comments")
    public ResponseEntity<Void> createGroupFeedComment(@PathVariable Long feedId,
                                                       @Validated @RequestBody GroupFeedCommentRequest commentRequest) {
        GroupFeedCommentDTO feedCommentDTO = GroupFeedCommentRequest.toGroupFeedCommentCommand(commentRequest);
        groupFeedFacade.addComment(feedId, feedCommentDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{feedId}/comments")
    public ResponseEntity<GroupFeedCommentResponse> getComments(@PathVariable Long feedId) {
        List<GroupFeedCommentDTO> comments = groupFeedFacade.getComments(feedId);
        GroupFeedCommentResponse response = GroupFeedCommentResponse.toGroupFeedCommentResponse(comments);
        return ResponseEntity.ok(response);
    }
}
