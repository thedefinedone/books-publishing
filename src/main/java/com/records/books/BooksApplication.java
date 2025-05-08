package com.records.books;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class BooksApplication {
    private static final Logger logger = LoggerFactory.getLogger(BooksApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BooksApplication.class, args);
        
        
        String startupTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("✅ Books And Publishing Records Application Started Successfully at {}", startupTime);
    }
}