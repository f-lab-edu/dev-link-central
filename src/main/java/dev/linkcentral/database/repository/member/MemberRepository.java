package dev.linkcentral.database.repository.member;

import dev.linkcentral.database.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * ID로 회원을 찾는다.
     *
     * @param id 회원 ID
     * @return 회원 엔티티의 Optional 객체
     */
    Optional<Member> findById(Long id);

    /**
     * 삭제되지 않은 회원 중 주어진 닉네임을 가진 회원이 존재하는지 확인
     *
     * @param nickname 회원 닉네임
     * @return 닉네임이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByNicknameAndDeletedFalse(String nickname);

    /**
     * 삭제되지 않은 회원 중 주어진 이메일을 가진 회원을 찾는다.
     *
     * @param email 회원 이메일
     * @return 회원 엔티티의 Optional 객체
     */
    Optional<Member> findByEmailAndDeletedFalse(String email);

    /**
     * 삭제되지 않은 회원 중 주어진 닉네임을 가진 회원을 찾는다.
     *
     * @param nickname 회원 닉네임
     * @return 회원 엔티티의 Optional 객체
     */
    Optional<Member> findByNicknameAndDeletedFalse(String nickname);

    /**
     * 삭제되지 않은 회원 중 주어진 이메일을 가진 회원 수를 샌다.
     *
     * @param email 회원 이메일
     * @return 회원 수
     */
    @Query("select count(m) from Member m where m.email = :email and m.deleted = false")
    Long countByEmailIgnoringDeleted(@Param("email") String email);

    /**
     * 주어진 ID를 가진 회원의 비밀번호를 업데이트
     *
     * @param id 회원 ID
     * @param passwordHash 새 비밀번호 해시
     */
    @Modifying
    @Query("update Member m set m.passwordHash = :password_hash where m.id = :id")
    void updatePasswordById(@Param("id") Long id, @Param("password_hash") String passwordHash);

    /**
     * 주어진 ID를 가진 회원을 소프트 삭제
     *
     * @param id 회원 ID
     */
    @Modifying
    @Query("update Member m set m.deleted = true where m.id = :id")
    void softDeleteById(@Param("id") Long id);
}
