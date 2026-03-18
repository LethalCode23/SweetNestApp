package com.dh.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    String store(MultipartFile file, String subfolder);
    void delete(String fileUrl);
}