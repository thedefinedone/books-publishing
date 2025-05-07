package com.records.books.services;

import com.records.books.entities.PublishingDate;
import com.records.books.entities.Book;
import com.records.books.repositories.PublishingDateRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PublishingDateServiceImpl implements PublishingDateService {

    private final PublishingDateRepository publishingDateRepository;

    public PublishingDateServiceImpl(PublishingDateRepository publishingDateRepository) {
        this.publishingDateRepository = publishingDateRepository;
    }

    @Override
    public List<PublishingDate> getAllPublishingDates() {
        return publishingDateRepository.findAll();
    }

    @Override
    public Optional<PublishingDate> getPublishingDateById(Long id) {
        return publishingDateRepository.findPublishingDateWithBooks(id);
    }

    @Override
    public void savePublishingDate(PublishingDate publishingDate, List<Book> books) {
        publishingDate.setBooks(books);
        publishingDateRepository.save(publishingDate);
    }

    @Override
    public void updatePublishingDate(PublishingDate publishingDate, List<Book> books) {
        if (!publishingDateRepository.existsById(publishingDate.getId())) {
            throw new IllegalArgumentException("Publishing date with ID " + publishingDate.getId() + " does not exist.");
        }
        PublishingDate existing = publishingDateRepository.findPublishingDateWithBooks(publishingDate.getId())
                .orElseThrow(() -> new IllegalArgumentException("Publishing date with ID " + publishingDate.getId() + " does not exist."));
        for (Book book : existing.getBooks()) {
            book.setPublishingDate(null);
        }
        publishingDate.setBooks(books);
        publishingDateRepository.save(publishingDate);
    }

    @Override
    public Optional<PublishingDate> findByDate(LocalDate date) {
        return publishingDateRepository.findByDate(date);
    }

    @Override
    public List<Book> getBooksByPublishingDate(LocalDate date) {
        Optional<PublishingDate> publishingDateOpt = findByDate(date);
        return publishingDateOpt.map(PublishingDate::getBooks).orElseGet(ArrayList::new);
    }
}