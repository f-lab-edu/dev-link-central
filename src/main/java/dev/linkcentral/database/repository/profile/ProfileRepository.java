package dev.linkcentral.database.repository.profile;

import dev.linkcentral.database.entity.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * 특정 회원 ID로 프로필을 찾는다.
     *
     * @param memberId 회원 ID
     * @return 프로필 엔티티의 Optional 객체
     */
    Optional<Profile> findByMemberId(Long memberId);
}