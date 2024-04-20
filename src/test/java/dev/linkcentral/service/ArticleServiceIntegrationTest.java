package dev.linkcentral.service;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleLike;
import dev.linkcentral.database.entity.ArticleStatistic;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.ArticleLikeRepository;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.database.repository.ArticleStatisticRepository;
import dev.linkcentral.database.repository.MemberRepository;
import dev.linkcentral.presentation.dto.request.article.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.request.article.ArticleUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class ArticleServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleStatisticRepository articleStatisticRepository;

    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    private Member createMember() {
        Member member = Member.builder()
                .name("김민석")
                .passwordHash("1234")
                .email("example@naver.com")
                .nickname("minseok")
                .deleted(false)
                .build();
        return member;
    }

    private Article createArticle() {
        Article article = Article.builder()
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();
        return article;
    }

    @DisplayName("게시글 저장 기능이 올바르게 작동하는지 검증")
    @Test
    void save_article() {
        // given
        ArticleCreateRequest articleDTO = ArticleCreateRequest.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();

        // when
//        articleService.saveArticle(articleDTO);

        // then
        List<Article> articlesList = articleRepository.findAll();
        assertFalse(articlesList.isEmpty());
        Article savedArticle = articlesList.get(0);

        assertEquals("제목", savedArticle.getTitle());
        assertEquals("내용", savedArticle.getContent());
        assertEquals("작성자", savedArticle.getWriter());
    }

    @DisplayName("모든 게시글을 올바르게 조회하는 기능 검증")
    @Test
    void find_all_articles() {
        // given
        Article firstArticle = Article.builder()
                .title("제목1")
                .content("내용1")
                .writer("작성자1")
                .build();

        Article secondArticle = Article.builder()
                .title("제목2")
                .content("내용2")
                .writer("작성자2")
                .build();
        articleRepository.save(firstArticle);
        articleRepository.save(secondArticle);

        // when
//        List<ArticleCreateRequest> foundArticles = articleService.findAllArticles();

        // then
//        assertEquals(2, foundArticles.size());
//        assertEquals("제목1", foundArticles.get(0).getTitle());
//        assertEquals("제목2", foundArticles.get(1).getTitle());
    }

    @DisplayName("ID를 통해 특정 게시글을 조회하는 기능 검증")
    @Test
    void find_article_by_id() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Article article = createArticle();
        articleRepository.save(article); // 실제 데이터베이스에 Article 객체 저장

        // when
        // 저장한 Article의 ID를 사용하여 조회
        ArticleCreateRequest foundArticle = articleService.findArticleById(article.getId(), member);

        // then
        assertNotNull(foundArticle);
        assertEquals("제목", foundArticle.getTitle());
        assertEquals("내용", foundArticle.getContent());
        assertEquals("작성자", foundArticle.getWriter());
    }

    @DisplayName("게시글의 조회수를 올바르게 업데이트하는 기능 검증")
    @Test
    void update_article_view_count() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Article article = createArticle();
        articleRepository.save(article);

        // when
        articleService.viewCountUpdate(member, article);

        // then
        Optional<ArticleStatistic> statistic = articleStatisticRepository.findByArticle(article);
        assertTrue(statistic.isPresent());
        assertEquals(1, statistic.get().getViews()); // 조회수가 1 증가했는지 확인
    }

    @DisplayName("게시글 수정 기능이 정상적으로 동작하는지 검증")
    @Test
    void update_article() {
        // given
        Article originalArticle = Article.builder()
                .title("기존 제목")
                .content("기존 내용")
                .writer("작성자")
                .build();
        originalArticle = articleRepository.save(originalArticle);

        ArticleUpdateRequest updateDTO = new ArticleUpdateRequest(
                originalArticle.getId(),
                "작성자",
                "수정된 제목",
                "수정된 내용");

        // when
//        articleService.updateArticle(updateDTO);

        // then
        Article updatedArticle = articleRepository.findById(originalArticle.getId()).orElse(null);
        assertNotNull(updatedArticle);
        assertEquals("수정된 제목", updatedArticle.getTitle());
        assertEquals("수정된 내용", updatedArticle.getContent());
    }

    @DisplayName("게시글 삭제 기능이 올바르게 작동하는지 검증")
    @Test
    void delete_article() {
        // given
        Article article = createArticle();
        articleRepository.save(article);

        Long articleId = article.getId();

        // when
        articleService.deleteArticle(articleId);

        // then
        Optional<Article> deletedArticle = articleRepository.findById(articleId);
        assertTrue(deletedArticle.isEmpty());
    }

    @DisplayName("페이징 처리된 게시글 목록이 올바르게 조회되는지 확인")
    @Test
    void get_paged_articles() {
        // given
        Article article1 = Article.builder()
                .title("제목1")
                .content("내용1")
                .writer("작성자1")
                .member(null) // 실제 테스트 환경에서는 적절한 Member 객체를 사용하거나 제외
                .build();

        Article article2 = Article.builder()
                .title("제목2")
                .content("내용2")
                .writer("작성자2")
                .member(null) // 실제 테스트 환경에서는 적절한 Member 객체를 사용하거나 제외
                .build();

        Article article3 = Article.builder()
                .title("제목3")
                .content("내용3")
                .writer("작성자3")
                .member(null) // 실제 테스트 환경에서는 적절한 Member 객체를 사용하거나 제외
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);

        // when
        int page = 1; // 1페이지
        int size = 3; // 페이지 당 3개의 게시글
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ArticleCreateRequest> pageResult = articleService.paginateArticles(pageable);

        // then
        assertEquals(3, pageResult.getContent().size()); // 페이지 내의 게시글 수 확인
        assertEquals("제목1", pageResult.getContent().get(0).getTitle()); // 첫 번째 게시글의 제목 확인
        assertEquals("내용1", pageResult.getContent().get(0).getContent()); // 첫 번째 게시글의 내용 확인
        assertEquals("작성자1", pageResult.getContent().get(0).getWriter()); // 첫 번째 게시글의 작성자 확인

        // 페이지 정보 검증
        assertEquals(page - 1, pageResult.getNumber()); // 현재 페이지 번호 확인
        assertEquals(size, pageResult.getSize()); // 페이지 당 게시글 수 확인
        assertEquals(1, pageResult.getTotalPages()); // 총 페이지 수 확인
    }

    @DisplayName("좋아요가 이미 존재할 경우 제거되는지 확인")
    @Test
    void toggle_like_when_like_exists() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Article article = Article.builder()
                .title("제목")
                .content("내용")
                .writer("작성자")
                .member(member)
                .build();
        article = articleRepository.save(article);

        ArticleLike existingLike = new ArticleLike(member, article);
        articleLikeRepository.save(existingLike);

        ArticleStatistic statistic = new ArticleStatistic(article);
        statistic.incrementLikes(); // 기존에 좋아요가 있음을 가정
        articleStatisticRepository.save(statistic);

        // when
        articleService.toggleArticleLike(article.getId(), member);

        // then
        assertFalse(articleLikeRepository.findByMemberAndArticle(member, article).isPresent()); // 좋아요가 제거되었는지 확인
        Optional<ArticleStatistic> updatedStatistic = articleStatisticRepository.findByArticle(article);
        assertTrue(updatedStatistic.isPresent()); // 통계 엔티티 존재 확인
        assertEquals(0, updatedStatistic.get().getLikes()); // 좋아요 수 감소 확인
    }

    @DisplayName("좋아요가 없을 경우 추가되는지 확인")
    @Test
    void toggle_like_when_like_not_exists() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Article article = Article.builder()
                .title("제목")
                .content("내용")
                .writer("작성자")
                .member(member)
                .build();
        article = articleRepository.save(article);

        ArticleStatistic statistic = new ArticleStatistic(article);
        articleStatisticRepository.save(statistic);

        // when
        articleService.toggleArticleLike(article.getId(), member);

        // then
        assertTrue(articleLikeRepository.findByMemberAndArticle(member, article).isPresent()); // 좋아요 추가 확인
        Optional<ArticleStatistic> updatedStatistic = articleStatisticRepository.findByArticle(article);
        assertTrue(updatedStatistic.isPresent()); // 통계 엔티티 존재 확인
        assertEquals(1, updatedStatistic.get().getLikes()); // 좋아요 수 증가 확인
    }

    @DisplayName("특정 게시글의 좋아요 총 갯수가 반환되는지 확인")
    @Test
    void get_article_likes_count() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Article article = Article.builder()
                .title("제목")
                .content("내용")
                .writer("작성자")
                .member(member)
                .build();
        article = articleRepository.save(article);

        // 좋아요를 5번 추가
        for (int i = 0; i < 5; i++) {
            ArticleLike like = new ArticleLike(member, article);
            articleLikeRepository.save(like);
        }

        // when
//        int likesCount = articleService.countArticleLikes(article.getId());

        // then
//        assertEquals(5, likesCount); // 좋아요 수 검증
    }

}