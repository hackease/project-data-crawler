package com.project.dataCrawler;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SpringBootApplication
public class DataCrawlerApplication {
    
    public static void main(String[] args) {
		SpringApplication.run(DataCrawlerApplication.class, args);
        
        // Path to your PDF file
        String pdfFilePath = "Student_List.pdf";
        
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            // Extract text from the PDF
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            
            // Match the regex for your registration number format
            Matcher matcher = Pattern.compile("\\b[A-Z]{3}\\d{8}\\b").matcher(text);
            
            int numberOfDetailsFetched = 0;
            
            // Find and check all matches
            while (matcher.find()) {
                String regNo = matcher.group();
                int statusCode = 204;
                
                for (int year=2007; year<=2010; year++) {
                    // Break if Status Code is 201
                    if (statusCode == 201) break;
                    
                    for (int month=1; month<=12; month++) {
                        // Break if Status Code is 201
                        if (statusCode == 201) break;
                        
                        for (int date=1; date<=31; date++) {
                            // Break if Status Code is 201
                            if (statusCode == 201) break;
                            
                            String dob = String.format("%02d/%02d/%04d", date, month, year);
                            
                            String url = "http://localhost:8080/api/crawler/fetch?regNo="+regNo+"&dob="+dob;
                            
                            HttpClient client = HttpClient.newHttpClient();
                            
                            HttpRequest request = HttpRequest.newBuilder()
                                                          .uri(URI.create(url))
                                                          .POST(HttpRequest.BodyPublishers.noBody())
                                                          .build();
                            
                            HttpResponse<String> response = client.send(
                                    request,
                                    HttpResponse.BodyHandlers.ofString()
                            );
                            
                            statusCode = response.statusCode();
                        }
                    }
                }
                
                numberOfDetailsFetched++;
                
                if (statusCode == 201)
                    log.info("Details fetched and saved successfully for {}! -> {}", regNo, numberOfDetailsFetched);
                else
                    log.info("Details not found for {}! -> {}", regNo, numberOfDetailsFetched);
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
 
}
