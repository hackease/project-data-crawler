package com.project.dataCrawler.controllers;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import com.project.dataCrawler.services.DataCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crawler")
public class DataCrawlerController {
    
    @Autowired
    private DataCrawlerService dataCrawlerService;
    
    @PostMapping(path = "/fetch")
    public ResponseEntity<UserDetailsDto> fetchAndSaveDetails(
            @RequestParam String regNo,
            @RequestParam String dob
    ) {
        UserDetailsDto userDetailsDto = dataCrawlerService.fetchAndSaveDetails(regNo, dob);
        
        if (userDetailsDto.getId() != null && !userDetailsDto.getDob().equals("Not in range"))
            return new ResponseEntity<>(userDetailsDto, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
