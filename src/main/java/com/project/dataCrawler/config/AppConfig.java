package com.project.dataCrawler.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppConfig {
    
    @Value("${app.file.pdfFilePath}")
    private String pdfFilePath;
    
    @Value("${app.file.txtFilePath}")
    private String txtFilePath;
    
}
