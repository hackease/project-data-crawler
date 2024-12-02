package com.project.dataCrawler;

import com.project.dataCrawler.utility.ApplicationStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataCrawlerApplication {
    
    public static void main(String[] args) {
		SpringApplication.run(DataCrawlerApplication.class, args);
        
        // Start the Application
        ApplicationStarter applicationStarter = new ApplicationStarter();
        
        try {
            // Populate data (RegNo & DOB)
            applicationStarter.populateData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Execute threads
        applicationStarter.startThreads();
        
	}
 
}
