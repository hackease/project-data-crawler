package com.project.dataCrawler.services;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import com.project.dataCrawler.domain.entities.UserDetailsEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DataCrawlerService {
    
    UserDetailsDto fetchAndSaveDetails(String regNo, String dob);
    
}
