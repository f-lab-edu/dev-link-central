package dev.linkcentral.service.helper;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.profile.Profile;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.database.repository.profile.ProfileRepository;
import dev.linkcentral.infrastructure.s3.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class ProfileServiceHelper {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final FileUploader fileUploader;

    /**
     * 회원 ID로 프로필을 찾거나 생성합니다.
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
    public void updateProfileDetails(Profile profile, String bio, MultipartFile imageFile) {
        profile.updateBio(bio);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileUploader.uploadFile(
                    imageFile, "profile-images/" + profile.getMember().getId()
            );
            profile.updateImageUrl(imageUrl);
        }
        profileRepository.save(profile);
    }
}
