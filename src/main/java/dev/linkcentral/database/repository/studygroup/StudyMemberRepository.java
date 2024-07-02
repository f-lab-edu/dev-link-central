package dev.linkcentral.database.repository.studygroup;

import dev.linkcentral.database.entity.member.Member;
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

    boolean existsByMemberAndStudyGroup(Member member, StudyGroup studyGroup);

    Optional<StudyMember> findByMemberAndStudyGroup(Member member, StudyGroup studyGroup);

    @Query("SELECT sm.studyGroup.id FROM StudyMember sm WHERE sm.member.id = :memberId")
    List<Long> findStudyGroupIdsByMemberId(@Param("memberId") Long memberId);

    List<StudyMember> findAllByStudyGroupAndStatus(@Param("studyGroup") StudyGroup studyGroup,
                                                   @Param("status") StudyGroupStatus status);

    @Modifying
    @Query("DELETE FROM StudyMember sm WHERE sm.studyGroup.id = :studyGroupId")
    void deleteByStudyGroupId(@Param("studyGroupId") Long studyGroupId);

    List<StudyMember> findAllByMemberIdAndStatus(Long memberId, StudyGroupStatus status);

    Optional<StudyMember> findByStudyGroupAndMemberId(StudyGroup studyGroup, Long memberId);

    @Query("SELECT sm FROM StudyMember sm JOIN FETCH sm.member WHERE sm.studyGroup.id = :studyGroupId AND sm.status = :status")
    List<StudyMember> findAllByStudyGroupIdAndStatus(@Param("studyGroupId") Long studyGroupId, @Param("status") StudyGroupStatus status);
}
