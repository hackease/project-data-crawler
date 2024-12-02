package com.project.dataCrawler.controllers;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import com.project.dataCrawler.services.DataCrawlerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crawler")
public class DataCrawlerController {
    
    private final DataCrawlerService dataCrawlerService;
    
    DataCrawlerController(DataCrawlerService dataCrawlerService) {
        this.dataCrawlerService = dataCrawlerService;
    }
    
    @PostMapping(path = "/fetch")
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
    
    @GetMapping(path = "/exists")
    public ResponseEntity<Void> checkIfRegNoExists(@RequestParam String regNo) {
        if (dataCrawlerService.checkIfRegNoExists(regNo))
            return new ResponseEntity<>(HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
}
