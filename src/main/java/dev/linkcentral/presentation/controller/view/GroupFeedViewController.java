package dev.linkcentral.presentation.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 그룹 피드 관련 뷰 컨트롤러
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/group-feed")
public class GroupFeedViewController {

    /**
     * 그룹 피드 생성 폼을 보여줍니다.
     *
     * @return "groupfeed/save" 템플릿 이름
     */
    @GetMapping("/create")
    public String showCreateFeedForm() {
        return "groupfeed/save";
    }

    /**
     * 그룹 피드 목록 페이지를 보여줍니다.
     *
     * @return "groupfeed/view" 템플릿 이름
     */
    @GetMapping("/list")
    public String showGroupFeedListPage() {
        return "groupfeed/view";
    }

    /**
     * 내 피드 목록 페이지를 보여줍니다.
     *
     * @return "groupfeed/my-feeds" 템플릿 이름
     */
    @GetMapping("/my-feeds")
    public String showMyFeedListPage() {
        return "groupfeed/my-feeds";
    }

    /**
     * 특정 ID의 그룹 피드 상세 정보를 보여줍니다.
     *
     * @param feedId 피드 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return "groupfeed/detail" 템플릿 이름
     */
    @GetMapping("/detail/{feedId}")
    public String showGroupFeedDetails(@PathVariable Long feedId, Model model) {
        model.addAttribute("feedId", feedId);
        return "groupfeed/detail";
    }

    /**
     * 특정 ID의 그룹 피드 수정 폼을 보여줍니다.
     *
     * @param feedId 피드 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return "groupfeed/update" 템플릿 이름
     */
    @GetMapping("/update/{feedId}")
    public String showUpdateFeedForm(@PathVariable Long feedId, Model model) {
        model.addAttribute("feedId", feedId);
        return "groupfeed/update";
    }
}
