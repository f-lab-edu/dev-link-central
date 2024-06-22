package dev.linkcentral.database.repository.profile;

import dev.linkcentral.database.entity.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByMemberId(Long memberId);
}