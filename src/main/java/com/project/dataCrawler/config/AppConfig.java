package com.project.dataCrawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    
    @Value("${app.file.path}")
    private String filePath;
    
    public String getFilePath() {
        return filePath;
    }

}
