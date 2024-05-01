package dev.linkcentral.service.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedFriendRequestDTO {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String senderName;

}
