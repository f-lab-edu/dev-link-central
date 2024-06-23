package dev.linkcentral.service;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.database.repository.studygroup.StudyGroupRepository;
import dev.linkcentral.database.repository.studygroup.StudyMemberRepository;
import dev.linkcentral.service.dto.studygroup.StudyGroupJoinRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final MemberService memberService;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 특정 회원 ID로 스터디 그룹 ID 목록을 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return 스터디 그룹 ID 목록
     */
    @Transactional(readOnly = true)
    public List<Long> getStudyGroupIdsByMemberId(Long memberId) {
        return studyMemberRepository.findStudyGroupIdsByMemberId(memberId);
    }

    /**
     * 스터디 그룹에 가입 요청을 보냅니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     */
    @Transactional
    public void requestJoinStudyGroup(Long studyGroupId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = findStudyGroupById(studyGroupId);

        validateMemberJoinRequest(currentMember, studyGroup);

        StudyMember studyMember = StudyMember.builder()
                .member(currentMember)
                .studyGroup(studyGroup)
                .status(StudyGroupStatus.REQUESTED)
                .build();

        studyMemberRepository.save(studyMember);
    }

    /**
     * 특정 스터디 그룹의 가입 요청 목록을 가져옵니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return 스터디 그룹 가입 요청 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<StudyGroupJoinRequestDTO> listJoinRequestsForStudyGroup(Long studyGroupId) {
        StudyGroup studyGroup = findStudyGroupById(studyGroupId);

        List<StudyMember> studyMembers = studyMemberRepository.findAllByStudyGroupAndStatus(
                studyGroup, StudyGroupStatus.REQUESTED);

        return studyMembers.stream()
                .filter(sm -> !sm.getMember().getId().equals(studyGroup.getStudyLeaderId()))
                .map(sm -> new StudyGroupJoinRequestDTO(
                        sm.getId(),
                        sm.getMember().getName(),
                        sm.getStudyGroup().getGroupName()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 스터디 그룹의 가입 요청을 수락합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param requestId 요청 ID
     */
    @Transactional
    public void acceptJoinRequest(Long studyGroupId, Long requestId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = findStudyGroupById(studyGroupId);

        validateLeaderPermission(currentMember, studyGroup);

        StudyMember joinRequest = findStudyMemberById(requestId);

        joinRequest.updateStudyGroupStatus(StudyGroupStatus.ACCEPTED);
        studyMemberRepository.save(joinRequest);
    }

    /**
     * 특정 스터디 그룹의 가입 요청을 거부합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param requestId 요청 ID
     */
    @Transactional
    public void rejectJoinRequest(Long studyGroupId, Long requestId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = findStudyGroupById(studyGroupId);

        validateLeaderPermission(currentMember, studyGroup);

        StudyMember joinRequest = findStudyMemberById(requestId);
        studyMemberRepository.delete(joinRequest);
    }

    /**
     * 특정 스터디 그룹의 모든 멤버를 제거합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     */
    @Transactional
    public void removeAllMembersByStudyGroupId(Long studyGroupId) {
        studyMemberRepository.deleteByStudyGroupId(studyGroupId);
    }

    /**
     * 특정 ID로 스터디 그룹을 찾습니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return StudyGroup 스터디 그룹 엔티티
     */
    private StudyGroup findStudyGroupById(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 특정 ID로 스터디 멤버를 찾습니다.
     *
     * @param studyMemberId 스터디 멤버 ID
     * @return StudyMember 스터디 멤버 엔티티
     */
    private StudyMember findStudyMemberById(Long studyMemberId) {
        return studyMemberRepository.findById(studyMemberId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 회원을 찾을 수 없습니다."));
    }

    /**
     * 회원의 스터디 그룹 가입 요청을 유효성 검사합니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     */
    private void validateMemberJoinRequest(Member member, StudyGroup studyGroup) {
        if (studyMemberRepository.existsByMemberAndStudyGroup(member, studyGroup)) {
            throw new IllegalStateException("이미 스터디 그룹에 지원했습니다.");
        }
    }

    /**
     * 스터디 그룹 리더의 권한을 유효성 검사합니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     */
    private void validateLeaderPermission(Member member, StudyGroup studyGroup) {
        if (!studyGroup.getStudyLeaderId().equals(member.getId())) {
            throw new IllegalStateException("스터디 그룹 리더만 참여 요청을 수락할 수 있습니다.");
        }
    }
}
