package dev.linkcentral.service.facade;

import dev.member.entity.Member;
import dev.linkcentral.service.ArticleService;
import dev.member.service.MemberService;
import dev.linkcentral.service.dto.article.*;
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

    private final ArticleMapper articleMapper;
    private final MemberService memberService;
    private final ArticleService articleService;
    private final ArticleCommentMapper articleCommentMapper;

    public ArticleCreateDTO createAndSaveArticle(ArticleCreateRequestDTO createRequestDTO) {
        Member currentMember = memberService.getCurrentMember();
        ArticleCreateDTO articleCreateDTO = articleMapper.toArticleCreateDTO(createRequestDTO, currentMember);
        return articleService.saveArticle(articleCreateDTO, currentMember);
    }

    public ArticleUpdatedDTO updateArticle(ArticleUpdateRequestDTO updateRequestDTO) {
        Member member = memberService.getCurrentMember();
        ArticleUpdateDTO articleDTO = articleMapper.toArticleUpdateDTO(updateRequestDTO);
        articleDTO.updateWriterId(member.getId());
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
        return articleService.saveComment(commentDTO, member);
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
        Member currentMember = memberService.current();
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

    public Page<ArticleCommentViewDTO> getCommentsForArticle(Long id, int offset, int limit) {
        Member member = memberService.getCurrentMember();
        return articleService.findCommentsForScrolling(id, offset, limit);
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
