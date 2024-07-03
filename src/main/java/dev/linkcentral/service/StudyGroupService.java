package dev.linkcentral.service;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.database.repository.member.MemberRepository;
import dev.linkcentral.database.repository.studygroup.StudyGroupRepository;
import dev.linkcentral.database.repository.studygroup.StudyMemberRepository;
import dev.linkcentral.service.dto.studygroup.*;
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

    /**
     * 특정 ID로 스터디 그룹을 가져옵니다.
     *
     * @param id 스터디 그룹 ID
     * @return StudyGroup 스터디 그룹 엔티티
     */
    @Transactional(readOnly = true)
    public StudyGroup getStudyGroupById(Long id) {
        return studyGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 새로운 스터디 그룹을 생성합니다.
     *
     * @param groupName 스터디 그룹 이름
     * @param studyTopic 스터디 주제
     * @param leaderId 리더 ID
     * @return StudyGroup 생성된 스터디 그룹 엔티티
     */
    @Transactional
    public StudyGroup createStudyGroup(String groupName, String studyTopic, Long leaderId) {
        Member leader = findMemberById(leaderId);

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

    /**
     * 리더 ID로 스터디 그룹을 찾습니다.
     *
     * @param leaderId 리더 ID
     * @return StudyGroup 스터디 그룹 엔티티
     */
    @Transactional(readOnly = true)
    public StudyGroup findStudyGroupByLeaderId(Long leaderId) {
        return studyGroupRepository.findByStudyLeaderId(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 스터디 그룹을 삭제합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param memberId 회원 ID
     */
    @Transactional
    public void deleteStudyGroup(Long studyGroupId, Long memberId) {
        StudyGroup studyGroup = getStudyGroupById(studyGroupId);

        if (!studyGroup.getStudyLeaderId().equals(memberId)) {
            throw new IllegalStateException("스터디 그룹 리더만 스터디 그룹을 삭제할 수 있습니다.");
        }
        studyGroupRepository.delete(studyGroup);
    }

    /**
     * 스터디 그룹을 떠납니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param memberId 회원 ID
     * @return boolean 떠남 여부
     */
    @Transactional
    public boolean leaveStudyGroupAsMember(Long studyGroupId, Long memberId) {
        StudyGroup studyGroup = getStudyGroupById(studyGroupId);
        Member member = findMemberById(memberId);

        StudyMember studyMember = findStudyMemberByMemberAndStudyGroup(member, studyGroup);
        if (studyMember != null) {
            studyMemberRepository.delete(studyMember);
            return true;
        }
        return false;
    }

    /**
     * 사용자가 스터디 그룹을 가지고 있는지 확인합니다.
     *
     * @param studyLeaderId 스터디 리더 ID
     * @return boolean 스터디 그룹 보유 여부
     */
    @Transactional(readOnly = true)
    public boolean checkIfUserHasStudyGroup(Long studyLeaderId) {
        return studyGroupRepository.existsByStudyLeaderId(studyLeaderId);
    }

    /**
     * 특정 회원 ID로 승인된 스터디 그룹 목록을 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return List<StudyGroup> 스터디 그룹 목록
     */
    @Transactional(readOnly = true)
    public List<StudyGroup> findAcceptedStudyGroupsByMemberId(Long memberId) {
        return studyMemberRepository.findAllByMemberIdAndStatus(memberId, StudyGroupStatus.ACCEPTED)
                .stream()
                .map(StudyMember::getStudyGroup)
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자 ID로 승인된 스터디 그룹 목록을 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return List<AcceptedStudyGroupDetailsDTO> 승인된 스터디 그룹 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<AcceptedStudyGroupDetailsDTO> getAcceptedGroupsByUser(Long userId) {
        List<StudyGroup> groups = findAcceptedStudyGroupsByMemberId(userId);
        return groups.stream()
                .map(this::toAcceptedStudyGroupDetailsDTO)
                .collect(Collectors.toList());
    }

    private AcceptedStudyGroupDetailsDTO toAcceptedStudyGroupDetailsDTO(StudyGroup studyGroup) {
        List<StudyGroupUserDTO> members = studyMemberRepository.findAllByStudyGroupIdAndStatus(
                        studyGroup.getId(), StudyGroupStatus.ACCEPTED).stream()
                .map(studyMember -> new StudyGroupUserDTO(
                        studyMember.getMember().getId(),
                        studyMember.getMember().getName()))
                .collect(Collectors.toList());
        return studyGroupMapper.toAcceptedStudyGroupDetailsDTO(studyGroup, members);
    }

    /**
     * 특정 사용자 ID로 스터디 그룹과 구성원 정보를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return List<StudyGroupMembersDetailDTO> 스터디 그룹 및 구성원 정보 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<StudyGroupMembersDetailDTO> getStudyGroupsAndMembers(Long userId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findStudyGroupsByUserId(userId);
        List<StudyGroupMembersDetailDTO> result = new ArrayList<>();

        for (StudyGroup group : studyGroups) {
            List<StudyMember> acceptedMembers = studyMemberRepository.findAllByStudyGroupAndStatus(group, StudyGroupStatus.ACCEPTED);

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

    /**
     * 스터디 그룹의 회원을 추방합니다.
     *
     * @param groupId 스터디 그룹 ID
     * @param memberId 회원 ID
     * @param loggedInUserId 로그인된 사용자 ID
     */
    @Transactional
    public void expelMember(Long groupId, Long memberId, Long loggedInUserId) {
        StudyGroup studyGroup = getStudyGroupById(groupId);

        if (!studyGroup.getStudyLeaderId().equals(loggedInUserId)) {
            throw new AccessDeniedException("그룹 리더만 구성원을 추방할 수 있습니다.");
        }
        StudyMember studyMember = findStudyMemberById(studyGroup, memberId);
        studyMemberRepository.delete(studyMember);
    }

    /**
     * 사용자가 스터디 그룹을 가지고 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return StudyGroupExistsDTO 스터디 그룹 존재 여부 DTO
     */
    @Transactional(readOnly = true)
    public StudyGroupExistsDTO userHasStudyGroup(Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findByStudyLeaderId(userId).orElse(null);
        boolean exists = studyGroup != null;
        Long groupId = null;

        if (exists) {
            groupId = studyGroup.getId();
        }
        return studyGroupMapper.toStudyGroupExistsDTO(exists, groupId);
    }

    /**
     * 특정 ID로 회원을 찾습니다.
     *
     * @param memberId 회원 ID
     * @return Member 회원 엔티티
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    /**
     * 특정 스터디 그룹과 회원 ID로 스터디 멤버를 찾습니다.
     *
     * @param studyGroup 스터디 그룹 엔티티
     * @param memberId 회원 ID
     * @return StudyMember 스터디 멤버 엔티티
     */
    private StudyMember findStudyMemberById(StudyGroup studyGroup, Long memberId) {
        return studyMemberRepository.findByStudyGroupAndMemberId(studyGroup, memberId)
                .orElseThrow(() -> new EntityNotFoundException("이 스터디 그룹에 회원을 찾을 수 없습니다."));
    }

    /**
     * 특정 회원과 스터디 그룹으로 스터디 멤버를 찾습니다.
     *
     * @param member 회원 엔티티
     * @param studyGroup 스터디 그룹 엔티티
     * @return StudyMember 스터디 멤버 엔티티
     */
    private StudyMember findStudyMemberByMemberAndStudyGroup(Member member, StudyGroup studyGroup) {
        return studyMemberRepository.findByMemberAndStudyGroup(member, studyGroup)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹 회원을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Member> findMembersByUserId(Long userId) {
        List<StudyGroup> userGroups = studyGroupRepository.findByMemberId(userId);
        return userGroups.stream()
                .flatMap(group -> studyMemberRepository.findByStudyGroupIdAndStatus(group.getId(), StudyGroupStatus.ACCEPTED).stream())
                .map(StudyMember::getMember)
                .distinct()
                .collect(Collectors.toList());
    }
}
