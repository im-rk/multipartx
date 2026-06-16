package com.multipartx.multipartx.repository;

import com.multipartx.multipartx.domain.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetaData, Long> {
}
