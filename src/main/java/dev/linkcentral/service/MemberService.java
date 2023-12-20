package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.service.dto.MemberSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

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
        Optional<Member> member = memberRepository.selectMember(name, password);
        return member;
    }
}