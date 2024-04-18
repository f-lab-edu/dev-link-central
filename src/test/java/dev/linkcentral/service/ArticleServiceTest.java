package dev.linkcentral.service;

import dev.linkcentral.database.entity.Article;
import dev.linkcentral.database.entity.ArticleLike;
import dev.linkcentral.database.entity.ArticleStatistic;
import dev.linkcentral.database.entity.Member;
import dev.linkcentral.database.repository.ArticleLikeRepository;
import dev.linkcentral.database.repository.ArticleRepository;
import dev.linkcentral.database.repository.ArticleStatisticRepository;
import dev.linkcentral.database.repository.ArticleViewRepository;
import dev.linkcentral.presentation.dto.request.ArticleCreateRequest;
import dev.linkcentral.presentation.dto.request.ArticleUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleStatisticRepository articleStatisticRepository;

    @Mock
    private ArticleLikeRepository articleLikeRepository;

    @Mock
    private ArticleViewRepository articleViewRepository;

    @InjectMocks
    private ArticleService articleService;

    private static Member getMember() {
        Member member = Member.builder()
                .id(1L)
                .name("김민석")
                .passwordHash("password")
                .email("example@example.com")
                .nickname("nickname")
                .deleted(false)
                .build();
        return member;
    }

    @DisplayName("게시글 저장 기능이 올바르게 작동하는지 검증")
    @Test
    void save_article() {
        // given
        ArticleCreateRequest articleDTO = new ArticleCreateRequest(
                null,
                "제목",
                "작성자",
                null);

        Article savedArticle = Article.builder()
                .title(articleDTO.getTitle())
                .content("내용")
                .writer(articleDTO.getWriter())
                .build();
        when(articleRepository.save(any(Article.class))).thenReturn(savedArticle);

        // when
//        articleService.saveArticle(articleDTO);

        // then
        verify(articleRepository).save(any(Article.class));
    }

    @DisplayName("모든 게시글을 올바르게 조회하는 기능 검증")
    @Test
    void find_all_articles() {
        // given
        List<Article> articles = Arrays.asList(
                Article.builder()
                        .id(1L)
                        .title("제목1")
                        .content("내용1")
                        .writer("작성자1")
                        .build(),

                Article.builder()
                        .id(2L)
                        .title("제목2")
                        .content("내용2")
                        .writer("작성자2")
                        .build()
        );
        when(articleRepository.findAll()).thenReturn(articles);

        // when
        List<ArticleCreateRequest> foundArticles = articleService.findAllArticles();

        // then
        assertEquals(2, foundArticles.size());
        assertEquals("제목1", foundArticles.get(0).getTitle());
        assertEquals("제목2", foundArticles.get(1).getTitle());
    }

    @DisplayName("ID를 통해 특정 게시글을 조회하는 기능 검증")
    @Test
    void find_article_by_id() {
        // given
        Long articleId = 1L;
        Member member = getMember();

        Article article = Article.builder()
                .id(articleId)
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // when
        ArticleCreateRequest foundArticle = articleService.findArticleById(articleId, member);

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
        Member member = getMember();

        Article article = Article.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();
        ArticleStatistic articleStatistic = new ArticleStatistic();

        when(articleViewRepository.existsByMemberAndArticle(member, article)).thenReturn(false);
        when(articleStatisticRepository.findByArticle(article)).thenReturn(Optional.of(articleStatistic));

        // when
        articleService.viewCountUpdate(member, article);

        // then
        verify(articleStatisticRepository).save(any(ArticleStatistic.class));
    }

    @DisplayName("게시글 수정 기능이 정상적으로 동작하는지 검증")
    @Test
    void update_article() {
        // given
        ArticleUpdateRequest updateDTO = new ArticleUpdateRequest(
                1L,
                "작성자",
                "수정된 제목",
                "수정된 내용");

        Article originalArticle = Article.builder()
                .id(1L)
                .title("원래 제목")
                .content("원래 내용")
                .writer("작성자")
                .build();

        when(articleRepository.findById(1L)).thenReturn(Optional.of(originalArticle));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article passedArticle = invocation.getArgument(0);
            originalArticle.updateTitle(passedArticle.getTitle());
            originalArticle.updateContent(passedArticle.getContent());
            return originalArticle;
        });

        // when
//        ArticleCreateRequest updatedArticle = articleService.updateArticle(updateDTO);

        // then
