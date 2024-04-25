package dev.linkcentral.service.facade;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.presentation.dto.StudyGroupIdsDTO;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.StudyGroupService;
import dev.linkcentral.service.StudyMemberService;
import dev.linkcentral.service.mapper.StudyGroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StudyGroupFacade {

    private final MemberService memberService;
    private final ArticleService articleService;
    private final StudyGroupService studyGroupService;
    private final StudyMemberService studyMemberService;
    private final StudyGroupMapper studyGroupMapper;

    public StudyGroupIdsDTO getStudyGroupIdsForMember() {
        Member member = memberService.getCurrentMember();
        List<Long> studyGroupIds = studyMemberService.getStudyGroupIdsByMemberId(member.getId());
        return studyGroupMapper.toStudyGroupIdsDTO(studyGroupIds);
    }

}
