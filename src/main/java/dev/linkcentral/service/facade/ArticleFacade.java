package dev.linkcentral.service.facade;

import dev.linkcentral.database.entity.Member;
import dev.linkcentral.service.ArticleService;
import dev.linkcentral.service.MemberService;
import dev.linkcentral.service.dto.article.*;
import dev.linkcentral.service.dto.member.MemberCurrentDTO;
import dev.linkcentral.service.mapper.ArticleCommentMapper;
import dev.linkcentral.service.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleFacade {

    private final ArticleService articleService;
    private final MemberService memberService;
    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;

    public ArticleCreateDTO createAndSaveArticle(ArticleCreateRequestDTO createRequestDTO) {
        Member currentMember = memberService.getCurrentMember();
        ArticleCreateDTO articleCreateDTO = articleMapper.toArticleCreateDTO(createRequestDTO, currentMember);
        return articleService.saveArticle(articleCreateDTO);
    }

    public ArticleUpdatedDTO updateArticle(ArticleUpdateRequestDTO updateRequestDTO) {
        Member member = memberService.getCurrentMember();
        ArticleUpdateDTO articleDTO = articleMapper.toArticleUpdateDTO(updateRequestDTO);
        return articleService.updateArticle(articleDTO);
    }

    public void deleteArticle(Long id) {
        articleService.deleteArticle(id);
    }

    public ArticleLikeDTO toggleLike(Long id) {
        Member member = memberService.getCurrentMember();
        return articleService.toggleArticleLike(id, member);
    }

    public ArticleLikesCountDTO getLikesCount(Long id) {
        Member member = memberService.getCurrentMember();
        return articleService.countArticleLikes(id);
    }

    public ArticleCommentDTO commentSave(ArticleCommentRequestDTO commentRequestDTO) {
        Member member = memberService.getCurrentMember();
        ArticleCommentDTO commentDTO = articleCommentMapper.toArticleCommentDTO(commentRequestDTO, member.getNickname());
        return articleService.saveComment(commentDTO, member.getNickname());
    }

    public ArticleCommentUpdateDTO updateComment(ArticleCommentRequestDTO commentRequestDTO, Long commentId) {
        Member member = memberService.getCurrentMember();
        ArticleCommentUpdateDTO commentUpdateDTO = articleCommentMapper.toArticleCommentUpdateDto(commentRequestDTO, commentId);
        return articleService.updateComment(commentUpdateDTO, member.getNickname());
    }

    public void deleteComment(Long commentId) {
        Member member = memberService.getCurrentMember();
        articleService.deleteComment(commentId, member.getNickname());
    }

    public ArticleDetailsDTO getArticleDetails(Long id) {
        return articleService.getArticleById(id);
    }

    public MemberCurrentDTO saveForm() {
        Member currentMember = memberService.getCurrentMember();
        return articleMapper.toMemberCurrentDTO(currentMember.getId(), currentMember.getNickname());
    }

    public ArticleViewDTO findById(Long articleId) {
        Member currentMember = memberService.getAuthenticatedMember();
        return articleService.findArticleById(articleId, currentMember);
    }

    public ArticleDetailsDTO updateForm(Long id) {
        return articleService.getArticleById(id);
    }

    public void deleteForm(Long id) {
        articleService.deleteArticle(id);
    }

    public Page<ArticleViewDTO> paging(Pageable pageable) {
        return articleService.paginateArticles(pageable);
    }

    public Page<ArticleCommentViewDTO> getCommentsForArticle(Long id, Pageable pageable) {
         return articleService.findCommentsForScrolling(id, pageable);
    }

    public List<ArticleCreatedAtDTO> getFormattedCreatedAtList(List<ArticleViewDTO> articleList) {
        return articleList.stream()
                .map(ArticleMapper::toArticleCreatedAtDTO)
                .collect(Collectors.toList());
    }

    public ArticleCurrentMemberDTO getCurrentMemberId() {
        Member member = memberService.getCurrentMember();
        return articleMapper.toCurrentMemberIdDTO(member.getId());
    }
}
