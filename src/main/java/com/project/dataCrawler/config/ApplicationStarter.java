package com.project.dataCrawler.config;

import com.project.dataCrawler.services.DataCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Component
public class ApplicationStarter {
    
    @Autowired
    private AppConfig appConfig;
    
    @Autowired
    private DataCrawlerService dataCrawlerService;
    
    private final List<String> regNoList = new ArrayList<>();
    private final List<String> dobList = new ArrayList<>();
    
    public void populateData() throws IOException {
        // PDF file path
        String pdfFilePath = appConfig.getPdfFilePath();
        
        // Extract and Populate new regNo List
        regNoList.addAll(extractRegNoFromPdf(pdfFilePath));
        
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
                         .filter(word -> !dataCrawlerService.checkIfRegNoExists(word))// Check if it doesn't exist in the database
                         .toList();                                                   // Collect into a list
        
        return result;
    }
    
    public void startThreads() {
        // Create a ForkJoinPool with a custom thread count
        ForkJoinPool customThreadPool = new ForkJoinPool(100);
        
        customThreadPool.submit(() -> {
            // Process every regNo with all dobs
            regNoList.parallelStream().forEach(regNo -> {
                // Process every dob with the regNo and find a match
                boolean fetch = dobList.stream().map(
                        dob -> dataCrawlerService.fetchAndSaveDetails(regNo, dob).getStatusCode().value()
                ).anyMatch(code -> code == 201);
                
                if (fetch)
                    log.info("Data fetched and saved successfully for {}", regNo);
                else
                    log.info("Data not found for {}", regNo);
            });
        }).join();
    }
}
