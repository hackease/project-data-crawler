package com.project.dataCrawler.utility;

import com.project.dataCrawler.config.AppConfig;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ApplicationStarter {
    
    private final AppConfig appConfig;
    
    private final List<String> regNoList = new ArrayList<>();
    private final List<String> dobList = new ArrayList<>();
    
    public ApplicationStarter(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    public void populateData() throws IOException {
        List<String> cleanedData;
        
        // TXT file path
        Path txtFilePath = Path.of("Left_Student_List.txt");
        
        // Check if the TXT file exists
        if (!Files.exists(txtFilePath)) {
            // Create TXT file
            Files.createFile(txtFilePath);
            
            // PDF file path
            String pdfFilePath = appConfig.getFilePath();
            
            // Extract and clean data from the PDF
            cleanedData = extractAndCleanDataFromPdf(pdfFilePath);
            
            // Write cleaned data to the TXT file
            writeToTxtFile(cleanedData, txtFilePath);
        }
        
        String txtFileContent = Files.readString(txtFilePath);
        
        // Match the regex for your registration number format
        Matcher matcher = Pattern.compile("\\b[A-Z]{3}\\d{8}\\b").matcher(txtFileContent);
        
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
    
    private void writeToTxtFile(List<String> data, Path txtFilePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(txtFilePath.toUri()))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
    
    private List<String> extractAndCleanDataFromPdf(String pdfFilePath) throws IOException {
        List<String> result;
        
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            
            result = Arrays.stream(text.split("\n"))                                // Split by newline to create lines
                             .map(line -> line.split("\\s+"))                       // Split each line into words
                             .filter(words -> words.length > 1)                           // Ensure line has enough words
                             .map(words -> words[1])                                      // Extract the second word
                             .filter(word -> word.matches("[A-Z]{3}[0-9]{8}"))      // Filter by Registration No. pattern
                             .toList();                                                   // Collect into a list
        }
        
        return result;
    }
    
    public void startThreads() {
        // Number of threads to use
        int threadCount = 10;
        
        // Create a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // Divide work among threads
        int chunkSize = regNoList.size() / threadCount;
        List<Runnable> threads = new ArrayList<>();
        
        for (int i=0; i<threadCount; i++) {
            int start = i * chunkSize;
            int end = (i == threadCount - 1) ? regNoList.size() : start + chunkSize;
            
            // Create a thread for this chunk
            threads.add(new ThreadRunnable(regNoList, dobList, start, end));
        }
        
        // Submit all tasks to the executor
        for (Runnable thread : threads) {
            executor.submit(thread);
        }
        
        // Shutdown executor after all tasks are done
        executor.shutdown();
    }
}
