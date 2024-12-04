package com.project.dataCrawler;

import com.project.dataCrawler.config.ApplicationStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DataCrawlerApplication {
    
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DataCrawlerApplication.class, args);
        
        // Start the Application
        ApplicationStarter applicationStarter = context.getBean(ApplicationStarter.class);
        
        try {
            // Populate data (RegNo & DOB)
            applicationStarter.populateData();
            
            // Execute threads
            applicationStarter.startThreads();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        
	}
 
}
