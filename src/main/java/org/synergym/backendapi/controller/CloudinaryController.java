package org.synergym.backendapi.controller;

import org.synergym.backendapi.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


//Cloudinary 이미지 업로드 컨트롤러
//사용자 프로필 이미지, 운동 사진 등의 이미지 파일을 Cloudinary에 업로드
@RestController
@RequestMapping("/api/cloudinary")
public class CloudinaryController {
    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // 이미지 업로드
    // 이미지 파일을 Cloudinary에 업로드하고 업로드된 이미지의 URL을 반환
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("CloudinaryController: 파일 업로드 요청, 파일명=" + file.getOriginalFilename());
            String url = cloudinaryService.uploadImage(file);
            System.out.println("CloudinaryController: 업로드 성공, URL=" + url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("업로드 실패: " + e.getMessage());
        }
    }
    
}
