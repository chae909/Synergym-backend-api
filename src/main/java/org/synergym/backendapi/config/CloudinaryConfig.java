package org.synergym.backendapi.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// 이미지 업로드 및 관리를 위한 Cloudinary 서비스 연결 설정
@Configuration
public class CloudinaryConfig {

    private final CloudinaryProperties properties;

    public CloudinaryConfig(CloudinaryProperties properties) {
        this.properties = properties;
    }

    // Cloudinary Bean 등록
    // 애플리케이션에서 이미지 업로드/관리 시 사용할 Cloudinary 인스턴스 생성
    @Bean
    public Cloudinary cloudinary() {
        System.out.println("[CloudinaryConfig] Cloudinary bean registered.");
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", properties.getCloudName(),
                "api_key", properties.getApiKey(),
                "api_secret", properties.getApiSecret()
        ));
    }
    
}
