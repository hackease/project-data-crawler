package com.project.dataCrawler.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    
    private Long id;
    
    private String regNo;
    
    private String dob;
    
    private String fullName;
    
    private String mobileNo;
    
    private String emailId;
    
    private String standard;
    
    private String gender;
    
    private String school;
    
    private String region;
    
    private String score;
    
}
