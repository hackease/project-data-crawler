package com.project.dataCrawler.services;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import org.springframework.stereotype.Service;

@Service
public interface DataCrawlerService {
    
    UserDetailsDto fetchAndSaveDetails(String regNo, String dob);
    
}
