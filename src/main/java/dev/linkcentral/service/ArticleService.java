package dev.linkcentral.service;

import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.ArticleCommentRepository;
import dev.linkcentral.database.repository.ArticleLikeRepository;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.database.repository.ArticleStatisticRepository;
import dev.linkcentral.service.dto.request.ArticleCommentRequestDTO;
import dev.linkcentral.service.dto.request.ArticleRequestDTO;
import dev.linkcentral.service.dto.request.ArticleUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import java.util.*;

// TODO: 메서드 네이밍 방식 통일 e.g. updateComment, commentSave
// TODO: unit test 작성 -> unit test를 통해 각 메서드가 어떤 의도를 가지고 있는지 파악할 수 있도록

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleStatisticRepository articleStatisticRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;

    // TODO: Transactional
    public void save(ArticleRequestDTO articleDTO) {
        Article articleEntity = Article.toSaveEntity(articleDTO);
        articleRepository.save(articleEntity);
    }

    // TODO: Transactional
    public List<ArticleRequestDTO> findAll() {
        List<Article> articleEntityList = articleRepository.findAll();
        List<ArticleRequestDTO> articleDTOList = new ArrayList<>();

        for (Article articleEntity : articleEntityList) {
            articleDTOList.add(ArticleRequestDTO.toArticleDTO(articleEntity));
        }
        return articleDTOList;
    }

    // TODO: Transactional
    public ArticleRequestDTO findById(Long id, HttpSession session) {
        return articleRepository.findById(id)
                .map(articleEntity -> {
                    // 세션에서 조회된 게시글 ID 확인
                    Set<Long> viewedArticles = (Set<Long>) session.getAttribute("viewedArticles");
                    if (viewedArticles == null) {
                        viewedArticles = new HashSet<>();
                    }

                    // 해당 게시글을 처음 조회하는 경우에만 조회수 증가
                    if (!viewedArticles.contains(id)) {
                        ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(articleEntity)
                                .orElseGet(() -> new ArticleStatistic(articleEntity));

                        articleStatistic.incrementViews();
                        articleStatisticRepository.save(articleStatistic);
                        viewedArticles.add(id);
                        session.setAttribute("viewedArticles", viewedArticles);
                    }

                    ArticleRequestDTO articleDTO = ArticleRequestDTO.toArticleDTO(articleEntity);
                    // Member 객체가 null이 아닐 때만 writerId 설정
                    if (articleEntity.getMember() != null) {
                        articleDTO.setWriterId(articleEntity.getMember().getId());
                    }

                    // 조회수 설정
                    articleStatisticRepository.findByArticle(articleEntity).ifPresent(
                            articleStatistic -> articleDTO.setViews(articleStatistic.getViews())
                    );
                    return articleDTO;
                })
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")); // 게시글이 없는 경우 예외 발생
    }

    // TODO: Transactional
    public ArticleRequestDTO update(ArticleUpdateRequestDTO articleDTO) {
        Article articleEntity = Article.toUpdateEntity(articleDTO);
        Article updateArticle = articleRepository.save(articleEntity);

        Optional<Article> optionalArticleEntity = articleRepository.findById(updateArticle.getId());
        if (optionalArticleEntity.isPresent()) {
            Article article = optionalArticleEntity.get();
            return ArticleRequestDTO.toArticleDTO(article);
        }
        return null;
    }

    // TODO: Transactional
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    // TODO: Transactional
    public Page<ArticleRequestDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한 페이지에 보여줄 글 갯수

        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        Page<Article> articleEntity = articleRepository.findAll(PageRequest
                .of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        return articleEntity.map(article -> new ArticleRequestDTO(
                article.getId(),
                article.getWriter(),
                article.getTitle(),
                article.getCreatedAt()));
    }

    @Transactional
    public void toggleLike(Long articleId, Member member) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<ArticleLike> like = articleLikeRepository.findByMemberAndArticle(member, article);
        ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(article)
                .orElseGet(() -> new ArticleStatistic(article));

        if (like.isPresent()) {
            articleLikeRepository.delete(like.get());
            articleStatistic.decrementLikes(); // 좋아요 감소
        } else {
            ArticleLike newLike = new ArticleLike(member, article);
            articleLikeRepository.save(newLike);
            articleStatistic.incrementLikes(); // 좋아요 증가
        }
        articleStatisticRepository.save(articleStatistic); // 상태 업데이트 저장
    }

    // 특정 게시글의 "좋아요" 총 갯수를 반환
    // TODO: Transactional
    public int getLikesCount(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return (int) articleLikeRepository.countByArticle(article);
    }

    @Transactional
    public Long commentSave(ArticleCommentRequestDTO commentDTO, String writerNickname) {
        Optional<Article> optionalArticle = articleRepository.findById(commentDTO.getArticleId());
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            ArticleComment commentEntity = ArticleComment.toSaveEntity(commentDTO, article, writerNickname);
            articleCommentRepository.save(commentEntity);
            return commentEntity.getId();
        } else {
            throw new EntityNotFoundException("ID가 포함된 게시글을 찾을 수 없습니다.");
        }
    }

    public List<ArticleCommentRequestDTO> commentFindAll(Long articleId) {
        Article article = articleRepository.findById(articleId).get();
        List<ArticleComment> commentList = articleCommentRepository.findAllByArticleOrderByIdDesc(article);

        List<ArticleCommentRequestDTO> commentDTOList = new ArrayList<>();
        for (ArticleComment comment : commentList) {
            ArticleCommentRequestDTO commentDTO = ArticleCommentRequestDTO.toCommentDTO(comment, articleId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }

    @Transactional
    public void updateComment(Long commentId, ArticleCommentRequestDTO commentDTO, String currentNickname) {
        ArticleComment comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(commentDTO.getContents());
        articleCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String currentNickname) {
        ArticleComment comment = articleCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getWriterNickname().equals(currentNickname)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        articleCommentRepository.delete(comment);
    }
}