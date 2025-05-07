package com.records.books.services;

import com.records.books.entities.PublishingDate;
import com.records.books.entities.Book;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

/**
 * Service interface for managing PublishingDate entities.
 */
public interface PublishingDateService {

    /**
     * Retrieves all publishing dates from the database.
     *
     * @return a list of all PublishingDate entities
     */
    List<PublishingDate> getAllPublishingDates();

    /**
     * Retrieves a publishing date by its ID, including its associated books.
     *
     * @param id the ID of the publishing date to retrieve
     * @return an Optional containing the PublishingDate, or empty if not found
     */
    Optional<PublishingDate> getPublishingDateById(Long id);

    /**
     * Saves a new publishing date and associates it with the specified books.
     *
     * @param publishingDate the PublishingDate entity to save
     * @param books the list of Books to associate with the publishing date
     */
    void savePublishingDate(PublishingDate publishingDate, List<Book> books);

    /**
     * Updates an existing publishing date and associates it with the specified books.
     *
     * @param publishingDate the PublishingDate entity to update
     * @param books the list of Books to associate with the publishing date
     * @throws IllegalArgumentException if the publishing date does not exist
     */
    void updatePublishingDate(PublishingDate publishingDate, List<Book> books);

    /**
     * Finds a publishing date by its date.
     *
     * @param date the LocalDate to search for
     * @return an Optional containing the PublishingDate, or empty if not found
     */
    Optional<PublishingDate> findByDate(LocalDate date);

    /**
     * Retrieves all books associated with a publishing date identified by the given date.
     *
     * @param date the LocalDate of the publishing date
     * @return a list of Books associated with the publishing date, or an empty list if not found
     */
    default List<Book> getBooksByPublishingDate(LocalDate date) {
        return Collections.emptyList(); // ✅ Ensures safe default return for missing books
    }
}