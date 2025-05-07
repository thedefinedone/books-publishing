package com.records.books.controllers;

import com.records.books.entities.PublishingDate;
import com.records.books.entities.Book;
import com.records.books.services.PublishingDateService;
import com.records.books.repositories.BookRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing PublishingDate entities.
 */
@Controller
@RequestMapping("/publishingDates")
public class PublishingDateController {

    private final PublishingDateService publishingDateService;
    private final BookRepository bookRepository;

    public PublishingDateController(PublishingDateService publishingDateService, BookRepository bookRepository) {
        this.publishingDateService = publishingDateService;
        this.bookRepository = bookRepository;
    }

    /**
     * Displays the list of publishing dates and handles edit mode restoration.
     *
     * @param model the model to add attributes to
     * @param session the HTTP session to store edit state
     * @return the name of the view to render
     */
    @GetMapping
    public String listPublishingDates(Model model, HttpSession session) {
        System.out.println("Received request to list publishing dates");
        System.out.println("Session editingMode: " + session.getAttribute("editingMode"));
        System.out.println("Session editId: " + session.getAttribute("editId"));

        Boolean editingMode = (Boolean) session.getAttribute("editingMode");
        Long editId = (Long) session.getAttribute("editId");

        if (Boolean.TRUE.equals(editingMode) && editId != null) {
            System.out.println("Restoring edit mode for ID: " + editId);
            Optional<PublishingDate> publishingDateOpt = publishingDateService.getPublishingDateById(editId);
            if (publishingDateOpt.isPresent()) {
                model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
                model.addAttribute("publishingDate", publishingDateOpt.get());
                model.addAttribute("books", bookRepository.findAll());
                model.addAttribute("editingMode", true);
                System.out.println("Returning publishing-dates.jsp in edit mode for ID: " + editId);
                return "publishing-dates";
            } else {
                System.out.println("Publishing date not found for ID: " + editId + ", clearing session");
                session.removeAttribute("editingMode");
                session.removeAttribute("editId");
            }
        }

        List<PublishingDate> publishingDates = publishingDateService.getAllPublishingDates();
        System.out.println("Listing publishing dates: " + publishingDates.size() + " entries found");
        model.addAttribute("publishingDates", publishingDates);
        model.addAttribute("publishingDate", new PublishingDate());
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("editingMode", false);
        System.out.println("Returning publishing-dates.jsp in list mode with editingMode = false");

        session.removeAttribute("editingMode");
        session.removeAttribute("editId");
        System.out.println("Session cleared, editingMode: " + session.getAttribute("editingMode") + ", editId: " + session.getAttribute("editId"));

        return "publishing-dates";
    }

    /**
     * Displays the form to edit an existing publishing date.
     *
     * @param id the ID of the publishing date to edit
     * @param model the model to add attributes to
     * @param session the HTTP session to store edit state
     * @param redirectAttributes attributes for redirect scenarios
     * @return the name of the view to render, or a redirect URL
     */
    @GetMapping("/edit/{id}")
    public String showEditPublishingDateForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("Received edit request for ID: " + id);
        Optional<PublishingDate> publishingDateOpt = publishingDateService.getPublishingDateById(id);
        if (publishingDateOpt.isEmpty()) {
            System.out.println("Publishing date not found for ID: " + id);
            redirectAttributes.addFlashAttribute("error", "Invalid publishing date ID: " + id);
            session.removeAttribute("editingMode");
            session.removeAttribute("editId");
            return "redirect:/publishingDates";
        }

        System.out.println("Publishing date found: " + publishingDateOpt.get().getDate());
        model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
        model.addAttribute("publishingDate", publishingDateOpt.get());
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("editingMode", true);

        session.setAttribute("editingMode", true);
        session.setAttribute("editId", id);
        System.out.println("Session updated, editingMode: " + session.getAttribute("editingMode") + ", editId: " + session.getAttribute("editId"));

