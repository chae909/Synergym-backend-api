package org.synergym.backendapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    //이미지 파일을 Cloudinary에 업로드
    public String uploadImage(MultipartFile file) throws IOException {
        System.out.println("CloudinaryService: 업로드 시작");
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        System.out.println("CloudinaryService: 업로드 성공, URL=" + uploadResult.get("secure_url"));
        return uploadResult.get("secure_url").toString(); // 업로드된 이미지의 HTTPS 주소
    }
    
}