package dev.linkcentral.service;

import dev.member.entity.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.member.repository.MemberRepository;
import dev.linkcentral.database.repository.studygroup.StudyGroupRepository;
import dev.linkcentral.database.repository.studygroup.StudyMemberRepository;
import dev.linkcentral.service.dto.studygroup.*;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final MemberRepository memberRepository;
    private final StudyGroupMapper studyGroupMapper;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 주어진 ID로 스터디 그룹을 찾습니다.
     *
     * @param id 스터디 그룹의 ID
     * @return 스터디 그룹 객체
     */

    @Transactional(readOnly = true)
    public StudyGroup getStudyGroupById(Long id) {
        return studyGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 새로운 스터디 그룹을 생성합니다.
     *
     * @param groupName 그룹 이름
     * @param studyTopic 스터디 주제
     * @param leaderId 리더의 ID
     * @return 생성된 스터디 그룹 객체
     */
    @Transactional
    public StudyGroup createStudyGroup(String groupName, String studyTopic, Long leaderId) {
        Member leader = findMemberById(leaderId);

        StudyGroup studyGroup = StudyGroup.of(groupName, studyTopic, leaderId);
        StudyGroup savedStudyGroup = studyGroupRepository.save(studyGroup);

        StudyMember studyMember = StudyMember.of(leader, savedStudyGroup, StudyGroupStatus.ACCEPTED);
        studyMemberRepository.save(studyMember);

        return savedStudyGroup;
    }

    /**
     * 리더 ID로 스터디 그룹을 찾습니다.
     *
     * @param leaderId 리더의 ID
     * @return 스터디 그룹 객체
     */
    @Transactional(readOnly = true)
    public StudyGroup findStudyGroupByLeaderId(Long leaderId) {
        return studyGroupRepository.findByStudyLeaderId(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹을 찾을 수 없습니다."));
    }

    /**
     * 주어진 스터디 그룹을 삭제합니다.
     *
     * @param studyGroupId 스터디 그룹의 ID
     * @param memberId 멤버의 ID
     */
    @Transactional
    public void deleteStudyGroup(Long studyGroupId, Long memberId) {
        StudyGroup studyGroup = getStudyGroupById(studyGroupId);
        validateStudyGroupLeader(studyGroup, memberId);
        studyGroupRepository.delete(studyGroup);
    }

    /**
     * 스터디 그룹 리더를 확인합니다.
     *
     * @param studyGroup 스터디 그룹 객체
     * @param memberId 멤버의 ID
     */
    private void validateStudyGroupLeader(StudyGroup studyGroup, Long memberId) {
        if (!studyGroup.getStudyLeaderId().equals(memberId)) {
            throw new IllegalStateException("스터디 그룹 리더만 스터디 그룹을 삭제할 수 있습니다.");
        }
    }

    /**
     * 멤버가 스터디 그룹을 떠납니다.
     *
     * @param studyGroupId 스터디 그룹의 ID
     * @param memberId 멤버의 ID
     * @return 스터디 그룹을 떠난 후의 상태
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
     * @param studyLeaderId 리더의 ID
     * @return 스터디 그룹 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean checkIfUserHasStudyGroup(Long studyLeaderId) {
        return studyGroupRepository.existsByStudyLeaderId(studyLeaderId);
    }

    /**
     * 멤버 ID로 승인된 스터디 그룹을 찾습니다.
     *
     * @param memberId 멤버의 ID
     * @return 승인된 스터디 그룹 목록
     */
    @Transactional(readOnly = true)
    public List<StudyGroup> findAcceptedStudyGroupsByMemberId(Long memberId) {
        return studyMemberRepository.findAllByMemberIdAndStatus(memberId, StudyGroupStatus.ACCEPTED)
                .stream()
                .map(StudyMember::getStudyGroup)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 승인된 스터디 그룹을 가져옵니다.
     *
     * @param userId 사용자의 ID
     * @return 승인된 스터디 그룹 세부 정보 목록
     */
    @Transactional(readOnly = true)
    public List<AcceptedStudyGroupDetailsDTO> getAcceptedGroupsByUser(Long userId) {
        List<StudyGroup> groups = findAcceptedStudyGroupsByMemberId(userId);
        return groups.stream()
                .map(this::toAcceptedStudyGroupDetailsDTO)
                .collect(Collectors.toList());
    }

    /**
     * 승인된 스터디 그룹 세부 정보를 변환합니다.
     *
     * @param studyGroup 스터디 그룹 객체
     * @return 승인된 스터디 그룹 세부 정보 DTO
     */
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
     * 사용자의 스터디 그룹 및 멤버 정보를 가져옵니다.
     *
     * @param userId 사용자의 ID
     * @return 스터디 그룹 멤버 세부 정보 목록
     */
    @Transactional(readOnly = true)
    public List<StudyGroupMembersDetailDTO> getStudyGroupsAndMembers(Long userId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findStudyGroupsByUserId(userId);
        return studyGroups.stream()
                .map(studyGroupMapper::toStudyGroupMembersDetailDTO)
                .collect(Collectors.toList());
    }

    /**
     * 스터디 그룹에서 멤버를 추방합니다.
     *
     * @param groupId 스터디 그룹의 ID
     * @param memberId 멤버의 ID
     * @param loggedInUserId 로그인한 사용자의 ID
     */
    @Transactional
    public void expelMember(Long groupId, Long memberId, Long loggedInUserId) {
        StudyGroup studyGroup = getStudyGroupById(groupId);
        validateStudyGroupLeader(studyGroup, loggedInUserId);
        StudyMember studyMember = findStudyMemberById(studyGroup, memberId);
        studyMemberRepository.delete(studyMember);
    }

    /**
     * 사용자가 스터디 그룹을 가지고 있는지 확인하고 해당 정보를 반환합니다.
     *
     * @param userId 사용자의 ID
     * @return 스터디 그룹 존재 정보 DTO
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
     * 주어진 ID로 멤버를 찾습니다.
     *
     * @param memberId 멤버의 ID
     * @return 멤버 객체
     */
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("ID로 멤버를 찾을 수 없습니다."));
    }

    /**
     * 스터디 그룹과 멤버 ID로 스터디 멤버를 찾습니다.
     *
     * @param studyGroup 스터디 그룹 객체
     * @param memberId 멤버의 ID
     * @return 스터디 멤버 객체
     */
    private StudyMember findStudyMemberById(StudyGroup studyGroup, Long memberId) {
        return studyMemberRepository.findByStudyGroupAndMemberId(studyGroup, memberId)
                .orElseThrow(() -> new EntityNotFoundException("이 스터디 그룹에 회원을 찾을 수 없습니다."));
    }

    /**
     * 멤버와 스터디 그룹으로 스터디 멤버를 찾습니다.
     *
     * @param member 멤버 객체
     * @param studyGroup 스터디 그룹 객체
     * @return 스터디 멤버 객체
     */
    private StudyMember findStudyMemberByMemberAndStudyGroup(Member member, StudyGroup studyGroup) {
        return studyMemberRepository.findByMemberAndStudyGroup(member, studyGroup)
                .orElseThrow(() -> new EntityNotFoundException("스터디 그룹 회원을 찾을 수 없습니다."));
    }

    /**
     * 주어진 사용자 ID로 스터디 그룹의 멤버를 찾습니다.
     *
     * @param userId 사용자 ID
     * @return 멤버 목록
     */
    @Transactional(readOnly = true)
    public List<Member> findMembersByUserId(Long userId) {
        List<StudyGroup> userGroups = studyGroupRepository.findByMemberId(userId);
        return userGroups.stream()
                .flatMap(group -> studyMemberRepository.findByStudyGroupIdAndStatus(
                        group.getId(), StudyGroupStatus.ACCEPTED).stream())
                .map(StudyMember::getMember)
                .distinct()
                .collect(Collectors.toList());
    }
}
