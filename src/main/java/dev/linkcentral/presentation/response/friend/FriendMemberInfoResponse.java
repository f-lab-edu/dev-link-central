package dev.linkcentral.presentation.response.friend;

import dev.linkcentral.service.dto.friend.FriendMemberInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendMemberInfoResponse {

    private Long memberId;

    public static FriendMemberInfoResponse toFriendMemberInfoResponse(FriendMemberInfoDTO memberInfoDTO) {
        return new FriendMemberInfoResponse(memberInfoDTO.getMemberId());
    }
}
