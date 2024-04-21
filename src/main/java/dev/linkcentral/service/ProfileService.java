package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.Profile;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.database.repository.ProfileRepository;
import dev.linkcentral.infrastructure.s3.AwsS3Uploader;
import dev.linkcentral.presentation.dto.ProfileDetailsDTO;
import dev.linkcentral.presentation.dto.ProfileUpdateDTO;
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
    private final AwsS3Uploader awsS3Uploader;

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

    private Profile findOrCreateProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseGet(() -> { Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
                    return new Profile(member, "", "");
                });
    }

    private void updateProfileDetails(Profile profile, String bio, MultipartFile imageFile) {
        profile.updateBio(bio);

        if (!imageFile.isEmpty()) {
            String imageUrl = awsS3Uploader.uploadFile(
                    imageFile, "profile-images/" + profile.getMember().getId()
            );
            profile.updateImageUrl(imageUrl);
        }
        profileRepository.save(profile);
    }
}