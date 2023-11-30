package com.example.Ahi.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadDiffusionImage(byte[] imgBytes) {
        InputStream inputStream = new ByteArrayInputStream(imgBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imgBytes.length);
        metadata.setContentType("image/jpeg");

        // 파일 이름을 /profile 또는 /diffusion으로 시작하고 뒤에는 원래 img 이름을 기반으로 한 난수로 설정
        String keyName =  "diffusion/" + UUID.randomUUID() + ".jpg";

        // 파일을 업로드하고, 어디서든 다운로드할 수 있도록 퍼블릭 읽기 권한을 부여
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, keyName, inputStream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);

        return getFileUrl(keyName);
    }

    public String uploadProfileImage(byte[] imgBytes) {
        InputStream inputStream = new ByteArrayInputStream(imgBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imgBytes.length);
        metadata.setContentType("image/jpeg");

        // 파일 이름을 /profile 또는 /diffusion으로 시작하고 뒤에는 원래 img 이름을 기반으로 한 난수로 설정
        String keyName =  "profile/" + UUID.randomUUID() + ".jpg";

        // 파일을 업로드하고, 어디서든 다운로드할 수 있도록 퍼블릭 읽기 권한을 부여
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, keyName, inputStream, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);

        return getFileUrl(keyName);
    }

    private String getFileUrl(String keyName) {
        return amazonS3Client.getResourceUrl(bucket, keyName);
    }
}

