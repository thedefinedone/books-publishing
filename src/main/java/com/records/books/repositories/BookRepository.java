package com.records.books.repositories;

import com.records.books.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.publishingDate pd WHERE pd.date = :date")
    List<Book> findBooksByPublishingDate(@Param("date") LocalDate date);
}