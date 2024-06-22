package dev.linkcentral.service.helper;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.database.repository.studygroup.StudyGroupRepository;
import dev.linkcentral.database.repository.studygroup.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudyGroupServiceHelper {

    private final MemberRepository memberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 특정 ID로 회원을 찾습니다.
     *
     * @param memberId 회원 ID
     * @return Member 회원 엔티티
     */
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

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
     * 특정 스터디 그룹과 회원 ID로 스터디 멤버를 찾습니다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     * @param memberId 회원 ID
     * @return StudyMember 스터디 멤버 엔티티
     */
    public StudyMember findStudyMemberById(StudyGroup studyGroup, Long memberId) {
        return studyMemberRepository.findByStudyGroupAndMemberId(studyGroup, memberId)
                .orElseThrow(() -> new EntityNotFoundException("이 스터디 그룹에 회원을 찾을 수 없습니다."));
    }

    /**
     * 스터디 그룹을 저장합니다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     * @return StudyGroup 저장된 스터디 그룹 엔티티
     */
    public StudyGroup saveStudyGroup(StudyGroup studyGroup) {
        return studyGroupRepository.save(studyGroup);
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
     * 스터디 그룹을 삭제합니다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     */
    public void deleteStudyGroup(StudyGroup studyGroup) {
        studyGroupRepository.delete(studyGroup);
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
     * 특정 스터디 리더 ID로 스터디 그룹 존재 여부를 확인합니다.
     *
     * @param studyLeaderId 스터디 리더 ID
     * @return boolean 스터디 그룹 존재 여부
     */
    public boolean existsByStudyLeaderId(Long studyLeaderId) {
        return studyGroupRepository.existsByStudyLeaderId(studyLeaderId);
    }

    /**
     * 특정 회원 ID로 승인된 스터디 그룹 목록을 찾습니다.
     *
     * @param memberId 회원 ID
     * @return List<StudyGroup> 승인된 스터디 그룹 목록
     */
    public List<StudyGroup> findAcceptedStudyGroupsByMemberId(Long memberId) {
        return studyMemberRepository.findAllByMemberIdAndStatus(memberId, StudyGroupStatus.ACCEPTED)
                .stream()
                .map(StudyMember::getStudyGroup)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자 ID로 스터디 그룹 목록을 찾습니다.
     *
     * @param userId 사용자 ID
     * @return List<StudyGroup> 스터디 그룹 목록
     */
    public List<StudyGroup> findStudyGroupsByUserId(Long userId) {
        return studyGroupRepository.findStudyGroupsByUserId(userId);
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
     * 특정 리더 ID로 스터디 그룹을 찾습니다.
     *
     * @param leaderId 리더 ID
     * @return StudyGroup 스터디 그룹 엔티티
     */
    public StudyGroup findStudyGroupByLeaderId(Long leaderId) {
        return studyGroupRepository.findByStudyLeaderId(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 특정 회원과 스터디 그룹으로 스터디 멤버를 찾습니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     * @return StudyMember 스터디 멤버 엔티티
     */
    public StudyMember findStudyMemberByMemberAndStudyGroup(Member member, StudyGroup studyGroup) {
        return studyMemberRepository.findByMemberAndStudyGroup(member, studyGroup)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹 회원을 찾을 수 없습니다."));
    }
}
