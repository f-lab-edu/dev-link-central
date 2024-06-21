package dev.linkcentral.service;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.entity.StudyGroup;
import dev.linkcentral.database.entity.StudyGroupStatus;
import dev.linkcentral.database.entity.StudyMember;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.database.repository.StudyGroupRepository;
import dev.linkcentral.database.repository.StudyMemberRepository;
import dev.linkcentral.service.dto.studygroup.StudyGroupExistsDTO;
import dev.linkcentral.service.dto.studygroup.StudyGroupMemberBasicInfoDTO;
import dev.linkcentral.service.dto.studygroup.AcceptedStudyGroupDetailsDTO;
import dev.linkcentral.service.dto.studygroup.StudyGroupMembersDetailDTO;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupMapper studyGroupMapper;
    private final MemberRepository memberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional(readOnly = true)
    public StudyGroup getStudyGroupById(Long id) {
        return studyGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    @Transactional
    public StudyGroup createStudyGroup(String groupName, String studyTopic, Long leaderId) {
        Member leader = memberRepository.findById(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));

        StudyGroup studyGroup = StudyGroup.builder()
                .groupName(groupName)
                .studyTopic(studyTopic)
                .studyLeaderId(leaderId)
                .isCreated(true)
                .build();

        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        StudyMember studyMember = StudyMember.builder()
                .member(leader)
                .studyGroup(savedStudyGroup)
                .status(StudyGroupStatus.ACCEPTED)
                .build();

        studyMemberRepository.save(studyMember);
        return savedStudyGroup;
    }

    @Transactional(readOnly = true)
    public StudyGroup findStudyGroupByLeaderId(Long leaderId) {
        return studyGroupRepository.findByStudyLeaderId(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteStudyGroup(Long studyGroupId, Long memberId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        if (!studyGroup.getStudyLeaderId().equals(memberId)) {
            throw new IllegalStateException("스터디 그룹 리더만 스터디 그룹을 삭제할 수 있습니다.");
        }

        studyGroupRepository.delete(studyGroup);
    }

    @Transactional
    public boolean leaveStudyGroupAsMember(Long studyGroupId, Long memberId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾을 수 없습니다."));

        StudyMember studyMember = studyMemberRepository.findByMemberAndStudyGroup(member, studyGroup)
                .orElse(null);

        if (studyMember != null) {
            studyMemberRepository.delete(studyMember);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkIfUserHasStudyGroup(Long studyLeaderId) {
        return studyGroupRepository.existsByStudyLeaderId(studyLeaderId);
    }

    @Transactional(readOnly = true)
    public List<StudyGroup> findAcceptedStudyGroupsByMemberId(Long memberId) {
        return studyMemberRepository.findAllByMemberIdAndStatus(memberId, StudyGroupStatus.ACCEPTED)
                .stream()
                .map(StudyMember::getStudyGroup)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AcceptedStudyGroupDetailsDTO> getAcceptedGroupsByUser(Long userId) {
        List<StudyGroup> groups = findAcceptedStudyGroupsByMemberId(userId);
        return groups.stream()
                .map(group -> new AcceptedStudyGroupDetailsDTO(
                        group.getId(),
                        group.getGroupName(),
                        group.getStudyTopic()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudyGroupMembersDetailDTO> getStudyGroupsAndMembers(Long userId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findStudyGroupsByUserId(userId);
        List<StudyGroupMembersDetailDTO> result = new ArrayList<>();

        for (StudyGroup group : studyGroups) {
            List<StudyMember> acceptedMembers = studyMemberRepository
                    .findAllByStudyGroupAndStatus(group, StudyGroupStatus.ACCEPTED);

            if (!acceptedMembers.isEmpty()) {
                List<StudyGroupMemberBasicInfoDTO> memberDtos = acceptedMembers.stream()
                        .map(member -> new StudyGroupMemberBasicInfoDTO(
                                member.getMember().getId(),
                                member.getMember().getName()))
                        .collect(Collectors.toList());

                result.add(new StudyGroupMembersDetailDTO(
                        group.getId(),
                        group.getStudyLeaderId(),
                        group.getGroupName(),
                        memberDtos));
            }
        }
        return result;
    }

    @Transactional
    public void expelMember(Long groupId, Long memberId, Long loggedInUserId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));

        if (!studyGroup.getStudyLeaderId().equals(loggedInUserId)) {
            throw new AccessDeniedException("그룹 리더만 구성원을 추방할 수 있습니다.");
        }

        StudyMember studyMember = studyMemberRepository.findByStudyGroupAndMemberId(studyGroup, memberId)
                .orElseThrow(() -> new EntityNotFoundException("이 스터디 그룹에 회원을 찾을 수 없습니다."));

        studyMemberRepository.delete(studyMember);
    }

    @Transactional(readOnly = true)
    public StudyGroupExistsDTO userHasStudyGroup(Long userId) {
        boolean existsByStudyLeaderId = studyGroupRepository.existsByStudyLeaderId(userId);
        return studyGroupMapper.toStudyGroupExistsDTO(existsByStudyLeaderId);
    }
}
