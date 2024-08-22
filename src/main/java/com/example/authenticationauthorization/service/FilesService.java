package com.example.authenticationauthorization.service;


import com.example.authenticationauthorization.dto.FileResponseDTO;

import com.example.authenticationauthorization.mapper.FileResponseDTOMapper;

import com.example.authenticationauthorization.model.StoredFile;

import com.example.authenticationauthorization.repository.StoredFileRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilesService {

    @Value("${file.storage.location}")
    private String fileStorageLocation;
    @Value("${file.convert.location}")
    private String fileConvertLocation;

    private final ConvertPdfService convertPdfService;
    private final StoredFileRepository fileRepository;
    private final FileResponseDTOMapper fileResponseDTOMapper = FileResponseDTOMapper.INSTANCE;

    public FilesService(ConvertPdfService convertPdfService, StoredFileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.convertPdfService = convertPdfService;
    }

    @Transactional
    public FileResponseDTO storeFile(String filename, byte[] fileContent, String username) {
        String sanitizedFilename = filename.replace(" ", "_");
        Path targetLocation = getTargetLocation(username + sanitizedFilename);

        try {
            // Store file content
            storeFileContent(targetLocation, fileContent);

            // Save file metadata to database
            return fileResponseDTOMapper.toFileResponseDTO(saveFileMetadata(filename, targetLocation));

        } catch (IOException e) {
            // Log the error details
            System.err.println("Error storing file: " + e.getMessage());
            throw new RuntimeException("Could not store file. Please try again!", e);
        }
    }

    @Transactional
    public void convertFile(Long fileId, String username) throws IOException {
        StoredFile storedFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));
        String filePath = storedFile.getFilePath();
        String fileName = storedFile.getFileName();
        String sanitizedFilename = fileName.replace(" ", "_");


        Path convertLocation = getConvertLocation(username + sanitizedFilename);

        // Convert and write the file
        convertPdfService.convertToPdf(filePath, convertLocation.toString());

        editConvertPath(fileId, convertLocation);

        System.out.println("File converted successfully: " + convertLocation.toAbsolutePath());

    }


    private Path getTargetLocation(String addPath) {
        return Paths.get(fileStorageLocation).toAbsolutePath().normalize().resolve(addPath);
//        return Paths.get(fileStorageLocation);
    }

    private Path getConvertLocation(String addPath) {
        return Paths.get(fileConvertLocation).toAbsolutePath().normalize().resolve(addPath);
//        return Paths.get(fileConvertLocation);
    }

    private void storeFileContent(Path targetLocation, byte[] fileContent) throws IOException {
        // Ensure the target directory exists
        Path parentDir = targetLocation.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // Write the file content
        try (OutputStream os = new FileOutputStream(targetLocation.toFile())) {
            os.write(fileContent);
            System.out.println("File stored successfully: " + targetLocation.toAbsolutePath());
        }
    }

    private StoredFile saveFileMetadata(String filename, Path targetLocation) {
        // Save file path to database
        StoredFile storedFile = new StoredFile();
        storedFile.setFileName(filename);
        storedFile.setFilePath(targetLocation.toString()); // Save file path
        return fileRepository.save(storedFile);
    }

    private void editConvertPath(Long id, Path convertLocation) {
        Optional<StoredFile> storedFileOptional = fileRepository.findById(id);
        if (storedFileOptional.isPresent()) {
            StoredFile storedFile = storedFileOptional.get();
            storedFile.setConvertPath(convertLocation.toString());
            fileRepository.save(storedFile); // Save the updated file record to the database
        } else {
            throw new RuntimeException("File with ID " + id + " not found.");
        }
    }

    public Resource downloadResource(Long id, String username) throws IOException {
        Optional<StoredFile> storedFile = fileRepository.findById(id);
        if (storedFile.isPresent()) {
            String fileName = storedFile.get().getFileName();
            String sanitizedFilename = fileName.replace(" ", "_");

            Path filePath = getTargetLocation(username + sanitizedFilename);
            return loadFileAsResource(filePath);
        } else {
            throw new IOException("Could not download file with ID: " + id);
        }
    }

    public Resource downloadConvert(Long id, String username) throws IOException {
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            // Create a Callable to perform the download task
            Callable<Resource> downloadTask = () -> {
                Optional<StoredFile> storedFile = fileRepository.findById(id);
                if (storedFile.isPresent()) {
                    String fileName = storedFile.get().getFileName();
                    String sanitizedFilename = fileName.replace(" ", "_");
                    Path convertPath = getConvertLocation(username + sanitizedFilename);
                    return loadFileAsResource(convertPath);
                } else {
                    throw new IOException("Could not download converted file with ID: " + id);
                }
            };

            // Submit the task for execution
            Future<Resource> future = executorService.submit(downloadTask);

            try {
                // Wait for the task to complete and get the result
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Error occurred during file download: " + e.getMessage(), e);
            } finally {
                // Shutdown the executor service
                executorService.shutdown();
            }
        }
    }

    private Resource loadFileAsResource(Path filePath) throws IOException {
        URI fileUri = filePath.toUri();
        try {
            return new UrlResource(fileUri);
        } catch (MalformedURLException e) {
            throw new IOException("Could not load file as resource: " + filePath, e);
        }
    }

    @Transactional
    public void deleteFile(String username, Long id) {
        try {
            // Delete from database

            Optional<StoredFile> storedFileOptional = fileRepository.findById(id);

            if (storedFileOptional.isPresent()) {

                fileRepository.delete(storedFileOptional.get());
            } else {
                System.err.println("File record with ID " + id + " not found.");
                return; // Optionally handle the case where record is not found
            }
            String fileName = storedFileOptional.get().getFileName();
            String sanitizedFilename = fileName.replace(" ", "_");
            Path filePath = getTargetLocation(username + sanitizedFilename);
            Path convertPath = getConvertLocation(username + sanitizedFilename);
            //delete from app
            if (Files.deleteIfExists(filePath)) {
                System.out.println("File deleted successfully: " + filePath);
            } else {
                System.err.println("File not found: " + filePath);
            }
            if (Files.deleteIfExists(convertPath)) {
                System.out.println("File Convert deleted successfully: " + convertPath);
            } else {
                System.err.println("File not found: " + convertPath);
            }

        } catch (IOException e) {
            System.err.println("Could not delete file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());

        }
    }

    public String getFileInfo(String fileName) {
        try {
            // Tạo đường dẫn đầy đủ đến tệp
            Path filePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize().resolve(fileName);

            // Kiểm tra xem tệp có tồn tại không
            if (Files.exists(filePath)) {
                // Lấy thông tin về tệp
                long fileSize = Files.size(filePath);
                String contentType = Files.probeContentType(filePath);
                String absolutePath = filePath.toAbsolutePath().toString();

                // Tạo chuỗi thông tin về tệp
                return String.format("File Info: \nName: %s\nSize: %d bytes\nContent Type: %s\nPath: %s",
                        fileName, fileSize, contentType, absolutePath);
            } else {
                return "File not found.";
            }
        } catch (IOException e) {
            return "Error retrieving file info: " + e.getMessage();
        }
    }

    public List<FileResponseDTO> getAllFile() {
        List<StoredFile> allFile = fileRepository.findAll();
        return allFile.stream()
                .map(fileResponseDTOMapper::toFileResponseDTO)
                .collect(Collectors.toList());
    }

    public FileResponseDTO getFile(Long id) {
        return fileRepository.findById(id)
                .map(fileResponseDTOMapper::toFileResponseDTO)
                .orElseGet(() -> new FileResponseDTO("File record with ID " + id + " not found."));
    }

}
