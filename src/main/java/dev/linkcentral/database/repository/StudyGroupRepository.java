package dev.linkcentral.database.repository;

import dev.linkcentral.database.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    Optional<StudyGroup> findByStudyLeaderId(Long leaderId);

    boolean existsByStudyLeaderId(Long studyLeaderId);

    @Query("SELECT sm.studyGroup FROM StudyMember sm WHERE sm.member.id = :userId")
    List<StudyGroup> findStudyGroupsByUserId(@Param("userId") Long userId);
}
