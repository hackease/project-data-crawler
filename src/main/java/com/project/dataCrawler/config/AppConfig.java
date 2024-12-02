package com.project.dataCrawler.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
    
    @Value("${app.file.path}")
    private String filePath;
    
}
