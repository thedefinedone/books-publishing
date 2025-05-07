package com.records.books.repositories;

import com.records.books.entities.PublishingDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PublishingDateRepository extends JpaRepository<PublishingDate, Long> {

    @Query("SELECT pd FROM PublishingDate pd JOIN FETCH pd.books WHERE pd.id = :id")
    Optional<PublishingDate> findPublishingDateWithBooks(@Param("id") Long id);

    Optional<PublishingDate> findByDate(LocalDate date);
}