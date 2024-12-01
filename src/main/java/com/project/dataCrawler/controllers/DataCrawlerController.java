package com.project.dataCrawler.controllers;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import com.project.dataCrawler.services.DataCrawlerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawler")
public class DataCrawlerController {
    
    private final DataCrawlerService dataCrawlerService;
    
    DataCrawlerController(DataCrawlerService dataCrawlerService) {
        this.dataCrawlerService = dataCrawlerService;
    }
    
    @PostMapping("/fetch")
    public ResponseEntity<UserDetailsDto> fetchAndSaveDetails(
            @RequestParam String regNo,
            @RequestParam String dob
    ) {
        UserDetailsDto userDetailsDto = dataCrawlerService.fetchAndSaveDetails(regNo, dob);
        
        if (userDetailsDto.getId() != null)
            return new ResponseEntity<>(userDetailsDto, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
