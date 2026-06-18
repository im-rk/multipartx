package com.multipartx.multipartx.service;

import com.multipartx.multipartx.domain.FileMetadata;
import com.multipartx.multipartx.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MultipartXService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FileMetadataRepository repository;

    public MultipartXService(S3Client s3Client, S3Presigner s3Presigner, FileMetadataRepository repository) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.repository = repository;
    }

    // 1. Normal Upload Flow (Proxy through Backend Memory)
    public FileMetadata uploadFileNormal(String bucketName, MultipartFile file) throws Exception {
        String s3Key = "uploads/normal/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setS3Key(s3Key);
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setStatus(FileMetadata.UploadStatus.COMPLETED);
        metadata.setCreatedAt(LocalDateTime.now());

        return repository.save(metadata);
    }

    public String generatePresignedUploadUrl(String bucketName, String fileName, String contentType) {
        String s3Key = "uploads/presigned/" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest putObjectPresignRequest= PutObjectPresignRequest.builder()
                .signatureDuration(java.time.Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(putObjectPresignRequest).url().toString();

        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(fileName);
        metadata.setS3Key(s3Key);
        metadata.setContentType(contentType);
        metadata.setStatus(FileMetadata.UploadStatus.PENDING);
        metadata.setCreatedAt(LocalDateTime.now());
        repository.save(metadata);

        return uploadUrl;
    }

}