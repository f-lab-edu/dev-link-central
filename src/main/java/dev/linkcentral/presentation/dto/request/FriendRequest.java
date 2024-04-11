package dev.linkcentral.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequest {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderName;
}