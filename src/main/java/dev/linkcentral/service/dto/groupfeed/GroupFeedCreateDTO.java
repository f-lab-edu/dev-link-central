package dev.linkcentral.service.dto.groupfeed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupFeedCreateDTO {

    private Long memberId;
    private String title;
    private String content;
    private String writer;
    private MultipartFile imageFile;
}
