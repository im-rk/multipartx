package com.multipartx.multipartx.domain;

import com.multipartx.multipartx.domain.enums.UploadStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="file_metadata")
public class FileMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String s3key;
    private String contentType;
    private Long size;

    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    private LocalDateTime createdAt;

}
