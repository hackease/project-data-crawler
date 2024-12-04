package com.project.dataCrawler.utility;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class ThreadRunnable implements Runnable {
    
    private final List<String> regNoList;
    private final List<String> dobList;
    private final int start;
    private final int end;
    
    @Override
    public void run() {
        for (int i=start; i<end; i++) {
            String regNo = regNoList.get(i);
            
            if (!alreadyExists(regNo)) {
                int statusCode = 204;
                
                for (String dob : dobList) {
                    statusCode = statusCode(regNo, dob);
                    if (statusCode == 201) break;
                }
                
                if (statusCode == 201)
                    log.info("Data fetched and saved successfully for {}!", regNo);
                else
                    log.info("Data not found for {}!", regNo);
            }
            
            try {
                // Delete the Reg No from the txt file
                deleteUsedRegNo(regNo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void deleteUsedRegNo(String regNo) throws IOException {
        // TXT file path
        Path txtFilePath = Path.of("Left_Student_List.txt");
        
        // Read all lines from the file
        List<String> lines = Files.readAllLines(txtFilePath);
        
        // Filter out the lines that contains regNo
        List<String> updatedLines = lines.stream()
                                            .filter(line -> !line.contains(regNo))
                                            .toList();
        
        // Write the updated lines back to the file
        Files.write(txtFilePath, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    // Check if RegNo already exists in the database
    private boolean alreadyExists(String regNo) {
        
        String url = "http://localhost:8080/api/crawler/exists?regNo="+regNo;
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            HttpRequest request = HttpRequest.newBuilder()
                                          .uri(URI.create(url))
                                          .GET()
                                          .build();
            
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            
            int responseCode = response.statusCode();
            
            return responseCode == 200;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Make POST request and save Http Status Code
    private int statusCode(String regNo, String dob) {
        String url = "http://localhost:8080/api/crawler/fetch?regNo="+regNo+"&dob="+dob;
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            HttpRequest request = HttpRequest.newBuilder()
                                          .uri(URI.create(url))
                                          .POST(HttpRequest.BodyPublishers.noBody())
                                          .build();
            
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            
            return response.statusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
