package dev.linkcentral.presentation.response.friend;

import dev.linkcentral.service.dto.friend.FriendshipDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipDetailResponse {

    private Long friendshipId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private String status;

    public static List<FriendshipDetailResponse> toFriendshipDetailResponseList(List<FriendshipDetailDTO> friendshipDetails) {
        return friendshipDetails.stream()
                .map(dto -> FriendshipDetailResponse.builder()
                        .friendshipId(dto.getFriendshipId())
                        .senderId(dto.getSenderId())
                        .receiverId(dto.getReceiverId())
                        .senderName(dto.getSenderName())
                        .receiverName(dto.getReceiverName())
                        .status(dto.getStatusAsString())
                        .build())
                .collect(Collectors.toList());
    }
}
