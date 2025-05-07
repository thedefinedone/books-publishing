package com.records.books.controllers;

import com.records.books.entities.Book;
import com.records.books.entities.PublishingDate;
import com.records.books.services.BookService;
import com.records.books.services.PublishingDateService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing Book entities.
 */
@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final PublishingDateService publishingDateService;

    public BookController(BookService bookService, PublishingDateService publishingDateService) {
        this.bookService = bookService;
        this.publishingDateService = publishingDateService;
    }

    /** 
     * Displays the list of all books.
     */
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    /** 
     * Displays the form to add a new book.
     */
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
        return "books-form";
    }

    /** 
     * Adds a new book with validation and error handling.
     */
    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult result,
            @RequestParam(required = false) Long publishingDateId,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid book details: " + result.getAllErrors());
            return "redirect:/books/add";
        }

        try {
            if (publishingDateId != null) {
                PublishingDate publishingDate = publishingDateService.getPublishingDateById(publishingDateId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid publishing date ID: " + publishingDateId));
                book.setPublishingDate(publishingDate);
            }

            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("message", "✅ Book added successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/books/add";
        }

        return "redirect:/books";
    }

    /** 
     * Updates the title of an existing book with validation.
     */
    @PostMapping("/updateBookTitle")
    public String updateBookTitle(
            @RequestParam Long bookId,
            @RequestParam String newTitle,
            RedirectAttributes redirectAttributes) {

        if (newTitle == null || newTitle.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Book title cannot be empty.");
            return "redirect:/books";
        }

        try {
            bookService.updateBookTitle(bookId, newTitle.trim());
            redirectAttributes.addFlashAttribute("message", "✅ Book title updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update book title: " + e.getMessage());
        }

        return "redirect:/books";
    }

    /** 
     * Displays books associated with a specific publishing date.
     */
    @GetMapping("/books-by-date")
    public String getBooksByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        List<Book> books = publishingDateService.getBooksByPublishingDate(date);
        model.addAttribute("books", books);
        model.addAttribute("selectedDate", date);
        return "books-by-date";
    }
}