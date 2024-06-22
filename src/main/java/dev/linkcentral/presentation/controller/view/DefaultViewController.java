package dev.linkcentral.presentation.controller.view;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 기본 뷰 컨트롤러
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class DefaultViewController {

    /**
     * 홈 화면을 보여줍니다.
     *
     * @return "/home" 템플릿 이름
     */
    @GetMapping("/")
    public String showHome() {
        return "/home";
    }
}
