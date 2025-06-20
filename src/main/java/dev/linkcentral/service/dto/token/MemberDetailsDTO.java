package dev.linkcentral.service.dto.token;

import dev.member.constant.MemberRole;
import dev.member.entity.Member;
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

    public static MemberDetailsDTO from(Member member) {
        List<String> roleNames = member.getRoles().stream()
                .map(MemberRole::name)
                .toList();

        return MemberDetailsDTO.builder()
                .id(member.getId())
                .password(member.getPasswordHash())
                .name(member.getName())
                .email(member.getEmail())
                .roles(roleNames)
                .isDeleted(member.isDeleted())
                .build();
    }
}
