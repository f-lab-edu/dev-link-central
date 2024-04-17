package dev.linkcentral.mapper;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.MemberEditDTO;
import dev.linkcentral.presentation.dto.request.MemberEditRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    public MemberEditDTO toMemberEditDTO(MemberEditRequest request) {
        return MemberEditDTO.builder()
                .id(request.getId())
                .name(request.getName())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build();
    }

    public void updateMemberFromEditDTO(Member memberEntity, MemberEditDTO memberEditDTO) {
        if (memberEditDTO.getPassword() != null && !memberEditDTO.getPassword().isEmpty()) {
            memberEntity.updatePasswordHash(passwordEncoder.encode(memberEditDTO.getPassword()));
        }
        memberEntity.updateName(memberEditDTO.getName());
        memberEntity.updateNickname(memberEditDTO.getNickname());
    }

}