//        assertNotNull(updatedArticle);
//        assertEquals("수정된 제목", updatedArticle.getTitle());
//        assertEquals("수정된 내용", updatedArticle.getContent());
    }

    @DisplayName("게시글 삭제 기능이 올바르게 작동하는지 검증")
    @Test
    void delete_article() {
        // given
        Long articleId = 1L;

        // when
        articleService.deleteArticle(articleId);

        // then
        verify(articleRepository).deleteById(articleId);
    }

    @DisplayName("페이징 처리된 게시글 목록이 올바르게 조회되는지 확인")
    @Test
    void get_paged_articles() {
        // given
        int page = 1; // 1페이지 (사용자 관점에서의 페이지 번호, 1부터 시작)
        int size = 3; // 페이지 당 3개의 게시글

        // 서비스에서 페이지 번호를 0 기반으로 처리하므로 조정
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Article> articles = Arrays.asList(
                Article.builder().id(1L).title("제목1").content("내용1").writer("작성자1").build(),
                Article.builder().id(2L).title("제목2").content("내용2").writer("작성자2").build(),
                Article.builder().id(3L).title("제목3").content("내용3").writer("작성자3").build()
        );
        Page<Article> articlePage = new PageImpl<>(articles, pageable, 6); // 총 6개의 게시글이 있다고 가정

        when(articleRepository.findAll(any(PageRequest.class))).thenReturn(articlePage);

        // when
        Page<ArticleCreateRequest> pageResult = articleService.paginateArticles(pageable);

        // then
        assertEquals(3, pageResult.getContent().size()); // 페이지 내의 게시글 수 확인
        assertEquals("제목1", pageResult.getContent().get(0).getTitle());    // 첫 번째 게시글의 제목 확인
        assertEquals("내용1", pageResult.getContent().get(0).getContent());  // 첫 번째 게시글의 내용 확인
        assertEquals("작성자1", pageResult.getContent().get(0).getWriter()); // 첫 번째 게시글의 작성자 확인

        // 페이지 정보 검증
        assertEquals(page - 1, pageResult.getNumber()); // 현재 페이지 번호 확인 (0 기반 인덱스)
        assertEquals(size, pageResult.getSize());               // 페이지 당 게시글 수 확인
        assertEquals(2, pageResult.getTotalPages());   // 총 페이지 수 확인
    }

    @DisplayName("좋아요가 이미 존재할 경우 제거되는지 확인")
    @Test
    void toggle_like_when_like_exists() {
        // given
        Long articleId = 1L;
        Member member = getMember();
        Article article = Article.builder()
                .id(articleId)
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();

        ArticleLike existingLike = new ArticleLike(member, article);
        ArticleStatistic statistic = spy(new ArticleStatistic(article));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleLikeRepository.findByMemberAndArticle(member, article)).thenReturn(Optional.of(existingLike));
        when(articleStatisticRepository.findByArticle(article)).thenReturn(Optional.of(statistic));

        // when
        articleService.toggleArticleLike(articleId, member);

        // then
        verify(articleLikeRepository).delete(existingLike); // 좋아요 제거 확인
        verify(articleStatisticRepository).save(statistic); // 통계 업데이트 확인
        verify(statistic).decrementLikes(); // 좋아요 감소 메서드 호출 확인
    }

    @DisplayName("좋아요가 없을 경우 추가되는지 확인")
    @Test
    void toggle_like_when_like_not_exists() {
        // given
        Long articleId = 1L;
        Member member = getMember();
        Article article = Article.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();

        ArticleStatistic statistic = new ArticleStatistic(article);
        ArticleStatistic statisticSpy = Mockito.spy(statistic);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleLikeRepository.findByMemberAndArticle(member, article)).thenReturn(Optional.empty());
        when(articleStatisticRepository.findByArticle(article)).thenReturn(Optional.of(statisticSpy));

        // when
        articleService.toggleArticleLike(articleId, member);

        // then
        verify(articleLikeRepository).save(any(ArticleLike.class)); // 좋아요 추가 확인
        verify(articleStatisticRepository).save(statisticSpy); // 통계 업데이트 확인
        verify(statisticSpy).incrementLikes(); // 좋아요 증가 메서드 호출 확인
    }

    @DisplayName("특정 게시글의 좋아요 총 갯수가 반환되는지 확인")
    @Test
    void get_article_likes_count() {
        // given
        Long articleId = 1L;
        Article article = Article.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .writer("작성자")
                .build();
        long expectedLikesCount = 5; // 예상되는 좋아요 수

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleLikeRepository.countByArticle(article)).thenReturn(expectedLikesCount);

        // when
        int likesCount = articleService.countArticleLikes(articleId);

        // then
        assertEquals((int) expectedLikesCount, likesCount); // 좋아요 수 검증
    }
}