package dev.linkcentral.service;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.database.repository.profile.ProfileRepository;
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

//    private final FileUploader fileUploader;
    private final ProfileMapper profileMapper;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    /**
     * 주어진 회원의 프로필 세부 정보를 가져온다
     *
     * @param memberId 프로필을 가져올 회원의 ID
     * @return 프로필 세부 정보
     */
    public ProfileDetailsDTO getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .map(profile -> profileMapper.toProfileDetailsDTO(profile))
                .orElse(new ProfileDetailsDTO(memberId, "자신을 소개해 주세요!", ""));
    }

    /**
     * 회원의 프로필을 업데이트
     *
     * @param profileDTO 프로필 업데이트 데이터 전송 객체
     * @param imageFile  업로드할 이미지 파일
     */
    @Transactional
    public void updateProfile(ProfileUpdateDTO profileDTO, MultipartFile imageFile) {
        Profile profile = findOrCreateProfile(profileDTO.getMemberId());
        updateProfileDetails(profile, profileDTO.getBio(), imageFile);
    }

    /**
     * 주어진 회원 ID에 대한 프로필을 가져온다
     *
     * @param memberId 프로필을 가져올 회원의 ID
     * @return 프로필
     */
    @Transactional(readOnly = true)
    public Profile getProfileByMemberId(Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));
    }

    /**
     * 주어진 회원 ID에 대한 프로필을 찾거나 생성
     *
     * @param memberId 회원의 ID
     * @return 프로필
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
     * 프로필 세부 정보를 업데이트
     *
     * @param profile  업데이트할 프로필
     * @param bio      새로운 자기소개
     * @param imageFile 업로드할 이미지 파일
     */
    private void updateProfileDetails(Profile profile, String bio, MultipartFile imageFile) {
        profile.updateBio(bio);

//        if (imageFile != null && !imageFile.isEmpty()) {
//            String imageUrl = fileUploader.uploadFile(imageFile, "profile-images/" + profile.getMember().getId());
//            profile.updateImageUrl(imageUrl);
//        }
        profileRepository.save(profile);
    }
}
