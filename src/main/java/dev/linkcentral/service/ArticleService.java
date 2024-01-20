package dev.linkcentral.service;

import dev.linkcentral.common.exception.CustomOptimisticLockException;
import dev.linkcentral.database.entity.*;
import dev.linkcentral.database.repository.ArticleLikeRepository;
import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleStatistic;
import dev.linkcentral.database.entity.ArticleLike;
import dev.linkcentral.database.entity.ArticleStatistic;
import dev.linkcentral.database.entity.ArticleView;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.ArticleLikeRepository;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.database.repository.ArticleStatisticRepository;
import dev.linkcentral.database.repository.ArticleStatisticRepository;
import dev.linkcentral.database.repository.ArticleViewRepository;
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

import javax.servlet.http.HttpSession;
import java.util.*;
import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private static int RETRY_COUNT = 0;
    private static final int MAX_RETRIES = 3; // 최대 재시도 횟수를 정의

    private final ArticleRepository articleRepository;
    private final ArticleStatisticRepository articleStatisticRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleViewRepository articleViewRepository;

    @Transactional
    public void save(ArticleRequestDTO articleDTO) {
        Article articleEntity = Article.toSaveEntity(articleDTO);
        articleRepository.save(articleEntity);
    }

    @Transactional(readOnly = true)
    public List<ArticleRequestDTO> findAll() {
        List<Article> articleEntityList = articleRepository.findAll();
        List<ArticleRequestDTO> articleDTOList = new ArrayList<>();

        for (Article articleEntity : articleEntityList) {
            articleDTOList.add(ArticleRequestDTO.toArticleDTO(articleEntity));
        }
        return articleDTOList;
    }

    @Transactional
    public ArticleRequestDTO findById(Long id, Member member) {
        return articleRepository.findById(id)
                .map(article -> {
                    viewCountUpdate(member, article);
                    ArticleStatistic statistic = articleStatisticRepository.findByArticle(article)
                            .orElse(new ArticleStatistic());
                    return ArticleRequestDTO.toArticleDTOWithViews(article, statistic.getViews());
                }).orElse(null);
    }

    @Transactional
    public void viewCountUpdate(Member member, Article article) {
        while (RETRY_COUNT < MAX_RETRIES) {
            try {
                if (isFirstView(member, article)) {
                    ArticleStatistic articleStatistic = articleStatisticRepository.findByArticle(article)
                            .orElseGet(() -> new ArticleStatistic(article));
                    articleStatistic.incrementViews();
                    articleStatisticRepository.save(articleStatistic);
                }
                break; // 성공적으로 완료시 루프 탈출
            } catch (OptimisticLockException e) {
                RETRY_COUNT++;
                if (RETRY_COUNT >= MAX_RETRIES) {
                    throw new CustomOptimisticLockException("조회수 업데이트를 위한 최대 재시도 횟수 초과");
                }
            }
        }
    }

    private boolean isFirstView(Member member, Article article) {
        boolean alreadyViewed = articleViewRepository.existsByMemberAndArticle(member, article);
        if (alreadyViewed) {
            return false; // 이미 조회한 경우
        }
        ArticleView articleView = new ArticleView(member, article);
        articleViewRepository.save(articleView);
        return true; // 처음 조회한 경우
    }

    @Transactional
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

    @Transactional
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
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
    public int getLikesCount(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return (int) articleLikeRepository.countByArticle(article);
    }
}