package com.jinjin.bidsystem.controller;

import com.jinjin.bidsystem.service.FileService;
import com.jinjin.bidsystem.service.ExceptionService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/files")

public class FileController {
    @Autowired
    private FileService fileService;

    // 파일 업로드 엔드포인트
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileService.uploadFile(file);
            return ResponseEntity.ok().body("업로드 성공: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 파일 다운로드 엔드포인트
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        try {
            Resource resource = fileService.downloadFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new NotFoundException("다운로드 파일을 찾을 수 없습니다: " + fileName);
        }
    }
}
