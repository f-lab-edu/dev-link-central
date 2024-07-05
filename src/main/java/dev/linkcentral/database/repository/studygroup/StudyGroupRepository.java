package dev.linkcentral.database.repository.studygroup;

import dev.linkcentral.database.entity.studygroup.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    /**
     * 리더 ID로 스터디 그룹을 찾는다.
     *
     * @param leaderId 리더 ID
     * @return 스터디 그룹 엔티티의 Optional 객체
     */
    Optional<StudyGroup> findByStudyLeaderId(Long leaderId);

    /**
     * 특정 리더 ID를 가진 스터디 그룹이 존재하는지 확인
     *
     * @param studyLeaderId 리더 ID
     * @return 스터디 그룹이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByStudyLeaderId(Long studyLeaderId);

    /**
     * 특정 사용자 ID로 스터디 그룹 목록을 찾는다.
     *
     * @param userId 사용자 ID
     * @return 스터디 그룹 엔티티 목록
     */
    @Query("SELECT sm.studyGroup FROM StudyMember sm WHERE sm.member.id = :userId")
    List<StudyGroup> findStudyGroupsByUserId(@Param("userId") Long userId);

    /**
     * 특정 회원 ID로 승인된 스터디 그룹 목록을 찾는다.
     *
     * @param memberId 회원 ID
     * @return 스터디 그룹 엔티티 목록
     */
    @Query("SELECT sg " +
            "FROM StudyGroup sg " +
            "JOIN StudyMember sm " +
            "ON sg.id = sm.studyGroup.id " +
            "WHERE sm.member.id = :memberId AND sm.status = 'ACCEPTED'")
    List<StudyGroup> findByMemberId(@Param("memberId") Long memberId);
}
