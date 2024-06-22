package dev.linkcentral.service.helper;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.database.repository.studygroup.StudyGroupRepository;
import dev.linkcentral.database.repository.studygroup.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudyMemberServiceHelper {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 특정 ID로 스터디 그룹을 찾습니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return StudyGroup 스터디 그룹 엔티티
     */
    public StudyGroup findStudyGroupById(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 특정 회원과 스터디 그룹으로 스터디 멤버 존재 여부를 확인합니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     * @return boolean 스터디 멤버 존재 여부
     */
    public boolean existsByMemberAndStudyGroup(Member member, StudyGroup studyGroup) {
        return studyMemberRepository.existsByMemberAndStudyGroup(member, studyGroup);
    }

    /**
     * 특정 회원 ID로 스터디 그룹 ID 목록을 찾습니다.
     *
     * @param memberId 회원 ID
     * @return List<Long> 스터디 그룹 ID 목록
     */
    public List<Long> findStudyGroupIdsByMemberId(Long memberId) {
        return studyMemberRepository.findStudyGroupIdsByMemberId(memberId);
    }

    /**
     * 특정 스터디 그룹과 상태로 스터디 멤버 목록을 찾습니다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     * @param status 스터디 그룹 상태
     * @return List<StudyMember> 스터디 멤버 목록
     */
    public List<StudyMember> findAllByStudyGroupAndStatus(StudyGroup studyGroup, StudyGroupStatus status) {
        return studyMemberRepository.findAllByStudyGroupAndStatus(studyGroup, status);
    }

    /**
     * 스터디 멤버를 저장합니다.
     *
     * @param studyMember 스터디 멤버 엔티티
     */
    public void saveStudyMember(StudyMember studyMember) {
        studyMemberRepository.save(studyMember);
    }

    /**
     * 스터디 멤버를 삭제합니다.
     *
     * @param studyMember 스터디 멤버 엔티티
     */
    public void deleteStudyMember(StudyMember studyMember) {
        studyMemberRepository.delete(studyMember);
    }

    /**
     * 특정 ID로 스터디 멤버를 찾습니다.
     *
     * @param studyMemberId 스터디 멤버 ID
     * @return StudyMember 스터디 멤버 엔티티
     */
    public StudyMember findStudyMemberById(Long studyMemberId) {
        return studyMemberRepository.findById(studyMemberId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 회원을 찾을 수 없습니다."));
    }

    /**
     * 특정 스터디 그룹 ID로 모든 스터디 멤버를 삭제합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     */
    public void deleteByStudyGroupId(Long studyGroupId) {
        studyMemberRepository.deleteByStudyGroupId(studyGroupId);
    }

    /**
     * 회원의 스터디 그룹 가입 요청을 유효성 검사합니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     */
    public void validateMemberJoinRequest(Member member, StudyGroup studyGroup) {
        if (existsByMemberAndStudyGroup(member, studyGroup)) {
            throw new IllegalStateException("이미 스터디 그룹에 지원했습니다.");
        }
    }

    /**
     * 스터디 그룹 리더의 권한을 유효성 검사합니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     */
    public void validateLeaderPermission(Member member, StudyGroup studyGroup) {
        if (!studyGroup.getStudyLeaderId().equals(member.getId())) {
            throw new IllegalStateException("스터디 그룹 리더만 참여 요청을 수락할 수 있습니다.");
        }
    }
}
