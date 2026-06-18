package com.multipartx.multipartx.repository;

import com.multipartx.multipartx.domain.FileMetaData;
import com.multipartx.multipartx.domain.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}