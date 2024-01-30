package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.Profile;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.database.repository.ProfileRepository;
import dev.linkcentral.infrastructure.AwsS3Uploader;
import dev.linkcentral.service.dto.request.ProfileRequestDTO;
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

    // 회원 ID로 프로필 정보를 조회하고 반환
    public ProfileRequestDTO getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .map(profile -> new ProfileRequestDTO(
                        profile.getMember().getId(),
                        profile.getBio(),
                        profile.getImageUrl()))
                .orElse(new ProfileRequestDTO(memberId, "자신을 소개해 주세요!", ""));
    }

    @Transactional
    public void updateProfile(ProfileRequestDTO profileDTO, MultipartFile imageFile) {
        Profile profile = findOrCreateProfile(profileDTO.getMemberId());
        updateProfileDetails(profile, profileDTO.getBio(), imageFile);
    }

    // 회원 ID로 프로필을 찾거나 새로 생성
    private Profile findOrCreateProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseGet(() -> { Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
                    return new Profile(member, "", "");
                });
    }

    private void updateProfileDetails(Profile profile, String bio, MultipartFile imageFile) {
        profile.updateBio(bio); // 프로필의 Bio 업데이트

        // 이미지가 제공된 경우, 이미지 업로드 및 URL 업데이트
        if (!imageFile.isEmpty()) {
            String imageUrl = awsS3Uploader.uploadFile(
                    imageFile, "profile-images/" + profile.getMember().getId());
            profile.updateImageUrl(imageUrl);
        }
        profileRepository.save(profile);
    }
}