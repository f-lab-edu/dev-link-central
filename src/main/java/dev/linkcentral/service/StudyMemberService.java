package dev.linkcentral.service;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.service.dto.studygroup.StudyGroupJoinRequestDTO;
import dev.linkcentral.service.helper.StudyMemberServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final MemberService memberService;
    private final StudyMemberServiceHelper studyMemberServiceHelper;

    /**
     * 특정 회원 ID로 스터디 그룹 ID 목록을 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return 스터디 그룹 ID 목록
     */
    @Transactional(readOnly = true)
    public List<Long> getStudyGroupIdsByMemberId(Long memberId) {
        return studyMemberServiceHelper.findStudyGroupIdsByMemberId(memberId);
    }

    /**
     * 스터디 그룹에 가입 요청을 보냅니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     */
    @Transactional
    public void requestJoinStudyGroup(Long studyGroupId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyMemberServiceHelper.findStudyGroupById(studyGroupId);

        studyMemberServiceHelper.validateMemberJoinRequest(currentMember, studyGroup);

        StudyMember studyMember = StudyMember.builder()
                .member(currentMember)
                .studyGroup(studyGroup)
                .status(StudyGroupStatus.REQUESTED)
                .build();

        studyMemberServiceHelper.saveStudyMember(studyMember);
    }

    /**
     * 특정 스터디 그룹의 가입 요청 목록을 가져옵니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @return 스터디 그룹 가입 요청 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<StudyGroupJoinRequestDTO> listJoinRequestsForStudyGroup(Long studyGroupId) {
        StudyGroup studyGroup = studyMemberServiceHelper.findStudyGroupById(studyGroupId);

        List<StudyMember> studyMembers = studyMemberServiceHelper.findAllByStudyGroupAndStatus(
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
        StudyGroup studyGroup = studyMemberServiceHelper.findStudyGroupById(studyGroupId);

        studyMemberServiceHelper.validateLeaderPermission(currentMember, studyGroup);

        StudyMember joinRequest = studyMemberServiceHelper.findStudyMemberById(requestId);

        joinRequest.updateStudyGroupStatus(StudyGroupStatus.ACCEPTED);
        studyMemberServiceHelper.saveStudyMember(joinRequest);
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
        StudyGroup studyGroup = studyMemberServiceHelper.findStudyGroupById(studyGroupId);

        studyMemberServiceHelper.validateLeaderPermission(currentMember, studyGroup);

        StudyMember joinRequest = studyMemberServiceHelper.findStudyMemberById(requestId);
        studyMemberServiceHelper.deleteStudyMember(joinRequest);
    }

    /**
     * 특정 스터디 그룹의 모든 멤버를 제거합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     */
    @Transactional
    public void removeAllMembersByStudyGroupId(Long studyGroupId) {
        studyMemberServiceHelper.deleteByStudyGroupId(studyGroupId);
    }
}
