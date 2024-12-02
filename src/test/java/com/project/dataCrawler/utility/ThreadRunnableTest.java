package com.project.dataCrawler.utility;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ThreadRunnableTest {
    
    @Test
    public void testRun() {
        String regNo = "Match";
        List<String> dobList = List.of("UnMatch", "unMatch", "UnMatch");
        
        if (!alreadyExists()) {
            int statusCode = 204;
            
            int index = 0;
            for (String dob : dobList) {
                
                statusCode = statusCode(regNo, dob);
                
                if (statusCode == 201) break;
                
                index++;
            }
            
            System.out.println("Index -> "+index);
            
            if (statusCode == 201)
                System.out.println("Data fetched and saved successfully!");
            else
                System.out.println("Data not found!");
        } else {
            System.out.println("Data doesn't exists!");
        }
    }
    
    private boolean alreadyExists() {
        return false;
    }
    
    private int statusCode(String regNo, String dob) {
        if (regNo.equals(dob)) return 201;
        else return 204;
    }
    
}
