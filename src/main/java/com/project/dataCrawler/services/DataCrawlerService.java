package com.project.dataCrawler.services;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DataCrawlerService {
    
    ResponseEntity<UserDetailsDto> fetchAndSaveDetails(String regNo, String dob);
    
    boolean checkIfRegNoExists(String regNo);
    
}
