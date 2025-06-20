package dev.linkcentral.database.repository.studygroup;

import dev.member.entity.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    /**
     * 특정 회원과 스터디 그룹으로 StudyMember를 찾는다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     * @return StudyMember 엔티티의 Optional 객체
     */
    boolean existsByMemberAndStudyGroup(Member member, StudyGroup studyGroup);

    /**
     * 특정 회원이 특정 스터디 그룹에 속해 있는지 확인
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     * @return 속해 있으면 true, 그렇지 않으면 false
     */
    Optional<StudyMember> findByMemberAndStudyGroup(Member member, StudyGroup studyGroup);

    /**
     * 특정 회원 ID로 속해 있는 스터디 그룹 ID 목록을 찾는다.
     *
     * @param memberId 회원 ID
     * @return 스터디 그룹 ID 목록
     */
    @Query("SELECT sm.studyGroup.id FROM StudyMember sm WHERE sm.member.id = :memberId")
    List<Long> findStudyGroupIdsByMemberId(@Param("memberId") Long memberId);

    /**
     * 특정 스터디 그룹과 상태로 StudyMember 목록을 찾는다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     * @param status 스터디 그룹 상태
     * @return StudyMember 엔티티 목록
     */
    List<StudyMember> findAllByStudyGroupAndStatus(@Param("studyGroup") StudyGroup studyGroup,
                                                   @Param("status") StudyGroupStatus status);

    /**
     * 특정 스터디 그룹 ID로 StudyMember를 삭제
     *
     * @param studyGroupId 스터디 그룹 ID
     */
    @Modifying
    @Query("DELETE FROM StudyMember sm WHERE sm.studyGroup.id = :studyGroupId")
    void deleteByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    /**
     * 특정 회원 ID와 상태로 StudyMember 목록을 찾는다.
     *
     * @param memberId 회원 ID
     * @param status 스터디 그룹 상태
     * @return StudyMember 엔티티 목록
     */
    List<StudyMember> findAllByMemberIdAndStatus(Long memberId, StudyGroupStatus status);

    /**
     * 특정 스터디 그룹과 회원 ID로 StudyMember를 찾는다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     * @param memberId 회원 ID
     * @return StudyMember 엔티티의 Optional 객체
     */
    Optional<StudyMember> findByStudyGroupAndMemberId(StudyGroup studyGroup, Long memberId);

    /**
     * 특정 스터디 그룹 ID와 상태로 StudyMember 목록을 찾는다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param status 스터디 그룹 상태
     * @return StudyMember 엔티티 목록
     */
     @Query("SELECT sm FROM StudyMember sm JOIN FETCH sm.member WHERE sm.studyGroup.id = :studyGroupId AND sm.status = :status")
    List<StudyMember> findAllByStudyGroupIdAndStatus(@Param("studyGroupId") Long studyGroupId,
                                                     @Param("status") StudyGroupStatus status);

    /**
     * 특정 스터디 그룹 ID와 상태로 StudyMember 목록을 찾는다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param status 스터디 그룹 상태
     * @return StudyMember 엔티티 목록
     */
    List<StudyMember> findByStudyGroupIdAndStatus(Long studyGroupId, StudyGroupStatus status);
}
