package com.project.dataCrawler.services.impl;

import com.project.dataCrawler.domain.dtos.UserDetailsDto;
import com.project.dataCrawler.domain.entities.UserDetailsEntity;
import com.project.dataCrawler.mappers.UserDetailsMapper;
import com.project.dataCrawler.repositories.UserDetailsRepository;
import com.project.dataCrawler.services.DataCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DataCrawlerServiceImpl implements DataCrawlerService {
    
    private static final String URL = "https://ioqm.manageexam.com/RMO/Login";
    
    private final UserDetailsRepository userDetailsRepository;
    
    private final UserDetailsMapper userDetailsMapper;
    
    public DataCrawlerServiceImpl(UserDetailsRepository userDetailsRepository, UserDetailsMapper userDetailsMapper) {
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsMapper = userDetailsMapper;
    }
    
    @Override
    public UserDetailsDto fetchAndSaveDetails(String regNo, String dob) {
        
        UserDetailsDto result = new UserDetailsDto();
        
        try {
            // Establish a connection and fetch the GET response
            Connection.Response getResponse = Jsoup.connect(URL)
                                                      .method(Connection.Method.GET)
                                                      .execute();
            
            // Extract cookies from the response
            Map<String, String> cookies = getResponse.cookies();
            
            // Fetch the get webpage
            Document getDocument = getResponse.parse();
            
            // Locate the input element of __RequestVerificationToken
            Element inputElement = getDocument.selectFirst("input[name=__RequestVerificationToken]");
            
            // Extract the __RequestVerificationToken of the form
            String requestVerificationToken = inputElement != null ? inputElement.attr("value") : "";
            
            // URL-encoded key-value pairs
            Map<String, String> formData = new HashMap<>();
            formData.put("__RequestVerificationToken", requestVerificationToken);
            formData.put("RegNo", regNo);
            formData.put("DOB", dob);
            
            // Submit the form with the required data and fetch the POST response
            Connection.Response postResponse = Jsoup.connect(URL)
                                                       .cookies(cookies)
                                                       .data(formData)
                                                       .method(Connection.Method.POST)
                                                       .execute();
            
            // Fetch the post Webpage
            Document postDocument = postResponse.parse();
            
            // Extract required fields and store in details
            Map<String, String> details = extractDetails(postDocument);
            
            UserDetailsEntity userDetails = new UserDetailsEntity();
            
            // Check if RegNo and DOB matches with details fields
            if (details.get("Reg No").equals(regNo) && details.get("DOB").equals(dob)) {
                
                // Save details into the database
                userDetails.setRegNo(regNo);
                userDetails.setDob(dob);
                userDetails.setFullName(details.get("Full Name"));
                userDetails.setMobileNo(details.get("Mobile No"));
                userDetails.setEmailId(details.get("Email ID"));
                userDetails.setStandard(details.get("Standard"));
                userDetails.setGender(details.get("Gender"));
                userDetails.setSchool(details.get("School"));
                userDetails.setRegion(details.get("Region"));
                userDetails.setScore(details.get("Tentative Score"));
                
                UserDetailsEntity savedUserDetails = userDetailsRepository.save(userDetails);
                
                if (!userDetailsRepository.existsByRegNo(regNo))
                    result = userDetailsMapper.toDto(savedUserDetails);
            }
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean checkIfRegNoExists(String regNo) {
        return userDetailsRepository.existsByRegNo(regNo);
    }
    
    private static Map<String, String> extractDetails(Document document) {
        Map<String, String> detailsMap = getFieldMap();
        Map<String, String> resultMap = new HashMap<>();
        
        detailsMap.forEach((key, value) -> {
            String temp = "";
            Element labelElement = document.selectFirst("label:containsOwn("+value+")");
            if (labelElement != null) {
                Element nextSibling = labelElement.nextElementSibling();
                if (nextSibling != null) {
                    if (value.equals("Mobile No :")) {
                        nextSibling = nextSibling.nextElementSibling();
                        if (nextSibling != null) temp = nextSibling.text();
                    } else {
                        temp = nextSibling.text();
                    }
                }
            }
            if (key.equals("First Name") || key.equals("Middle Name") || key.equals("Last Name")) {
                detailsMap.put(key, temp);
            } else {
                resultMap.put(key, temp);
            }
        });
        
        String fullName = String.format(
                "%s %s %s",
                detailsMap.get("First Name"),
                detailsMap.get("Middle Name"),
                detailsMap.get("Last Name")
        ).trim().replaceAll("\\s+", " ");
        
        resultMap.put("Full Name", fullName);
        
        return resultMap;
    }
    
    private static Map<String, String> getFieldMap() {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("Reg No", "Registration No :");
        fieldMap.put("DOB", "Date Of Birth :");
        fieldMap.put("First Name", "First Name :");
        fieldMap.put("Middle Name", "Middle Name :");
        fieldMap.put("Last Name", "Last Name :");
        fieldMap.put("Mobile No", "Mobile No :");
        fieldMap.put("Email ID", "Email Id :");
        fieldMap.put("Standard", "Class/Standard :");
        fieldMap.put("Gender", "Gender :");
        fieldMap.put("School", "School studying in :");
        fieldMap.put("Region", "Region :");
        fieldMap.put("Tentative Score", "Tentative Score :");
        
        return fieldMap;
    }
    
}
