package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.Profile;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.database.repository.ProfileRepository;
import dev.linkcentral.service.dto.profile.ProfileDetailsDTO;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import dev.linkcentral.infrastructure.s3.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final FileUploader fileUploader;

    public ProfileDetailsDTO getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .map(profile -> new ProfileDetailsDTO(
                        profile.getMember().getId(),
                        profile.getBio(),
                        profile.getImageUrl()))
                .orElse(new ProfileDetailsDTO(memberId, "자신을 소개해 주세요!", ""));
    }

    @Transactional
    public void updateProfile(ProfileUpdateDTO profileDTO, MultipartFile imageFile) {
        Profile profile = findOrCreateProfile(profileDTO.getMemberId());
        updateProfileDetails(profile, profileDTO.getBio(), imageFile);
    }

    public Profile findOrCreateProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
                    String defaultImageUrl = "/images/default.png"; // 기본 이미지 경로
                    Profile newProfile = new Profile(member, "자신을 소개해 주세요!", defaultImageUrl);
                    return profileRepository.save(newProfile);
                });
    }

    private void updateProfileDetails(Profile profile, String bio, MultipartFile imageFile) {
        profile.updateBio(bio);

        if (!imageFile.isEmpty()) {
            String imageUrl = fileUploader.uploadFile(
                    imageFile, "profile-images/" + profile.getMember().getId()
            );
            profile.updateImageUrl(imageUrl);
        }
        profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public Profile getProfileByMemberId(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));
    }
}
