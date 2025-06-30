package org.synergym.backendapi.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry regi){
    regi.addMapping("/**")
            .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:5175")
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
            .allowedHeaders("*");
  }

}
