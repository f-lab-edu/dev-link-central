package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.request.friend.FriendshipRequest;
import dev.linkcentral.presentation.response.friend.*;
import dev.linkcentral.service.dto.friend.FriendMemberInfoDTO;
import dev.linkcentral.service.dto.friend.FriendRequestDTO;
import dev.linkcentral.service.dto.friend.FriendshipDetailDTO;
import dev.linkcentral.service.facade.FriendFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendFacade friendFacade;

    /**
     * 현재 로그인한 회원의 정보를 반환합니다.
     *
     * @return 회원 정보 응답
     */
    @GetMapping("/auth/member-info")
    public ResponseEntity<FriendMemberInfoResponse> getMemberInfo() {
        FriendMemberInfoDTO memberInfoDTO = friendFacade.getMemberInfo();
        FriendMemberInfoResponse response = FriendMemberInfoResponse.toFriendMemberInfoResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청을 보냅니다.
     *
     * @param friendshipRequest 친구 요청 정보
     * @return 친구 요청 응답
     */
    @PostMapping("/request")
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@Validated @RequestBody FriendshipRequest friendshipRequest) {
        FriendRequestDTO friendRequestDTO = FriendshipRequest.toFriendRequestCommand(friendshipRequest);
        Long friendRequestId = friendFacade.sendFriendRequest(friendRequestDTO);
        FriendRequestResponse response = FriendRequestResponse.toFriendRequestResponse(friendRequestId);
        return ResponseEntity.ok(response);
    }

    /**
     * 수신된 친구 요청 목록을 반환합니다.
     *
     * @param receiverId 수신자 ID
     * @return 친구 요청 목록 응답
     */
    @GetMapping("/received-requests/{receiverId}")
    public ResponseEntity<FriendReceivedResponse> getReceivedFriendRequests(@PathVariable Long receiverId) {
        List<FriendRequestDTO> friendRequests = friendFacade.getReceivedFriendRequests(receiverId);
        FriendReceivedResponse response = FriendReceivedResponse.toFriendReceivedResponse(friendRequests);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청을 수락합니다.
     *
     * @param requestId 친구 요청 ID
     * @return 응답 상태
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long requestId) {
        friendFacade.acceptFriendRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 친구 관계 ID를 조회합니다.
     *
     * @param senderId 발신자 ID
     * @param receiverId 수신자 ID
     * @return 친구 관계 ID 응답
     */
    @GetMapping("/friendship-ids")
    public ResponseEntity<FriendshipResponse> findFriendshipId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long friendshipId = friendFacade.findFriendshipId(senderId, receiverId);
        FriendshipResponse response = FriendshipResponse.toFriendshipId(friendshipId);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청을 거절합니다.
     *
     * @param requestId 친구 요청 ID
     * @return 응답 상태
     */
    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long requestId) {
        friendFacade.rejectFriendRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 친구 관계를 끊습니다.
     *
     * @param friendId 친구 관계 ID
     * @return 응답 상태
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> unfriend(@PathVariable Long friendId) {
        friendFacade.unfriend(friendId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 선택한 친구 관계들을 끊습니다.
     *
     * @param friendIds 친구 관계 ID 목록
     * @return 응답 상태
     */
    @PostMapping("/unfriend")
    public ResponseEntity<Void> unfriendSelected(@RequestBody List<Long> friendIds) {
        friendFacade.unfriendSelected(friendIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원의 친구 관계 목록을 반환합니다.
     *
     * @param memberId 회원 ID
     * @return 친구 관계 목록 응답
     */
    @GetMapping("/{memberId}/friendships")
    public ResponseEntity<List<FriendshipDetailResponse>> getFriendships(@PathVariable Long memberId) {
        List<FriendshipDetailDTO> friendships = friendFacade.getFriendships(memberId);
        List<FriendshipDetailResponse> responses = FriendshipDetailResponse.toFriendshipDetailResponseList(friendships);
        return ResponseEntity.ok(responses);
    }
}
