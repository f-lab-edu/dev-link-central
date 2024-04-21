package dev.linkcentral.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderName;

}
