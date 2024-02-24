package dev.linkcentral.infrastructure.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        // 액세스 키와 시크릿 키를 이용하여 AWS 기본 인증 정보를 생성
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // AmazonS3Client 빌더를 사용하여 S3 클라이언트를 생성
        // 설정된 리전과 인증 정보를 사용하여 클라이언트를 구성한다.
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}