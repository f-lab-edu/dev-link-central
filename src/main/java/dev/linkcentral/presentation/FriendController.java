package dev.linkcentral.presentation;

import dev.linkcentral.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/getFriendshipId")
    public ResponseEntity<?> getFriendshipId(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long friendId = friendService.getFriendshipId(senderId, receiverId);
        if (friendId != null) {
            return ResponseEntity.ok(friendId);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> unfriend(@PathVariable Long friendId) {
        friendService.unfriend(friendId);
        return ResponseEntity.ok().build();
    }

}