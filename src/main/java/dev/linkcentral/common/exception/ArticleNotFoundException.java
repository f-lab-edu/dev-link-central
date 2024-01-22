package dev.linkcentral.common.exception;

/**
 * 사용자가 존재하지 않는 게시글 ID를 요청한 경우
 */
public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException() {
        super("해당 ID의 게시글을 찾을 수 없습니다.");
    }
}