package dev.linkcentral.presentation.controller.view;

import dev.linkcentral.service.dto.article.ArticleCreatedAtDTO;
import dev.linkcentral.service.dto.article.ArticleDetailsDTO;
import dev.linkcentral.service.dto.article.ArticleViewDTO;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.facade.ArticleFacade;
import dev.linkcentral.service.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 게시글 관련 뷰 컨트롤러
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/view/article")
public class ArticleViewController {

    private final ArticleFacade articleFacade;

    /**
     * 게시글 저장 폼을 보여줍니다.
     *
     * @param model 뷰에 전달할 데이터 모델
     * @return "articles/save" 템플릿 이름
     */
    @GetMapping("/save-form")
    public String showArticleSaveForm(Model model) {
        MemberCurrentDTO memberCurrentDTO = articleFacade.saveForm();
        model.addAttribute("nickname", memberCurrentDTO.getNickname());
        return "articles/save";
    }

    /**
     * 특정 ID의 게시글 상세 정보를 보여줍니다.
     *
     * @param pageable 페이징 정보
     * @param id 게시글 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return "/articles/detail" 템플릿 이름
     */
    @GetMapping("/{id}")
    public String showArticleDetails(@PageableDefault(size = 5, sort = "id",
                           direction = Sort.Direction.DESC) Pageable pageable,
                           @PathVariable Long id, Model model) {

        ArticleViewDTO articleViewDTO = articleFacade.findById(id);
        model.addAttribute("article", articleViewDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "/articles/detail";
    }

    /**
     * 게시글 수정 폼을 보여줍니다.
     *
     * @param id 게시글 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return "/articles/update" 템플릿 이름
     */
    @GetMapping("/update-form/{id}")
    public String showArticleUpdateForm(@PathVariable Long id, Model model) {
        ArticleDetailsDTO articleDetailsDTO = articleFacade.updateForm(id);
        model.addAttribute("articleUpdate", articleDetailsDTO);
        return "/articles/update";
    }

    /**
     * 게시글을 삭제하고 리다이렉트합니다.
     *
     * @param id 게시글 ID
     * @return "redirect:/api/v1/view/article/" 리다이렉트 URL
     */
    @GetMapping("/delete-form/{id}")
    public String deleteArticleAndRedirect(@PathVariable Long id) {
        articleFacade.deleteForm(id);
        return "redirect:/api/v1/view/article/";
    }

    /**
     * 페이징된 게시글 목록을 보여줍니다.
     *
     * @param pageable 페이징 정보
     * @param model 뷰에 전달할 데이터 모델
     * @return "/articles/paging" 템플릿 이름
     */
    @GetMapping("/paging")
    public String showArticlesWithPagination(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<ArticleViewDTO> articlePage = articleFacade.paging(pageable);
        List<ArticleViewDTO> articleList = articlePage.getContent(); // Page에서 List로 변환

        int blockLimit = 3;
        int startPage = (((int) Math.ceil(((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), articlePage.getTotalPages());
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        List<ArticleCreatedAtDTO> formattedCreatedAtList = articleFacade.getFormattedCreatedAtList(articleList);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("articleList", articleList); // List로 전달
        model.addAttribute("formattedCreatedAtList", formattedCreatedAtList);
        model.addAttribute("articlePage", articlePage); // 페이지 정보 전달
        model.addAttribute("startPage", startPage);     // 시작 페이지
        model.addAttribute("endPage", endPage);         // 마지막 페이지
        return "/articles/paging";
    }
}
