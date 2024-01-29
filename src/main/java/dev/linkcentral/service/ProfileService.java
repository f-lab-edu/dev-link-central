package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.Profile;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.database.repository.ProfileRepository;
import dev.linkcentral.service.dto.request.ProfileRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void updateOrCreateProfile(ProfileRequestDTO profileDTO) {
        Member member = memberRepository.findById(profileDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));

        // Profile 엔티티 찾기 또는 새로 생성
        Profile profile = profileRepository.findByMemberId(profileDTO.getMemberId())
                .orElseGet(() -> new Profile(member, "", "")); // 새 Profile 인스턴스 생성

        // Profile 업데이트
        profile.updateBio(profileDTO.getBio());
        profile.updateImageUrl(profileDTO.getImageUrl());
        profileRepository.save(profile);
    }

    public ProfileRequestDTO getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .map(profile -> new ProfileRequestDTO(
                        profile.getMember().getId(),
                        profile.getBio(),
                        profile.getImageUrl()))
                .orElse(new ProfileRequestDTO(memberId, "", ""));
    }
}