        System.out.println("Returning publishing-dates.jsp in edit mode for ID: " + id + " with editingMode = true");
        return "publishing-dates";
    }

    /**
     * Adds a new publishing date and associates it with the specified books.
     *
     * @param publishingDate the PublishingDate to add
     * @param result the binding result for validation errors
     * @param bookIds the IDs of the books to associate
     * @param model the model to add attributes to
     * @param session the HTTP session to store edit state
     * @param redirectAttributes attributes for redirect scenarios
     * @return the name of the view to render, or a redirect URL
     */
    @PostMapping("/add")
    public String addPublishingDate(@Valid @ModelAttribute PublishingDate publishingDate, BindingResult result,
                                    @RequestParam(value = "bookIds", required = false) List<Long> bookIds, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("Received add request: " + publishingDate);
        if (result.hasErrors()) {
            System.out.println("Validation errors in addPublishingDate: " + result.getAllErrors());
            model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
            model.addAttribute("publishingDate", publishingDate);
            model.addAttribute("books", bookRepository.findAll());
            model.addAttribute("editingMode", false);
            model.addAttribute("error", "Invalid publishing date: " + result.getAllErrors());
            session.removeAttribute("editingMode");
            session.removeAttribute("editId");
            return "publishing-dates";
        }
        try {
            List<Book> books = (bookIds != null && !bookIds.isEmpty()) ? bookRepository.findAllById(bookIds) : List.of();
            publishingDateService.savePublishingDate(publishingDate, books);
            for (Book book : books) {
                book.setPublishingDate(publishingDate);
                bookRepository.save(book);
            }
            redirectAttributes.addFlashAttribute("message", "Publishing date added successfully!");
        } catch (DataIntegrityViolationException e) {
            System.out.println("Data integrity violation in addPublishingDate: " + e.getMessage());
            model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
            model.addAttribute("publishingDate", publishingDate);
            model.addAttribute("books", bookRepository.findAll());
            model.addAttribute("editingMode", false);
            model.addAttribute("error", "Error: Duplicate or invalid date.");
            session.removeAttribute("editingMode");
            session.removeAttribute("editId");
            return "publishing-dates";
        }
        session.removeAttribute("editingMode");
        session.removeAttribute("editId");
        return "redirect:/publishingDates";
    }

    /**
     * Updates an existing publishing date and associates it with the specified books.
     *
     * @param publishingDate the PublishingDate to update
     * @param result the binding result for validation errors
     * @param bookIds the IDs of the books to associate
     * @param model the model to add attributes to
     * @param session the HTTP session to store edit state
     * @param redirectAttributes attributes for redirect scenarios
     * @return the name of the view to render, or a redirect URL
     */
    @PostMapping("/update")
    public String updatePublishingDate(@Valid @ModelAttribute PublishingDate publishingDate, BindingResult result,
                                       @RequestParam(value = "bookIds", required = false) List<Long> bookIds, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("Received update request for ID: " + publishingDate.getId());
        if (result.hasErrors()) {
            System.out.println("Validation errors in updatePublishingDate: " + result.getAllErrors());
            model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
            model.addAttribute("publishingDate", publishingDate);
            model.addAttribute("books", bookRepository.findAll());
            model.addAttribute("editingMode", true);
            model.addAttribute("error", "Invalid publishing date: " + result.getAllErrors());
            session.setAttribute("editingMode", true);
            session.setAttribute("editId", publishingDate.getId());
            return "publishing-dates";
        }
        if (publishingDate.getId() == null) {
            System.out.println("Publishing date ID is null in updatePublishingDate");
            model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
            model.addAttribute("publishingDate", publishingDate);
            model.addAttribute("books", bookRepository.findAll());
            model.addAttribute("editingMode", true);
            model.addAttribute("error", "Publishing date ID cannot be null.");
            session.setAttribute("editingMode", true);
            session.setAttribute("editId", publishingDate.getId());
            return "publishing-dates";
        }

        try {
            List<Book> books = (bookIds != null && !bookIds.isEmpty()) ? bookRepository.findAllById(bookIds) : List.of();
            publishingDateService.updatePublishingDate(publishingDate, books);
            for (Book book : books) {
                book.setPublishingDate(publishingDate);
                bookRepository.save(book);
            }
            redirectAttributes.addFlashAttribute("message", "Publishing date updated successfully!");
        } catch (DataIntegrityViolationException e) {
            System.out.println("Data integrity violation in updatePublishingDate: " + e.getMessage());
            model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
            model.addAttribute("publishingDate", publishingDate);
            model.addAttribute("books", bookRepository.findAll());
            model.addAttribute("editingMode", true);
            model.addAttribute("error", "Error: Duplicate or invalid date.");
            session.setAttribute("editingMode", true);
            session.setAttribute("editId", publishingDate.getId());
            return "publishing-dates";
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal argument in updatePublishingDate: " + e.getMessage());
            model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
            model.addAttribute("publishingDate", publishingDate);
            model.addAttribute("books", bookRepository.findAll());
            model.addAttribute("editingMode", true);
            model.addAttribute("error", e.getMessage());
            session.setAttribute("editingMode", true);
            session.setAttribute("editId", publishingDate.getId());
            return "publishing-dates";
        }
        session.removeAttribute("editingMode");
        session.removeAttribute("editId");
        return "redirect:/publishingDates";
    }

    /**
     * Searches for a publishing date by date and displays it in edit mode if found.
     *
     * @param date the date to search for
     * @param model the model to add attributes to
     * @param session the HTTP session to store edit state
     * @param redirectAttributes attributes for redirect scenarios
     * @return the name of the view to render, or a redirect URL
     */
    @GetMapping("/search")
    public String searchPublishingDateByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("Received search request for date: " + date);
        Optional<PublishingDate> publishingDateOpt = publishingDateService.findByDate(date);
        if (publishingDateOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No publishing date found for " + date);
            return "redirect:/publishingDates";
        }

        PublishingDate publishingDate = publishingDateOpt.get();
        model.addAttribute("publishingDates", publishingDateService.getAllPublishingDates());
        model.addAttribute("publishingDate", publishingDate);
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("editingMode", true);

        session.setAttribute("editingMode", true);
        session.setAttribute("editId", publishingDate.getId());
        System.out.println("Session updated, editingMode: " + session.getAttribute("editingMode") + ", editId: " + session.getAttribute("editId"));

        System.out.println("Returning publishing-dates.jsp in edit mode for date: " + date);
        return "publishing-dates";
    }
}