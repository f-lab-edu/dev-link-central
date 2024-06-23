package dev.linkcentral.service;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.database.repository.profile.ProfileRepository;
import dev.linkcentral.infrastructure.s3.FileUploader;
import dev.linkcentral.service.dto.profile.ProfileDetailsDTO;
import dev.linkcentral.service.dto.profile.ProfileUpdateDTO;
import dev.linkcentral.service.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final FileUploader fileUploader;
    private final ProfileMapper profileMapper;

    /**
     * 특정 회원의 프로필 정보를 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return ProfileDetailsDTO 프로필 상세 정보 DTO
     */
    public ProfileDetailsDTO getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .map(profile -> profileMapper.toProfileDetailsDTO(profile))
                .orElse(new ProfileDetailsDTO(memberId, "자신을 소개해 주세요!", ""));
    }

    /**
     * 프로필을 업데이트합니다.
     *
     * @param profileDTO 프로필 업데이트 DTO
     * @param imageFile 프로필 이미지 파일
     */
    @Transactional
    public void updateProfile(ProfileUpdateDTO profileDTO, MultipartFile imageFile) {
        Profile profile = findOrCreateProfile(profileDTO.getMemberId());
        updateProfileDetails(profile, profileDTO.getBio(), imageFile);
    }

    /**
     * 특정 회원 ID로 프로필을 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return Profile 프로필 엔티티
     */
    @Transactional(readOnly = true)
    public Profile getProfileByMemberId(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));
    }

    /**
     * 특정 회원 ID로 프로필을 찾거나 생성합니다.
     *
     * @param memberId 회원 ID
     * @return Profile 프로필 엔티티
     */
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

    /**
     * 프로필의 상세 정보를 업데이트합니다.
     *
     * @param profile 프로필 엔티티
     * @param bio 자기소개
     * @param imageFile 프로필 이미지 파일
     */
    private void updateProfileDetails(Profile profile, String bio, MultipartFile imageFile) {
        profile.updateBio(bio);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileUploader.uploadFile(imageFile, "profile-images/" + profile.getMember().getId());
            profile.updateImageUrl(imageUrl);
        }
        profileRepository.save(profile);
    }
}
