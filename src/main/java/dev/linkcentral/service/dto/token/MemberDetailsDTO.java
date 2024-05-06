package dev.linkcentral.service.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailsDTO {

    private Long id;
    private String password;
    private String name;
    private String email;
    private List<String> roles;
    private boolean isDeleted;
}
