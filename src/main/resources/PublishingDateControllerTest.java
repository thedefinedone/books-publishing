package com.records.books.controllers;

import com.records.books.entities.PublishingDate;
import com.records.books.entities.Book;
import com.records.books.services.PublishingDateService;
import com.records.books.repositories.BookRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PublishingDateController.
 */
@ExtendWith(MockitoExtension.class)
public class PublishingDateControllerTest {

    @Mock
    private PublishingDateService publishingDateService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PublishingDateController publishingDateController;

    @BeforeEach
    public void setUp() {
        reset(publishingDateService, bookRepository, model, session, bindingResult, redirectAttributes);
    }

    /**
     * Tests listPublishingDates when not in edit mode.
     */
    @Test
    public void testListPublishingDates_notInEditMode() {
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);
        when(session.getAttribute("editingMode")).thenReturn(null);
        when(session.getAttribute("editId")).thenReturn(null);

        String viewName = publishingDateController.listPublishingDates(model, session);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", any(PublishingDate.class));
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", false);
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests listPublishingDates when in edit mode with a valid publishing date.
     */
    @Test
    public void testListPublishingDates_inEditMode() {
        Long editId = 1L;
        PublishingDate publishingDate = new PublishingDate();
        publishingDate.setId(editId);
        publishingDate.setDate(LocalDate.of(2005, 5, 5));
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(session.getAttribute("editingMode")).thenReturn(true);
        when(session.getAttribute("editId")).thenReturn(editId);
        when(publishingDateService.getPublishingDateById(editId)).thenReturn(Optional.of(publishingDate));
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);

