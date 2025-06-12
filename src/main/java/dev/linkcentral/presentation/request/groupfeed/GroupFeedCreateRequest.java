package dev.linkcentral.presentation.request.groupfeed;

import dev.linkcentral.service.dto.groupfeed.GroupFeedCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCreateRequest {

    private Long memberId;

    @NotBlank(message = "게시글 제목은 필수 입력 항목입니다.")
    @Size(min = 3, max = 100, message = "게시글 제목은 3자 이상 100자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수 입력 항목입니다.")
    @Size(min = 3, max = 10000, message = "게시글 내용은 3자 이상 10000자 이하이어야 합니다.")
    private String content;

    @NotBlank(message = "작성자 이름은 필수 입력 항목입니다.")
    private String writer;

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
