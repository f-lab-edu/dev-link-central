package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.StudyGroup;
import dev.linkcentral.database.entity.StudyGroupStatus;
import dev.linkcentral.database.entity.StudyMember;
import dev.linkcentral.database.repository.StudyGroupRepository;
import dev.linkcentral.database.repository.StudyMemberRepository;
import dev.linkcentral.presentation.dto.request.studygroup.StudyMemberRequest;
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

    @Transactional(readOnly = true)
    public List<Long> getStudyGroupIdsByMemberId(Long memberId) {
        return studyMemberRepository.findStudyGroupIdsByMemberId(memberId);
    }

    @Transactional
    public void requestJoinStudyGroup(Long studyGroupId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        if (studyMemberRepository.existsByMemberAndStudyGroup(currentMember, studyGroup)) {
            throw new IllegalStateException("이미 스터디 그룹에 지원했습니다.");
        }

        StudyMember studyMember = StudyMember.builder()
                .member(currentMember)
                .studyGroup(studyGroup)
                .status(StudyGroupStatus.REQUESTED)
                .build();

        studyMemberRepository.save(studyMember);
    }

    @Transactional(readOnly = true)
    public List<StudyMemberRequest> listJoinRequestsForStudyGroup(Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        List<StudyMember> studyMembers = studyMemberRepository.findAllByStudyGroupAndStatus(
                studyGroup, StudyGroupStatus.REQUESTED);

        return studyMembers.stream()
                .filter(sm -> !sm.getMember().getId().equals(studyGroup.getStudyLeaderId()))
                .map(sm -> new StudyMemberRequest(
                        sm.getId(),
                        sm.getMember().getName(),
                        sm.getStudyGroup().getGroupName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptJoinRequest(Long studyGroupId, Long requestId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        if (!studyGroup.getStudyLeaderId().equals(currentMember.getId())) {
            throw new IllegalStateException("스터디 그룹 리더만 참여 요청을 수락할 수 있습니다.");
        }

        StudyMember joinRequest = studyMemberRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("참가 요청을 찾을 수 없습니다."));

        joinRequest.updateStudyGroupStatus(StudyGroupStatus.ACCEPTED);
        studyMemberRepository.save(joinRequest);
    }

    @Transactional
    public void rejectJoinRequest(Long studyGroupId, Long requestId) {
        Member currentMember = memberService.getCurrentMember();
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        if (!studyGroup.getStudyLeaderId().equals(currentMember.getId())) {
            throw new IllegalStateException("스터디 그룹 리더만 참가 요청을 거부할 수 있습니다.");
        }

        StudyMember joinRequest = studyMemberRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("참가 요청을 찾을 수 없습니다."));

        studyMemberRepository.delete(joinRequest);
    }

    @Transactional
    public void removeAllMembersByStudyGroupId(Long studyGroupId) {
        studyMemberRepository.deleteByStudyGroupId(studyGroupId);
    }
}
