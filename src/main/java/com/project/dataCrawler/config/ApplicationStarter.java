package com.project.dataCrawler.config;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ApplicationStarter {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);
    @Autowired
    private AppConfig appConfig;
    
    private final List<String> regNoList = new ArrayList<>();
    private final List<String> dobList = new ArrayList<>();
    
    public void populateData() throws IOException {
        // TXT file path
        Path txtFilePath = Path.of(appConfig.getTxtFilePath());
        
        // Check if the TXT file exists
        if (!Files.exists(txtFilePath)) {
            // Create an empty TXT file
            Files.createFile(txtFilePath);
            
            // PDF file path
            String pdfFilePath = appConfig.getPdfFilePath();
            
            // Extract and clean Reg No from the PDF
            List<String> extractedRegNoList = extractRegNoFromPdf(pdfFilePath);
            
            // Write remaining cleaned Reg No to the TXT file
            Files.write(txtFilePath, extractedRegNoList);
        }
        
        // Match the regex for your registration number format
        Matcher matcher = Pattern.compile("\\b[A-Z]{3}\\d{8}\\b")
                                  .matcher(Files.readString(txtFilePath));
        
        // Populate regNo List
        while (matcher.find()) {
            regNoList.add(matcher.group());
        }
        
        // Populate dob List from 01/08/2005 to 31/07/2012
        for (int year=2005; year<=2012; year++) {
            for (int month=1; month<=12; month++) {
                for (int date=1; date<=31; date++) {
                    if ((year == 2005 && month < 8) || (year == 2012 && month > 7)) continue;
                    dobList.add(String.format("%02d/%02d/%04d", date, month, year));
                }
            }
        }
        
    }
    
    private List<String> extractRegNoFromPdf(String pdfFilePath) throws IOException {
        List<String> result;
        
        PDDocument document = PDDocument.load(new File(pdfFilePath));
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String pdfText = pdfTextStripper.getText(document);
        document.close();
        
        result = Arrays.stream(pdfText.split("\n"))                             // Split by newline to create lines
                         .map(line -> line.split("\\s+"))                       // Split each line into words
                         .filter(words -> words.length > 1)                           // Ensure line has enough words
                         .map(words -> words[1])                                      // Extract the second word
                         .filter(word -> word.matches("[A-Z]{3}[0-9]{8}"))      // Filter by Registration No. pattern
                         .toList();                                                   // Collect into a list
        
        return result;
    }
    
    public void startThreads() {
        HttpClient client = HttpClient.newHttpClient();
        
        regNoList.parallelStream().forEach(regNo -> {
            int statusCode = 204;
            
            for (String dob : dobList) {
                String url = "http://localhost:8080/api/crawler/fetch?regNo="+regNo+"&dob="+dob;
                
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                                                  .uri(URI.create(url))
                                                  .POST(HttpRequest.BodyPublishers.noBody())
                                                  .build();
                    
                    HttpResponse<String> response = client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );
                    
                    statusCode = response.statusCode();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                
                if (statusCode == 201) break;
            }
            
            if (statusCode == 201)
                log.info("Data fetched and saved successfully for {}!", regNo);
            else
                log.info("Data not found for {}!", regNo);
            
            try {
                // Delete the Reg No from the txt file
                deleteUsedRegNo(regNo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    private void deleteUsedRegNo(String regNo) throws IOException {
        // TXT file path
        Path txtFilePath = Path.of(appConfig.getTxtFilePath());
        
        // Read all lines from the file
        List<String> lines = Files.readAllLines(txtFilePath);
        
        // Filter out the lines that contains regNo
        List<String> updatedLines = lines.stream()
                                            .filter(line -> !line.contains(regNo))
                                            .toList();
        
        // Write the updated lines back to the file
        Files.write(txtFilePath, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
