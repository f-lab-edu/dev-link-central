package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    @Modifying
    @Query("update Member m set m.passwordHash = :password_hash where m.id = :id")
    void updatePasswordById(@Param("id") Long id, @Param("password_hash") String passwordHash);

    Optional<Member> findByNickname(String nickname);
}