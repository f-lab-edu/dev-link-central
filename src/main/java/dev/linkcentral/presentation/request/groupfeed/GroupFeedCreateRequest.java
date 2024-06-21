package dev.linkcentral.presentation.request.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedCreateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "피드 작성을 위한 요청 데이터 모델")
public class GroupFeedCreateRequest {

    @ApiModelProperty(value = "멤버 ID", required = true)
    private Long memberId;

    @ApiModelProperty(value = "게시글의 제목", required = true)
    @NotBlank(message = "게시글 제목은 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @ApiModelProperty(value = "게시글의 내용", required = true)
    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    @ApiModelProperty(value = "게시글의 작성자 이름", required = true)
    @NotBlank(message = "작성자 이름은 필수 입력 항목입니다.")
    private String writer;

    @ApiModelProperty(value = "피드 이미지 URL")
    private MultipartFile image;

    public static GroupFeedCreateDTO toGroupFeedCreateCommand(GroupFeedCreateRequest createRequest) {
        return GroupFeedCreateDTO.builder()
                .memberId(createRequest.memberId)
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .writer(createRequest.getWriter())
                .imageFile(createRequest.getImage())
                .build();
    }
}
