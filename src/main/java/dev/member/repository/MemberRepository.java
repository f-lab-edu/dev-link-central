package dev.member.repository;

import dev.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);

    boolean existsByNicknameAndDeletedFalse(String nickname);

    Optional<Member> findByEmailAndDeletedFalse(String email);

    @Query("select count(m) from Member m where m.email = :email and m.deleted = false")
    Long countByEmailIgnoringDeleted(@Param("email") String email);

    @Modifying
    @Query("update Member m set m.deleted = true where m.id = :id")
    void softDeleteById(@Param("id") Long id);
}
