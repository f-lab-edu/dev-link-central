package dev.linkcentral.presentation.request.friend;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "친구 요청을 위한 데이터")
public class FriendshipRequest {

    @ApiModelProperty(value = "친구 요청의 고유 식별자")
    private Long id;

    @ApiModelProperty(value = "친구 요청을 보내는 사용자의 ID", required = true)
    @NotNull(message = "보내는 사람의 ID는 필수입니다.")
    private Long senderId;

    @ApiModelProperty(value = "친구 요청을 받는 사용자의 ID", required = true)
    @NotNull(message = "받는 사람의 ID는 필수입니다.")
    private Long receiverId;

    @ApiModelProperty(value = "보내는 사람의 이름", required = true)
    @NotNull(message = "보내는 사람의 이름은 필수입니다.")
    @Size(min = 1, max = 100, message = "보내는 사람의 이름은 1자 이상 100자 이하이어야 합니다.")
    private String senderName;
}
