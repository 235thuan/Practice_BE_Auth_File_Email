package com.example.authenticationauthorization.model;

import jakarta.persistence.*;

@Entity
public class StoredFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(unique = true,name="file_name")
    private String fileName;

    @Column(unique = true,name="file_path")
    private String filePath;

    @Column(unique = true,name="convert_path")
    private String convertPath;
    public StoredFile() {
    }
    public StoredFile(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getConvertPath() {
        return convertPath;
    }

    public void setConvertPath(String convertPath) {
        this.convertPath = convertPath;
    }
}
