package com.project.dataCrawler;

import com.project.dataCrawler.utility.ThreadRunnable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class DataCrawlerApplication {
    
    private static final List<String> regNoList = new ArrayList<>();
    private static final List<String> dobList = new ArrayList<>();
    
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
            
            // Number of threads to use
            int threadCount = 8;
            
            // Create a fixed thread pool
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            
            // Divide work among threads
            int chunkSize = regNoList.size() / threadCount;
            List<Runnable> threads = new ArrayList<>();
            
            for (int i=0; i<threadCount; i++) {
                int start = i * chunkSize;
                int end = (i == threadCount - 1) ? regNoList.size() : start + chunkSize;
                
                // Create a task for this chunk
                threads.add(new ThreadRunnable(regNoList, dobList, start, end));
            }
            
            // Submit all tasks to the executor
            for (Runnable thread : threads) {
                executor.submit(thread);
            }
            
            // Shutdown executor after all tasks are done
            executor.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
 
}
