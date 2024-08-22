package com.example.authenticationauthorization.controller;

import com.example.authenticationauthorization.dto.FileRequestDTO;
import com.example.authenticationauthorization.dto.FileResponseDTO;
import com.example.authenticationauthorization.dto.ResponseDTO;

import com.example.authenticationauthorization.dto.UserResponseDTO;
import com.example.authenticationauthorization.service.FilesService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class FilesController {
    private final FilesService fileService;

    public FilesController(FilesService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userName") String userName) throws IOException {
        try {
            // Assuming fileService.storeFile and fileService.convertFile are correctly implemented
            FileResponseDTO fileResponseDTO = fileService.storeFile(Objects.requireNonNull(file.getOriginalFilename()), file.getBytes(), userName);
            fileService.convertFile(fileResponseDTO.getFileId(), userName);
            return new ResponseEntity<>(new ResponseDTO(("File " + file.getOriginalFilename() + " uploaded successfully"),"200","true"), HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(new ResponseDTO("Could not upload file: " + ex.getMessage(),"404","fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/convert")
    public ResponseEntity<ResponseDTO> convertFileToPdf(@RequestBody FileResponseDTO fileResponseDTO) {
        try {
            Long fileId = fileResponseDTO.getFileId();
            String userName = fileResponseDTO.getUserName();

            // Convert the file
            fileService.convertFile(fileId, userName);

            // Respond with a success message
            return new ResponseEntity<>(new ResponseDTO("File " + fileId + " converted successfully","200","true"), HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(new ResponseDTO("Could not convert file: " + ex.getMessage(),"404","fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadConvertedFile(@RequestBody FileResponseDTO fileResponseDTO) {
        try {
            // Retrieve the converted file resource
            Resource resource = fileService.downloadConvert(fileResponseDTO.getFileId(), fileResponseDTO.getUserName());

            // Return the file as a response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> deleteFile(@RequestBody FileRequestDTO fileRequestDTO, @RequestBody UserResponseDTO userResponseDTO) {
        try {
            fileService.deleteFile(fileRequestDTO.getFileName(), fileRequestDTO.getFileId());
            return new ResponseEntity<>(new ResponseDTO("Delete file successful","200","true"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("Error delete file" + e.getMessage(),"404","false")
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //lay thong tin 1 file
    @GetMapping("/info")
    public ResponseEntity<FileResponseDTO> getFileInfo(@RequestBody FileResponseDTO fileRequestDTO) {
        try {
            FileResponseDTO file = fileService.getFile(fileRequestDTO.getFileId());
            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new FileResponseDTO("Error get file infor" + e.getMessage())
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/info/extra")
    public ResponseEntity<FileResponseDTO> getDetailInfor(@RequestBody FileRequestDTO fileRequestDTO) {
        try {
            String file = fileService.getFileInfo(fileRequestDTO.getFileName());
            return new ResponseEntity<>(new FileResponseDTO(file), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new FileResponseDTO("Error get file infor detail" + e.getMessage())
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get all file info in db
    @GetMapping
    public ResponseEntity<?> getFiles() {
        try {
            List<FileResponseDTO> file = fileService.getAllFile();

            if (file.isEmpty()) {
                // Return 204 No Content if no users are found
                return new ResponseEntity<>(new ResponseDTO("No file found!", "404", "false"), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            ResponseDTO responseDTO = new ResponseDTO("Error getting all users " + e.getMessage(), "404", "false");
            // Return 500 Internal Server Error with the error details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
