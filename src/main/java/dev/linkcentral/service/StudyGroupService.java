package dev.linkcentral.service;

import dev.linkcentral.database.entity.member.Member;
import dev.linkcentral.database.entity.studygroup.StudyGroup;
import dev.linkcentral.database.entity.studygroup.StudyGroupStatus;
import dev.linkcentral.database.entity.studygroup.StudyMember;
import dev.linkcentral.service.dto.studygroup.AcceptedStudyGroupDetailsDTO;
import dev.linkcentral.service.dto.studygroup.StudyGroupExistsDTO;
import dev.linkcentral.service.dto.studygroup.StudyGroupMemberBasicInfoDTO;
import dev.linkcentral.service.dto.studygroup.StudyGroupMembersDetailDTO;
import dev.linkcentral.service.helper.StudyGroupServiceHelper;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupMapper studyGroupMapper;
    private final StudyGroupServiceHelper studyGroupServiceHelper;

    /**
     * 특정 ID로 스터디 그룹을 가져옵니다.
     *
     * @param id 스터디 그룹 ID
     * @return StudyGroup 스터디 그룹 엔티티
     */
    @Transactional(readOnly = true)
    public StudyGroup getStudyGroupById(Long id) {
        return studyGroupServiceHelper.findStudyGroupById(id);
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
        Member leader = studyGroupServiceHelper.findMemberById(leaderId);

        StudyGroup studyGroup = StudyGroup.builder()
                .groupName(groupName)
                .studyTopic(studyTopic)
                .studyLeaderId(leaderId)
                .isCreated(true)
                .build();

        StudyGroup savedStudyGroup = studyGroupServiceHelper.saveStudyGroup(studyGroup);

        StudyMember studyMember = StudyMember.builder()
                .member(leader)
                .studyGroup(savedStudyGroup)
                .status(StudyGroupStatus.ACCEPTED)
                .build();

        studyGroupServiceHelper.saveStudyMember(studyMember);
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
        return studyGroupServiceHelper.findStudyGroupByLeaderId(leaderId);
    }

    /**
     * 스터디 그룹을 삭제합니다.
     *
     * @param studyGroupId 스터디 그룹 ID
     * @param memberId 회원 ID
     */
    @Transactional
    public void deleteStudyGroup(Long studyGroupId, Long memberId) {
        StudyGroup studyGroup = studyGroupServiceHelper.findStudyGroupById(studyGroupId);

        if (!studyGroup.getStudyLeaderId().equals(memberId)) {
            throw new IllegalStateException("스터디 그룹 리더만 스터디 그룹을 삭제할 수 있습니다.");
        }
        studyGroupServiceHelper.deleteStudyGroup(studyGroup);
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
        StudyGroup studyGroup = studyGroupServiceHelper.findStudyGroupById(studyGroupId);
        Member member = studyGroupServiceHelper.findMemberById(memberId);

        StudyMember studyMember = studyGroupServiceHelper.findStudyMemberByMemberAndStudyGroup(member, studyGroup);
        if (studyMember != null) {
            studyGroupServiceHelper.deleteStudyMember(studyMember);
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
        return studyGroupServiceHelper.existsByStudyLeaderId(studyLeaderId);
    }

    /**
     * 특정 회원 ID로 승인된 스터디 그룹 목록을 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return List<StudyGroup> 스터디 그룹 목록
     */
    @Transactional(readOnly = true)
    public List<StudyGroup> findAcceptedStudyGroupsByMemberId(Long memberId) {
        return studyGroupServiceHelper.findAcceptedStudyGroupsByMemberId(memberId);
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
                .map(group -> new AcceptedStudyGroupDetailsDTO(
                        group.getId(),
                        group.getGroupName(),
                        group.getStudyTopic()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자 ID로 스터디 그룹과 구성원 정보를 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return List<StudyGroupMembersDetailDTO> 스터디 그룹 및 구성원 정보 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<StudyGroupMembersDetailDTO> getStudyGroupsAndMembers(Long userId) {
        List<StudyGroup> studyGroups = studyGroupServiceHelper.findStudyGroupsByUserId(userId);
        List<StudyGroupMembersDetailDTO> result = new ArrayList<>();

        for (StudyGroup group : studyGroups) {
            List<StudyMember> acceptedMembers = studyGroupServiceHelper
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

    /**
     * 스터디 그룹의 회원을 추방합니다.
     *
     * @param groupId 스터디 그룹 ID
     * @param memberId 회원 ID
     * @param loggedInUserId 로그인된 사용자 ID
     */
    @Transactional
    public void expelMember(Long groupId, Long memberId, Long loggedInUserId) {
        StudyGroup studyGroup = studyGroupServiceHelper.findStudyGroupById(groupId);

        if (!studyGroup.getStudyLeaderId().equals(loggedInUserId)) {
            throw new AccessDeniedException("그룹 리더만 구성원을 추방할 수 있습니다.");
        }
        StudyMember studyMember = studyGroupServiceHelper.findStudyMemberById(studyGroup, memberId);
        studyGroupServiceHelper.deleteStudyMember(studyMember);
    }

    /**
     * 사용자가 스터디 그룹을 가지고 있는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return StudyGroupExistsDTO 스터디 그룹 존재 여부 DTO
     */
    @Transactional(readOnly = true)
    public StudyGroupExistsDTO userHasStudyGroup(Long userId) {
        boolean existsByStudyLeaderId = studyGroupServiceHelper.existsByStudyLeaderId(userId);
        return studyGroupMapper.toStudyGroupExistsDTO(existsByStudyLeaderId);
    }
}
