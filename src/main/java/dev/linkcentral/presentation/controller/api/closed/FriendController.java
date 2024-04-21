package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.dto.FriendRequestDTO;
import dev.linkcentral.presentation.dto.request.friend.FriendRequest;
import dev.linkcentral.presentation.dto.response.friend.FriendListResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendReceivedResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendRequestResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendshipDetailResponse;
import dev.linkcentral.service.FriendService;
import dev.linkcentral.service.mapper.FriendMapper;
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
    private final FriendMapper friendMapper;

    @PostMapping("/request")
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@RequestBody FriendRequest friendRequest) {
        FriendRequestDTO friendRequestDTO = friendMapper.toFriendRequestDTO(friendRequest);
        Long friendRequestId = friendService.sendFriendRequest(friendRequestDTO.getSenderId(), friendRequestDTO.getReceiverId());
        FriendRequestResponse response = friendMapper.createFriendRequestResponse(friendRequestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/received-requests/{receiverId}")
    public ResponseEntity<FriendReceivedResponse> getReceivedFriendRequests(@PathVariable Long receiverId) {
        List<FriendRequestDTO> friendRequests = friendService.getReceivedFriendRequests(receiverId);
        FriendReceivedResponse response = friendMapper.buildFriendReceivedResponse(friendRequests);
        return ResponseEntity.ok(response);
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