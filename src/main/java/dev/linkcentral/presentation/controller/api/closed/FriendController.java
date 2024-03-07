package dev.linkcentral.presentation.controller.api.closed;

import dev.linkcentral.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/friendship-ids")
    public ResponseEntity<?> getFriendshipId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long friendId = friendService.findFriendshipId(senderId, receiverId);
        if (friendId != null) {
            return ResponseEntity.ok(friendId);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        friendService.createFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.updateFriendRequestStatus(requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> unfriend(@PathVariable Long friendId) {
        friendService.deleteFriendship(friendId);
        return ResponseEntity.ok().build();
    }

}