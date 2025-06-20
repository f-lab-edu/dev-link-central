package dev.member.entity;

import dev.common.BaseEntity;
import dev.member.constant.MemberRole;
import dev.member.controller.request.SignUpRequest;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private boolean deleted = Boolean.FALSE;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roles = Set.of(MemberRole.USER);

    @Builder
    public Member(
            Long id,
            String name,
            String passwordHash,
            String email,
            String nickname,
            boolean deleted,
            Set<MemberRole> roles
    ) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.email = email;
        this.nickname = nickname;
        this.deleted = deleted;
        this.roles = roles;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.passwordHash = passwordEncoder.encode(newPassword);
    }

    public static Member fromSignUpRequest(SignUpRequest dto, PasswordEncoder passwordEncoder) {
        Set<MemberRole> memberRoles = convertToMemberRoles(dto.getRoles());
        return Member.builder()
                .name(dto.getName())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .roles(memberRoles)
                .build();
    }

    private static Set<MemberRole> convertToMemberRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(MemberRole.USER);
        }
        return roles.stream()
                .map(MemberRole::valueOf)
                .collect(Collectors.toSet());
    }
}
