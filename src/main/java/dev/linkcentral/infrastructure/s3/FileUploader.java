package dev.linkcentral.infrastructure.s3;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로더 인터페이스
 * 파일을 업로드하는 메서드를 정의합니다.
 */
public interface FileUploader {

    /**
     * 파일을 업로드하고 해당 파일의 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @param directory 파일이 업로드될 디렉토리
     * @return 업로드된 파일의 URL
     */
    String uploadFile(MultipartFile file, String directory);
}
