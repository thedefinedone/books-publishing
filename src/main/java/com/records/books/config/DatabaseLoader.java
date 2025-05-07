package com.records.books.config;

import com.records.books.entities.Book;
import com.records.books.entities.PublishingDate;
import com.records.books.repositories.BookRepository;
import com.records.books.repositories.PublishingDateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);
    private final BookRepository bookRepository;
    private final PublishingDateRepository publishingDateRepository;

    public DatabaseLoader(BookRepository bookRepository, PublishingDateRepository publishingDateRepository) {
        this.bookRepository = bookRepository;
        this.publishingDateRepository = publishingDateRepository;
    }

    @Override
    public void run(String... args) {
        try {
            logger.info("⏳ Loading sample data...");

            // Create PublishingDates
            List<PublishingDate> publishingDates = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                PublishingDate date = new PublishingDate();
                date.setDate(LocalDate.of(2018 + (i % 5), (i % 12) + 1, 1)); // ✅ Ensures valid months
                publishingDates.add(date);
            }
            publishingDateRepository.saveAll(publishingDates);

            // Create Books
            List<Book> books = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Book book = new Book();
                book.setTitle("Book " + i);
                book.setGenre("Genre " + i);
                book.setPublishingDate(publishingDates.get(i % publishingDates.size())); // ✅ Ensures valid index
                books.add(book);
            }
            bookRepository.saveAll(books);

            logger.info("✅ Sample data successfully loaded: {} PublishingDates, {} Books", publishingDates.size(), books.size());
        } catch (Exception e) {
            logger.error("❌ Error loading sample data: {}", e.getMessage(), e);
        }
    }
}