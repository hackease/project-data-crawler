package com.project.dataCrawler.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userdetails")
public class UserDetailsEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "reg_no", length = 15)
    private String regNo;
    
    @Column(name = "dob", length = 15)
    private String dob;
    
    @Column(name = "full_name", length = 250)
    private String fullName;
    
    @Column(name = "mobile_no", length = 15)
    private String mobileNo;
    
    @Column(name = "email_id", length = 100)
    private String emailId;
    
    @Column(name = "standard", length = 5)
    private String standard;
    
    @Column(name = "gender", length = 10)
    private String gender;
    
    @Column(name = "school", length = 150)
    private String school;
    
    @Column(name = "region", length = 100)
    private String region;
    
    @Column(name = "tentative_score", length = 150)
    private String score;
    
}
