package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long joinMember(MemberSaveRequestDTO member) {
        member.updateRole("USER");
        Member memberEntity = Member.builder()
                .name(member.getName())
                .password(member.getPassword())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .role(member.getRole())
                .build();

        return memberRepository.save(memberEntity).getId();
    }

    public Optional<Member> loginMember(String name, String password) {
        List<Member> memberList = memberRepository.findByName(name);

        for (Member member : memberList) {
            if (passwordEncoder.matches(password, member.getPassword())) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }
}