package com.records.books.services;

import com.records.books.entities.Book;
import com.records.books.repositories.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBookTitle(Long bookId, String newTitle) {  // ✅ Allows book name edits
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setTitle(newTitle);  // ✅ Updates book name
            bookRepository.save(book);
        }
    }
}