        String viewName = publishingDateController.listPublishingDates(model, session);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", publishingDate);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", true);
        verify(session, never()).removeAttribute("editingMode");
        verify(session, never()).removeAttribute("editId");
    }

    /**
     * Tests listPublishingDates when in edit mode but the publishing date is not found.
     */
    @Test
    public void testListPublishingDates_inEditMode_publishingDateNotFound() {
        Long editId = 1L;
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(session.getAttribute("editingMode")).thenReturn(true);
        when(session.getAttribute("editId")).thenReturn(editId);
        when(publishingDateService.getPublishingDateById(editId)).thenReturn(Optional.empty());
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);

        String viewName = publishingDateController.listPublishingDates(model, session);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", any(PublishingDate.class));
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", false);
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests showEditPublishingDateForm when the publishing date is found.
     */
    @Test
    public void testShowEditPublishingDateForm_success() {
        Long id = 1L;
        PublishingDate publishingDate = new PublishingDate();
        publishingDate.setId(id);
        publishingDate.setDate(LocalDate.of(2005, 5, 5));
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(publishingDateService.getPublishingDateById(id)).thenReturn(Optional.of(publishingDate));
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);

        String viewName = publishingDateController.showEditPublishingDateForm(id, model, session, redirectAttributes);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", publishingDate);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", true);
        verify(session).setAttribute("editingMode", true);
        verify(session).setAttribute("editId", id);
    }

    /**
     * Tests showEditPublishingDateForm when the publishing date is not found.
     */
    @Test
    public void testShowEditPublishingDateForm_notFound() {
        Long id = 1L;
        when(publishingDateService.getPublishingDateById(id)).thenReturn(Optional.empty());

        String viewName = publishingDateController.showEditPublishingDateForm(id, model, session, redirectAttributes);

        assertEquals("redirect:/publishingDates", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid publishing date ID: " + id);
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests addPublishingDate with valid input.
     */
    @Test
    public void testAddPublishingDate_success() {
        PublishingDate publishingDate = new PublishingDate();
        publishingDate.setDate(LocalDate.of(2023, 1, 1));
        List<Long> bookIds = List.of(1L, 2L);
        List<Book> books = new ArrayList<>();
        Book book1 = new Book("Book 1", "Fiction");
        book1.setId(1L);
        Book book2 = new Book("Book 2", "Non-Fiction");
        book2.setId(2L);
        books.add(book1);
        books.add(book2);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookRepository.findAllById(bookIds)).thenReturn(books);
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());
        when(publishingDateService.getAllPublishingDates()).thenReturn(new ArrayList<>());

        String viewName = publishingDateController.addPublishingDate(publishingDate, bindingResult, bookIds, model, session, redirectAttributes);

        assertEquals("redirect:/publishingDates", viewName);
        verify(publishingDateService).savePublishingDate(publishingDate, books);
        verify(bookRepository).save(book1);
        verify(bookRepository).save(book2);
        verify(redirectAttributes).addFlashAttribute("message", "Publishing date added successfully!");
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests addPublishingDate with validation errors.
     */
    @Test
    public void testAddPublishingDate_validationErrors() {
        PublishingDate publishingDate = new PublishingDate();
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);

        String viewName = publishingDateController.addPublishingDate(publishingDate, bindingResult, null, model, session, redirectAttributes);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", publishingDate);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", false);
        verify(model).addAttribute("error", anyString());
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests addPublishingDate with a DataIntegrityViolationException.
     */
    @Test
    public void testAddPublishingDate_dataIntegrityViolation() {
        PublishingDate publishingDate = new PublishingDate();
        List<Long> bookIds = List.of(1L);
        List<Book> books = new ArrayList<>();
        Book book = new Book("Book 1", "Fiction");
        book.setId(1L);
        books.add(book);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookRepository.findAllById(bookIds)).thenReturn(books);
        when(bookRepository.findAll()).thenReturn(books);
        when(publishingDateService.getAllPublishingDates()).thenReturn(new ArrayList<>());
        doThrow(new DataIntegrityViolationException("Duplicate date")).when(publishingDateService).savePublishingDate(any(), any());

        String viewName = publishingDateController.addPublishingDate(publishingDate, bindingResult, bookIds, model, session, redirectAttributes);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("error", "Error: Duplicate or invalid date.");
        verify(model).addAttribute("editingMode", false);
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests updatePublishingDate with valid input.
     */
    @Test
    public void testUpdatePublishingDate_success() {
        PublishingDate publishingDate = new PublishingDate();
        publishingDate.setId(1L);
        publishingDate.setDate(LocalDate.of(2023, 1, 1));
        List<Long> bookIds = List.of(1L, 2L);
        List<Book> books = new ArrayList<>();
        Book book1 = new Book("Book 1", "Fiction");
        book1.setId(1L);
        Book book2 = new Book("Book 2", "Non-Fiction");
        book2.setId(2L);
        books.add(book1);
        books.add(book2);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(bookRepository.findAllById(bookIds)).thenReturn(books);
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());
        when(publishingDateService.getAllPublishingDates()).thenReturn(new ArrayList<>());

        String viewName = publishingDateController.updatePublishingDate(publishingDate, bindingResult, bookIds, model, session, redirectAttributes);

        assertEquals("redirect:/publishingDates", viewName);
        verify(publishingDateService).updatePublishingDate(publishingDate, books);
        verify(bookRepository).save(book1);
        verify(bookRepository).save(book2);
        verify(redirectAttributes).addFlashAttribute("message", "Publishing date updated successfully!");
        verify(session).removeAttribute("editingMode");
        verify(session).removeAttribute("editId");
    }

    /**
     * Tests updatePublishingDate with null ID.
     */
    @Test
    public void testUpdatePublishingDate_nullId() {
        PublishingDate publishingDate = new PublishingDate();
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);

        String viewName = publishingDateController.updatePublishingDate(publishingDate, bindingResult, null, model, session, redirectAttributes);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", publishingDate);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", true);
        verify(model).addAttribute("error", "Publishing date ID cannot be null.");
        verify(session).setAttribute("editingMode", true);
        verify(session).setAttribute("editId", null);
    }

    /**
     * Tests searchPublishingDateByDate when the publishing date is found.
     */
    @Test
    public void testSearchPublishingDateByDate_success() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        PublishingDate publishingDate = new PublishingDate();
        publishingDate.setId(1L);
        publishingDate.setDate(date);
        List<PublishingDate> publishingDates = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        when(publishingDateService.findByDate(date)).thenReturn(Optional.of(publishingDate));
        when(publishingDateService.getAllPublishingDates()).thenReturn(publishingDates);
        when(bookRepository.findAll()).thenReturn(books);

        String viewName = publishingDateController.searchPublishingDateByDate(date, model, session, redirectAttributes);

        assertEquals("publishing-dates", viewName);
        verify(model).addAttribute("publishingDates", publishingDates);
        verify(model).addAttribute("publishingDate", publishingDate);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("editingMode", true);
        verify(session).setAttribute("editingMode", true);
        verify(session).setAttribute("editId", publishingDate.getId());
    }

    /**
     * Tests searchPublishingDateByDate when the publishing date is not found.
     */
    @Test
    public void testSearchPublishingDateByDate_notFound() {
        LocalDate date = LocalDate.of(2023, 1, 1);
        when(publishingDateService.findByDate(date)).thenReturn(Optional.empty());

        String viewName = publishingDateController.searchPublishingDateByDate(date, model, session, redirectAttributes);

        assertEquals("redirect:/publishingDates", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "No publishing date found for " + date);
    }
}