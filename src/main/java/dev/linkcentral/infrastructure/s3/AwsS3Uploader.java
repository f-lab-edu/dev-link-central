package dev.linkcentral.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dev.linkcentral.infrastructure.s3.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 파일 업로드 컴포넌트
 * 파일을 S3에 업로드하고 해당 파일의 URL을 반환합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AwsS3Uploader implements FileUploader {

    private final AmazonS3Client amazonS3Client;

    // S3 버킷 이름 주입
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile multipartFile, String dirName) {
        try {
            String fileName = generateFileName(dirName, multipartFile.getOriginalFilename());
            ObjectMetadata metadata = createObjectMetadata(multipartFile);

            // S3에 파일 업로드
            amazonS3Client.putObject(new PutObjectRequest(
                    bucket,
                    fileName,
                    multipartFile.getInputStream(),
                    metadata).withCannedAcl(CannedAccessControlList.PublicRead));

            // 파일 URL 반환
            return amazonS3Client.getResourceUrl(bucket, fileName);
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    // 파일명 생성 함수
    private String generateFileName(String dirName, String originalFilename) {
        return dirName + "/" + UUID.randomUUID().toString() + "_" + originalFilename;
    }

    // 메타데이터 생성 함수
    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        return metadata;
    }
}
