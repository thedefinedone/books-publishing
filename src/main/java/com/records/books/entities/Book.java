package com.records.books.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String genre;

    @ManyToOne
    @JoinColumn(name = "publishing_date_id")
    private PublishingDate publishingDate;

    // Constructors
    public Book() {
    }

    public Book(String title, String genre) {
        this.title = title;
        this.genre = genre;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public PublishingDate getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(PublishingDate publishingDate) {
        this.publishingDate = publishingDate;
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', genre='" + genre + "', publishingDateId=" + 
               (publishingDate != null ? publishingDate.getId() : "null") + "}";
    }

}