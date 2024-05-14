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
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendFacade friendFacade;

    @GetMapping("/auth/member-info")
    public ResponseEntity<FriendMemberInfoResponse> getMemberInfo() {
        FriendMemberInfoDTO memberInfoDTO = friendFacade.getMemberInfo();
        FriendMemberInfoResponse response = FriendMemberInfoResponse.toFriendMemberInfoResponse(memberInfoDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/request")
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(@Validated @RequestBody FriendshipRequest friendshipRequest) {
        FriendRequestDTO friendRequestDTO = FriendshipRequest.toFriendRequestCommand(friendshipRequest);
        Long friendRequestId = friendFacade.sendFriendRequest(friendRequestDTO);
        FriendRequestResponse response = FriendRequestResponse.toFriendRequestResponse(friendRequestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/received-requests/{receiverId}")
    public ResponseEntity<FriendReceivedResponse> getReceivedFriendRequests(@PathVariable Long receiverId) {
        List<FriendRequestDTO> friendRequests = friendFacade.getReceivedFriendRequests(receiverId);
        FriendReceivedResponse response = FriendReceivedResponse.toFriendReceivedResponse(friendRequests);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable Long requestId) {
        friendFacade.acceptFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friendship-ids")
    public ResponseEntity<FriendshipResponse> findFriendshipId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long friendshipId = friendFacade.findFriendshipId(senderId, receiverId);
        FriendshipResponse response = FriendshipResponse.toFriendshipId(friendshipId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable Long requestId) {
        friendFacade.rejectFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> unfriend(@PathVariable Long friendId) {
        friendFacade.unfriend(friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfriend")
    public ResponseEntity<Void> unfriendSelected(@RequestBody List<Long> friendIds) {
        friendFacade.unfriendSelected(friendIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}/friendships")
    public ResponseEntity<List<FriendshipDetailResponse>> getFriendships(@PathVariable Long memberId) {
        List<FriendshipDetailDTO> friendships = friendFacade.getFriendships(memberId);
        List<FriendshipDetailResponse> responses = FriendshipDetailResponse.toFriendshipDetailResponseList(friendships);
        return ResponseEntity.ok(responses);
    }

}
