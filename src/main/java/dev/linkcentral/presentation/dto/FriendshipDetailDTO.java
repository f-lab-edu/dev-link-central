package dev.linkcentral.presentation.dto;

import dev.linkcentral.database.entity.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDetailDTO {

    private Long friendshipId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private FriendStatus status;

}
