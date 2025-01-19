package com.example.springbootwebsocketangularv002.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allow all paths
                .allowedOrigins("http://localhost:4200")  // Allow requests from your frontend (Angular running on localhost:4200)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allow all necessary HTTP methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true)  // Allow cookies or authorization headers (if necessary)
                .maxAge(3600);  // Cache preflight response for 1 hour
    }
}
