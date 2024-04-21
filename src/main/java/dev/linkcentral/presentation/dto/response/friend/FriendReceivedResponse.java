package dev.linkcentral.presentation.dto.response.friend;

import dev.linkcentral.presentation.dto.FriendRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendReceivedResponse {

    private boolean success;
    private String message;
    private List<FriendRequestDTO> friendRequests;
}
