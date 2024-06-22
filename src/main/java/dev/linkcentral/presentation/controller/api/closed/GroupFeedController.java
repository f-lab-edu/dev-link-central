package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.groupfeed.GroupFeedCommentRequest;
import dev.linkcentral.presentation.request.groupfeed.GroupFeedCommentUpdateRequest;
import dev.linkcentral.presentation.request.groupfeed.GroupFeedCreateRequest;
import dev.linkcentral.presentation.request.groupfeed.GroupFeedUpdateRequest;
import dev.linkcentral.presentation.response.groupfeed.*;
import dev.linkcentral.service.dto.groupfeed.*;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.facade.GroupFeedFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    /**
     * 현재 사용자의 정보를 반환합니다.
     *
     * @return 사용자 정보 응답
     */
    @GetMapping("/auth/member-info")
    public ResponseEntity<GroupFeedInfoResponse> getGroupFeedInfo() {
        MemberCurrentDTO memberCurrentDTO = groupFeedFacade.getUserInfo();
        GroupFeedInfoResponse response = GroupFeedInfoResponse.toGroupFeedInfoResponse(memberCurrentDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 피드를 생성합니다.
     *
     * @param groupFeedCreateRequest 그룹 피드 생성 요청
     * @return 생성된 그룹 피드 응답
     */
    @PostMapping
    public ResponseEntity<GroupFeedCreateResponse> createGroupFeed(
            @Validated @ModelAttribute GroupFeedCreateRequest groupFeedCreateRequest) {

        GroupFeedCreateDTO groupFeedCreateDTO = GroupFeedCreateRequest.toGroupFeedCreateCommand(groupFeedCreateRequest);
        GroupFeedSavedDTO groupFeedSavedDTO = groupFeedFacade.createGroupFeed(groupFeedCreateDTO);
        GroupFeedCreateResponse response = GroupFeedCreateResponse.toGroupFeedCreateResponse(groupFeedSavedDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 피드 목록을 반환합니다.
     *
     * @param offset 페이지 시작점
     * @param limit 페이지 크기
     * @return 그룹 피드 목록 응답
     */
    @GetMapping
    public ResponseEntity<GroupFeedListResponse> getGroupFeeds(@RequestParam int offset, @RequestParam int limit) {
        Page<GroupFeedWithProfileDTO> groupFeeds = groupFeedFacade.getGroupFeeds(offset, limit);
        GroupFeedListResponse response = GroupFeedListResponse.toGroupFeedListResponse(groupFeeds);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 사용자의 모든 피드 목록을 반환합니다.
     *
     * @return 사용자의 피드 목록 응답
     */
    @GetMapping("/my-feeds")
    public ResponseEntity<MyFeedListResponse> getMyFeeds() {
        List<MyGroupFeedDetailsDTO> myFeeds = groupFeedFacade.getAllFeedsByMemberId();
        MyFeedListResponse response = MyFeedListResponse.toMyFeedListResponse(myFeeds);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 피드의 세부 정보를 반환합니다.
     *
     * @param feedId 피드 ID
     * @return 피드 세부 정보 응답
     */
    @GetMapping("/{feedId}")
    public ResponseEntity<GroupFeedWithProfileDTO> getFeedDetail(@PathVariable Long feedId) {
        GroupFeedWithProfileDTO feed = groupFeedFacade.getFeedById(feedId);
        return ResponseEntity.ok(feed);
    }

    /**
     * 특정 피드를 삭제합니다.
     *
     * @param feedId 피드 ID
     * @return 응답 없음 (204 No Content)
     */
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(@PathVariable Long feedId) {
        groupFeedFacade.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 피드를 업데이트합니다.
     *
     * @param feedId 피드 ID
     * @param groupFeedUpdateRequest 그룹 피드 업데이트 요청
     * @param image 이미지 파일
     * @return 응답 없음 (204 No Content)
     */
    @PutMapping("/{feedId}")
    public ResponseEntity<Void> updateGroupFeed(@PathVariable Long feedId,
                     @Validated @RequestPart("groupFeedUpdateRequest") GroupFeedUpdateRequest groupFeedUpdateRequest,
                     @RequestPart(value = "image", required = false) MultipartFile image) {

        groupFeedUpdateRequest.setImage(image);
        GroupFeedUpdateDTO feedUpdateDTO = GroupFeedUpdateRequest.toGroupFeedUpdateCommand(feedId, groupFeedUpdateRequest);
        groupFeedFacade.updateGroupFeed(feedUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 피드에 댓글을 추가합니다.
     *
     * @param feedId 피드 ID
     * @param commentRequest 댓글 요청
     * @return 응답 없음 (200 OK)
     */
    @PostMapping("/{feedId}/comments")
    public ResponseEntity<Void> createGroupFeedComment(@PathVariable Long feedId,
                                @Validated @RequestBody GroupFeedCommentRequest commentRequest) {

        GroupFeedCommentDTO feedCommentDTO = GroupFeedCommentRequest.toGroupFeedCommentCommand(commentRequest);
        groupFeedFacade.addComment(feedId, feedCommentDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 피드의 모든 댓글을 반환합니다.
     *
     * @param feedId 피드 ID
     * @return 피드 댓글 목록 응답
     */
    @GetMapping("/{feedId}/comments")
    public ResponseEntity<GroupFeedCommentResponse> getComments(@PathVariable Long feedId) {
        List<GroupFeedCommentDTO> comments = groupFeedFacade.getComments(feedId);
        GroupFeedCommentResponse response = GroupFeedCommentResponse.toGroupFeedCommentResponse(comments);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 피드의 특정 댓글을 업데이트합니다.
     *
     * @param feedId 피드 ID
     * @param commentId 댓글 ID
     * @param commentUpdateRequest 댓글 업데이트 요청
     * @return 응답 없음 (204 No Content)
     */
    @PutMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long feedId, @PathVariable Long commentId,
                                              @Validated @RequestBody GroupFeedCommentUpdateRequest commentUpdateRequest) {

        GroupFeedCommentUpdateDTO commentUpdateDTO = GroupFeedCommentUpdateRequest
                .toGroupFeedCommentUpdateCommand(feedId, commentId, commentUpdateRequest);
        groupFeedFacade.updateComment(commentUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 피드의 특정 댓글을 삭제합니다.
     *
     * @param feedId 피드 ID
     * @param commentId 댓글 ID
     * @return 응답 없음 (204 No Content)
     */
    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long feedId, @PathVariable Long commentId) {
        groupFeedFacade.deleteComment(feedId, commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 피드에 좋아요를 토글합니다.
     *
     * @param feedId 피드 ID
     * @return 좋아요 상태 응답
     */
    @PostMapping("/{feedId}/like")
    public ResponseEntity<GroupFeedLikeResponse> toggleLike(@PathVariable Long feedId) {
        GroupFeedLikeDTO groupFeedLikeDTO = groupFeedFacade.toggleLike(feedId);
        GroupFeedLikeResponse response = GroupFeedLikeResponse.toGroupFeedLikeResponse(groupFeedLikeDTO);
        return ResponseEntity.ok(response);
    }
}
