package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByName(String name);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String memberEmail);

    @Modifying
    @Query("update Member m set m.password = :password where m.id = :id")
    void updatePasswordById(@Param("id") Long id, @Param("password") String password);

}