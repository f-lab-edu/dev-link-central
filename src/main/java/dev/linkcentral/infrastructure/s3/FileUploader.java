package dev.linkcentral.infrastructure.s3;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String uploadFile(MultipartFile file, String directory);
}
