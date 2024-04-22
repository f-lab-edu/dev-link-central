package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.presentation.dto.FriendRequestDTO;
import dev.linkcentral.presentation.dto.FriendshipDetailDTO;
import dev.linkcentral.presentation.dto.request.friend.FriendRequest;
import dev.linkcentral.presentation.dto.response.friend.FriendReceivedResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendRequestResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendshipDetailResponse;
import dev.linkcentral.presentation.dto.response.friend.FriendshipResponse;
import dev.linkcentral.service.FriendService;
import dev.linkcentral.service.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friendship-ids")
    public ResponseEntity<FriendshipResponse> getFriendshipId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long friendId = friendService.findFriendshipId(senderId, receiverId);
        return ResponseEntity.ok(new FriendshipResponse(friendId));
    }

    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> unfriend(@PathVariable Long friendId) {
        friendService.deleteFriendship(friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfriend")
    public ResponseEntity<Void> unfriendSelected(@RequestBody List<Long> friendIds) {
        friendService.unfriendSelected(friendIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}/friendships")
    public ResponseEntity<List<FriendshipDetailResponse>> getFriendships(@PathVariable Long memberId) {
        List<FriendshipDetailDTO> friendships = friendService.getFriendships(memberId);
        List<FriendshipDetailResponse> responses = friendMapper.toFriendshipDetailResponseList(friendships);
        return ResponseEntity.ok(responses);
    }

}
