package com.example.authenticationauthorization.repository;

import com.example.authenticationauthorization.model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
    StoredFile findByFileName(String fileName);
}
