package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.dto.request.FriendRequest;
import dev.linkcentral.presentation.dto.response.FriendListResponse;
import dev.linkcentral.presentation.dto.response.FriendshipDetailResponse;
import dev.linkcentral.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    /**
     * 친구 요청 보내기
     */
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        log.info("friendRequest.getSenderId: {}", friendRequest.getSenderId());     // 보낸사람 id
        log.info("friendRequest.getReceiverId: {}", friendRequest.getReceiverId()); // 수신자 id
        Long requestId = friendService.sendFriendRequest(
                friendRequest.getSenderId(),
                friendRequest.getReceiverId());
        return ResponseEntity.ok(requestId);
    }

    /**
     * 받은 친구 요청 목록 가져오기
     */
    @GetMapping("/received-requests/{receiverId}")
    public ResponseEntity<?> getReceivedFriendRequests(@PathVariable Long receiverId) {
        List<FriendRequest> friendRequests = friendService.getReceivedFriendRequests(receiverId);
        return ResponseEntity.ok(friendRequests);
    }

    /**
     * 친구 요청 수락
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId) {
        try {
            friendService.acceptFriendRequest(requestId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * 친구 ID 가져오기
     */
    @GetMapping("/friendship-ids")
    public ResponseEntity<?> getFriendshipId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        log.info("--> senderId: {}", senderId);
        log.info("--> receiverId: {}", receiverId);

        Long friendId = friendService.findFriendshipId(senderId, receiverId);
        if (friendId != null) {
            return ResponseEntity.ok(friendId);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 친구 거절
     */
    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable Long requestId) {
        try {
            friendService.rejectFriendRequest(requestId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.error("EntityNotFoundException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * 친구 끊기
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> unfriend(@PathVariable Long friendId) {
        friendService.deleteFriendship(friendId);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 목록 가져오기
     */
    @GetMapping("/{memberId}/friends")
    public ResponseEntity<?> getFriends(@PathVariable Long memberId) {
        List<FriendListResponse> friends = friendService.getFriends(memberId);
        return ResponseEntity.ok(friends);
    }

    /**
     * 선택한 친구들과의 친구 관계를 끊는 API
     */
    @PostMapping("/unfriend")
    public ResponseEntity<?> unfriendSelected(@RequestBody List<Long> friendIds) {
        friendService.unfriendSelected(friendIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 관계의 ID를 가져오는 API
     */
    @GetMapping("/{memberId}/friendships")
    public ResponseEntity<?> getFriendships(@PathVariable Long memberId) {
        List<FriendshipDetailResponse> friendships = friendService.getFriendships(memberId);
        return ResponseEntity.ok(friendships);
    }

}